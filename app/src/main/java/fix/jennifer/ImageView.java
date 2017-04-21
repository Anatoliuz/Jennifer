package fix.jennifer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ImageView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        byte[] s = getIntent().getByteArrayExtra("GG");

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl);
        MyView mv = new MyView(this);
        relativeLayout.addView(mv);
    }
}
