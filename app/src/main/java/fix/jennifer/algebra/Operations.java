package fix.jennifer.algebra;


import fix.jennifer.Pair;
import fix.jennifer.ellipticcurves.EllipticCurve;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Operations
{
    public static final BigInteger TWO = new BigInteger("2");
    public static final BigInteger FOUR = new BigInteger("4");
    public static final BigInteger EIGHT = new BigInteger("8");
    public static final BigInteger P = new BigInteger("6277101735386680763835789423207666416083908700390324961279"); // порядок поля
    private static final int MESSAGE_LEN = 20;
    private static final int K = 50;   // задаем вероятность, что для данного сообщения мы не построим точку (1 / 2^k)
    private static final BigInteger PROBABILITY = new BigInteger("50");  // Большая форма для вероятности
    private static final BigInteger Pd2 = new BigInteger("3138550867693340381917894711603833208041954350195162480639");

    public static BigInteger getSecretKey()
    {
        String secretS = "";
        SecureRandom rand = new SecureRandom();

        for(int i = 0; i < Operations.MESSAGE_LEN; ++i)
            secretS += (char)(rand.nextInt(10) + '0');
        return new BigInteger(secretS);
    }

    public static Pair<byte[], ArrayList<Point>> encrypt(EllipticCurve curve, byte[] message, Point openKey) throws IOException
    {
        ArrayList<Point> helpers = new ArrayList<>();
        ArrayList<Pair<Point, Point>> cipherPoints = new ArrayList<>();
        int left = 0;
        int right = Math.min(MESSAGE_LEN , message.length);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while(left < message.length)
        {
            cipherPoints.add(getCipherPoint(curve, Arrays.copyOfRange(message, left, right), openKey));
            left += MESSAGE_LEN;
            right = Math.min(left + MESSAGE_LEN, message.length);
        }

        for(Pair<Point, Point> pair : cipherPoints)
        {
            byte[] cipherPointB = cipherPointToBytes(pair.getValue());
            out.write(cipherPointB.length);
            out.write(cipherPointB);
            helpers.add(pair.getKey());
        }
        return new Pair<>(out.toByteArray(), helpers);
    }

    public static byte[] decrypt(EllipticCurve curve, byte[] cipherText, BigInteger secretKey, ArrayList<Point> helpers)
            throws IOException
    {
        int left = 0;
        int len = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for(Point helper : helpers)
        {
            helper = algMult(curve, secretKey, helper);  // secretKey * kQ
            len = cipherText[left];
            byte[] cipherPointB = Arrays.copyOfRange(cipherText, left + 1, left + len + 1);
            Point cipherPoint = cipherNumToPoint(curve, new BigInteger(cipherPointB));   // secretKey * kQ + M
            cipherPoint = inverse(cipherPoint);
            Point text = sum(curve, cipherPoint, inverse(helper));

            out.write(pointToBytes(text));
            left += len + 1;
        }
        return out.toByteArray();
    }

    public static Point algMult(EllipticCurve curve, BigInteger alpha, Point point) // Возвращает alpha * point
    {
        Point res = curve.INF;
        Point tempP = new Point(point.getX(), point.getY());
        boolean wasInit = false;

        while(!alpha.equals(BigInteger.ZERO))
        {
            if(alpha.mod(TWO).equals(BigInteger.ONE))
            {
                if(wasInit)
                    res = sum(curve, res, tempP);
                else
                {
                    res = new Point(tempP.getX(), tempP.getY());
                    wasInit = true;
                }
            }
            alpha = alpha.shiftRight(1);
            tempP = doublePoint(curve, tempP);
        }
        return res;
    }

    private static Pair<Point, Point> getCipherPoint(EllipticCurve curve, byte[] messageB, Point openKey)
    {
        BigInteger message = new BigInteger(messageB);
        Point cipherText = numToPoint(curve, message);  // M
        Point helper = curve.getBasePoint();
        String sK = "";
        SecureRandom rand = new SecureRandom();

        for(int i = 0; i < MESSAGE_LEN; ++i)
            sK += (char)(rand.nextInt(10) + '0');

        BigInteger k = new BigInteger(sK);
        helper = algMult(curve, k, helper);  // k * Q
        openKey = algMult(curve, k, openKey); // k * secretKey * Q

        cipherText = sum(curve, cipherText, openKey);  // M + k * secretKey * Q
        return new Pair<>(helper, cipherText);
    }

    private static Point inverse(Point point)
    {
        return new Point(point.getX(), P.subtract(point.getY()));
    }

    private static Point numToPoint(EllipticCurve curve, BigInteger message)
    {
        BigInteger x = message.multiply(PROBABILITY);
        BigInteger y = BigInteger.ZERO;
        BigInteger y2;

        for(int i = 0; i < K; ++i)
        {
            y2 = curve.getY2(addIntToBig(x, i));
            if(hasRoot(y2))
            {
                y = root(y2);
                if(y.equals(BigInteger.ZERO))
                    continue;
                x = addIntToBig(x, i);
                break;
            }
        }
        return new Point(x, y);
    }

    private static Point cipherNumToPoint(EllipticCurve curve, BigInteger cipherText)
    {
        boolean isNeg = false;
        if(cipherText.signum() == -1)
        {
            isNeg = true;
            cipherText = cipherText.negate();
        }
        BigInteger y2 = curve.getY2(cipherText);
        while(!hasRoot(y2))
        {
            cipherText = addIntToBig(cipherText, 1);
            y2 = curve.getY2(cipherText);
        }
        y2 = root(y2);
        if(isNeg && y2.compareTo(Pd2) != -1 || !isNeg && y2.compareTo(Pd2) == -1)
            y2 = P.subtract(y2);
        return new Point(cipherText, y2);
    }

    private static byte[] pointToBytes(Point point) throws IOException
    {
        BigInteger x = point.getX().divide(new BigInteger(Integer.toString(K)));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(x.toByteArray());
        return out.toByteArray();
    }

    private static byte[] cipherPointToBytes(Point point) throws IOException
    {
        BigInteger x = point.getX();
        if(point.getY().compareTo(Pd2) == -1)
            x = x.negate();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(x.toByteArray());
        return out.toByteArray();
    }

    private static boolean hasRoot(BigInteger x2)
    {
        return JacobiSymbol(x2, P);
    }

    private static boolean JacobiSymbol(BigInteger x2, BigInteger mod)
    {
        int binDeg = 0;
        if(x2.equals(BigInteger.ONE))
            return true;
        while(x2.mod(TWO).equals(BigInteger.ZERO))
        {
            ++binDeg;
            x2 = x2.shiftRight(1);
        }
        if(binDeg == 0)
        {
            // если хотя бы одно из чисел в остатке от деления на 4 дает 1, то знак символа Якоби не меняется
            if(x2.mod(FOUR).equals(BigInteger.ONE) || mod.mod(FOUR).equals(BigInteger.ONE))
                return JacobiSymbol(mod.mod(x2), x2);
            else
                return !JacobiSymbol(mod.mod(x2), x2);
        }
        else if(binDeg % 2 == 0)
        {
            return JacobiSymbol(x2, mod);
        }
        else
        {
            BigInteger m8 = mod.mod(EIGHT);
            // если знаменатель символа в остатке от деления на 8 дает +- 1, то знак не меняется
            if(m8.equals(BigInteger.ONE) || m8.equals(EIGHT.subtract(BigInteger.ONE)))
                return JacobiSymbol(x2, mod);
            else
                return !JacobiSymbol(x2, mod);
        }
    }

    private static BigInteger root(BigInteger x2) // Алгоритм Чиполла
    {
        // Генерируем неприводимый многочлен вида z^2 + tz + x, задавая случайное t (перебор), вероятность, примерно, 0.5
        // Проверка на неприводимость - отсутствие корней, следовательно корень дискриминанта не вычислим
        // Корень дискриминанта не вычислим, когда символ Якоби = -1
        BigInteger t = BigInteger.ONE;
        BigInteger D = t.multiply(t).subtract(FOUR.multiply(x2)).mod(P);  // дискримнант = t^2 - 4x mod P
        while(JacobiSymbol(D, P))
        {
            t = t.add(BigInteger.ONE);
            D = t.multiply(t).subtract(FOUR.multiply(x2)).mod(P);
        }

        // корень из числа равен z^((P + 1)/2) mod z^2 + tz + x
        BigInteger deg = P.add(BigInteger.ONE).shiftRight(1);

        // при возведении многочлена в стпепень будем получать многочлен степени не выше 1, следовательно 2 коэффициента
        // az + b
        // вычислять будем разбивая степень на степени 2, сохраняя коэффициенты
        HashMap<Integer, Pair<BigInteger, BigInteger>> coefs = new HashMap<>();
        int curDeg = 0;
        BigInteger a = BigInteger.ONE;
        BigInteger b = BigInteger.ZERO;
        Pair<BigInteger, BigInteger> curPair = new Pair<>(a, b);
        Pair<BigInteger, BigInteger> ans = new Pair<>(BigInteger.ZERO, BigInteger.ONE);
        while(pow2IntCompBig(curDeg, deg) < 0)
        {
            coefs.put(curDeg, curPair);
            ++curDeg;
            curPair = multBinoms(curPair.getKey(), curPair.getValue(), curPair.getKey(), curPair.getValue(), t, x2);
        }
        while(!deg.equals(BigInteger.ZERO))
        {
            while(pow2IntCompBig(curDeg, deg) > 0)
                --curDeg;
            ans = multBinoms(ans.getKey(), ans.getValue(), coefs.get(curDeg).getKey(), coefs.get(curDeg).getValue(), t, x2);
            deg = deg.subtract(TWO.modPow(new BigInteger(Integer.toString(curDeg)), P));
        }
        // так как вычиления были в поле расширения, а корень из подполя, то а = 0, иначе, что-то пошло не так и начинаем сначала
        if(ans.getKey().equals(BigInteger.ZERO))
            return ans.getValue().mod(P);
        else
            return BigInteger.ZERO;
    }

    // (az + b) * (cz + d) mod z^2 +tz + x
    private static Pair<BigInteger, BigInteger>  multBinoms(BigInteger a, BigInteger b, BigInteger c,
                                                            BigInteger d, BigInteger t, BigInteger x)
    {
        // получим двучлен iz^2 + jz + k
        BigInteger i = a.multiply(c).mod(P);  // a * c mod P
        BigInteger j = a.multiply(d).add(c.multiply(b)).mod(P);  // a * d + c * b mod P
        BigInteger k = b.multiply(d).mod(P); // b * d mod P

        a = j.subtract(i.multiply(t)).mod(P);  // j - i * t mod P
        b = k .subtract(i.multiply(x)).mod(P); // k - i * x mod P
        return new Pair<>(a, b);
    }

    private static int pow2IntCompBig(int l, BigInteger r)  // 2^l < r
    {
        BigInteger lBig = TWO;
        lBig = lBig.modPow(new BigInteger(Integer.toString(l)), P);
        return lBig.compareTo(r);
    }

    private static BigInteger addIntToBig(BigInteger x, int k)
    {
        String kS = Integer.toString(k);
        return x.add(new BigInteger(kS));
    }

    private static Point sum(EllipticCurve curve, Point p, Point q)
    {
        return curve.sum(p, q);
    }

    private static Point doublePoint(EllipticCurve curve, Point point)
    {
        return curve.doublePoint(point);
    }
}
