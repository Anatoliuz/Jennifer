package fix.jennifer;

import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;


public class PhotoActivity extends ActionBarActivity {
    String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             value = extras.getString("key");
            //The key argument here must match that used in the other activity
        }

        File imageFile = new  File(value);
        if(imageFile.exists()){
            ImageView imageView= (ImageView) findViewById(R.id.imageView);
            imageView.setRotation(90);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        }


    }

}