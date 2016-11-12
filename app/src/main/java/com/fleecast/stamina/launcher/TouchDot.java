package com.fleecast.stamina.launcher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.fleecast.stamina.R;

/**
 * Created by nnt on 19/03/16.
 */
public class TouchDot extends ImageView {


    private int circleDotCol;
    private int circleCol;
    private float outerCirclesStorkThickness = 10.0f;

    private int circleAlpha = 255;

    private boolean iWantCircleSelected = false;
    double randRandi = Math.random();
    // radius
    private int radius = 0;

    // paint for draw a custom view
    private Padding padding;
    private Paint circlePaint;

    public TouchDot(Context context) {
        super(context);
        circlePaint = new Paint();

        setIWantCircleSelected(true);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    public TouchDot(Context context, AttributeSet attrs) {
        super(context, attrs);

        circlePaint = new Paint();

        setIWantCircleSelected(true);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TouchDot, 0, 0);

        try {
            circleCol = a.getInteger(R.styleable.TouchDot_circleColor, 0);

        } finally {
            a.recycle();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        setBackgroundColor(Color.TRANSPARENT);

        drawMatrix(canvas);

    }

    public void setPadding(Padding padding){

        this.padding = padding;


    }
    public Padding getPadding(){
        return this.padding;
    }

    private void drawMatrix(Canvas canvas){


        int viewWidthHalf = this.getMeasuredWidth() / 2;
        int viewHeightHalf = this.getMeasuredHeight() / 2;

        if (viewWidthHalf > viewHeightHalf) {
            radius = viewHeightHalf - 10;
        } else {
            radius = viewWidthHalf - 10;
        }

        if(!getIWantCircleSelected()) {
            // circle
            circlePaint.setStyle(Paint.Style.FILL);
            //circlePaint.setStrokeWidth(outerCirclesStorkThickness);
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(circleCol);
            //circlePaint.setAlpha(circleAlpha);

            canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, circlePaint);

        }
        else {

            circlePaint.setStyle(Paint.Style.FILL);
            //circlePaint.setStrokeWidth(outerCirclesStorkThickness);

            circlePaint.setAntiAlias(true);

            circlePaint.setColor(circleCol);

//            circlePaint.setAlpha(circleAlpha);

            //Paint circlePaint1 = new Paint();
            //circlePaint.setMaskFilter(new BlurMaskFilter(30f, BlurMaskFilter.Blur.OUTER));

            canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, circlePaint);

            Log.e("BBBBBBBBBb","" + circleCol);

/*
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setStrokeWidth(outerCirclesStorkThickness);
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(circleCol);
            circlePaint.setAlpha(circleAlpha);
            Paint _paintBlur = new Paint();
            _paintBlur.set(circlePaint);
            _paintBlur.setColor(Color.argb(127, 74, 138, 255));
            _paintBlur.setStrokeWidth(30f);
            _paintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
*/

            //canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, _paintBlur);

        }


    }

    public int getCircleCol() {
        return circleCol;
    }

    public void setCircleCol(int circleCol) {
        this.circleCol = circleCol;
        // redraw the view, call onDraw()
        invalidate();
        requestLayout();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean getIWantCircleSelected() {
        return iWantCircleSelected;
    }

    public void setIWantCircleSelected(boolean iWantCircleSelected) {
        this.iWantCircleSelected = iWantCircleSelected;
    }

    public int getCircleAlpha() {
        return circleAlpha;
    }

    public void setCircleAlpha(int circleAlpha) {
        this.circleAlpha = circleAlpha;
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {

        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_HOVER_ENTER:
                Log.e("ACTION_HOVER_ENTER",  randRandi + "  ");
                event.setAction(MotionEvent.ACTION_DOWN);
                break;
            case MotionEvent.ACTION_HOVER_MOVE:
                Log.e("ACTION_HOVER_MOVE",  randRandi + "  ");
                event.setAction(MotionEvent.ACTION_MOVE);
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                Log.e("ACTION_HOVER_EXIT",  randRandi + "  ");
                event.setAction(MotionEvent.ACTION_UP);
                break;
        }
        onTouchEvent(event);
        event.setAction(action);

        return super.onHoverEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("ACTION_DOWN",  randRandi + "  ");
                return true;
            case MotionEvent.ACTION_UP:
                Log.e("ACTION_UP",  randRandi + "  ");
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.e("ACTION_MOVE",  randRandi + "  ");
                return true;
            case MotionEvent.ACTION_CANCEL:
                Log.e("ACTION_CANCEL",  randRandi + "  ");
                return true;
        }
        return false;
    }


    private class Padding {
        private int top=0;
        private int right=0;
        private int bottom =0;
        private int left=0;

        public Padding(int bottom, int left, int right, int top) {
            this.bottom = bottom;
            this.left = left;
            this.right = right;
            this.top = top;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getBottom() {
            return bottom;
        }

        public void setBottom(int bottom) {
            this.bottom = bottom;
        }


    }


}
