package fix.jennifer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.Image;
import android.view.View;

class MyView extends View
{
    Image image;
    public MyView(Context context) {
        super(context);
        this.image = image;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.images), 100 ,100, null);
    }

}