package com.nike.uploads.tasks;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.nike.uploads.MainActivity;

/**
 * Created by Fez on 8/1/2014.
 */
public class PagerLinearLayout extends LinearLayout {

    private float scale = MainActivity.BIG_SCALE;

    public PagerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PagerLinearLayout(Context context) {
        super(context);
    }

    public void setScaleBoth(float scale) {
        this.scale = scale;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // The main mechanism to display scale animation, you can customize it
        // as your needs
        int w = this.getWidth();
        int h = this.getHeight();
        canvas.scale(scale, scale, w/2, h/2);

        super.onDraw(canvas);
    }
}
