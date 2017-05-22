package fix.jennifer.algebra;

import java.math.BigInteger;


public class Point
{
    private BigInteger x;
    private BigInteger y;

    public Point(BigInteger x, BigInteger y)
    {
        this.x = x;
        this.y = y;
    }

    public BigInteger getX()
    {
        return x;
    }

    public BigInteger getY()
    {
        return y;
    }
}
