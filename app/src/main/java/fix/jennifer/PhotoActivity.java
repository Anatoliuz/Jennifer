package fix.jennifer;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import fix.jennifer.algebra.Operations;
import fix.jennifer.algebra.Point;
import fix.jennifer.config.HelperFactory;
import fix.jennifer.ellipticcurves.EllipticCurve;
import fix.jennifer.executor.DefaultExecutorSupplier;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

import java.util.ArrayList;

public class PhotoActivity extends ActionBarActivity {
    String value;

    private ProgressDialog mDialog;
    private final int mTotalTime = 70;
    byte[] temp;

    boolean done;
    Bitmap bitmap;

    Handler h;
    Handler hh;
    int myProgress = 0;

    Handler.Callback hc = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            if (msg.what == 1){
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setRotation(90);
                mDialog.dismiss();

                imageView.setImageBitmap(bitmap);
            }
            return false;
        }
    };



    void decryptImage() {


        DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        Bundle extras = getIntent().getExtras();
                        if (extras != null) {
                            value = extras.getString("key");
                        }
                        try {
                            File imageFile = new File(value);

                            byte[] file = readContentIntoByteArray(imageFile);
                            EllipticCurve curve = HelperFactory.getHelper().getCurve();
                            BigInteger secretKey = HelperFactory.getHelper().getSecretKey();

                            temp = Operations.decrypt(curve, file, secretKey, new ArrayList<Point>());

                            if (imageFile.exists() ) {

                                bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                                h.sendEmptyMessageDelayed(1,0);
                                myProgress = 100;

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setProgress(0);
        mDialog.setMax(mTotalTime);
        mDialog.show();
        h = new Handler(hc);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int counter = 0;

                while (counter < mTotalTime) {//counter < mTotalTime
                    try {
                        Thread.sleep(100);
                        counter++;
                        mDialog.setProgress(counter);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }

        }).start();
        decryptImage();


    }

    private static byte[] readContentIntoByteArray(File file)
    {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try
        {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
            for (int i = 0; i < bFile.length; i++)
            {
                System.out.print((char) bFile[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bFile;
    }

}


