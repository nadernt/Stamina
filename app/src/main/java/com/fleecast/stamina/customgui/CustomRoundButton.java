package com.fleecast.stamina.customgui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.GroupsModel;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Utility;

/**
 * Created by nnt on 19/03/16.
 */
public class CustomRoundButton extends ImageView {


    private Context context;
    private Drawable icon;
/*
    <attr name="circle_center_color" format="color" />
    <attr name="circle_border_color" format="color" />
    <attr name="circle_label" format="string" />
    <attr name="label_color" format="color" />
*/

    private int circleCenterColor;
    private int outerCirclesStorkColor;
    private int textColor;
    private float outerCirclesStorkThickness = 5.0f;
    private int buttonAlpha = 255;
    private int iconSize;
    private int textSize;
    private int app_group_code;

    // radius
    private float radius = 0;

    // paint for draw a custom view
    private Padding padding;

    private String buttonText;
    private int categoryInt;
    private boolean blHighLightMe=false;
    //  because regular setId() of view can be cause of conflict then we make our one!
    private GroupsModel groupsModel;

    public CustomRoundButton(Context context, String buttonText, int textSize, Drawable icon, int iconSize, float circleRadius,
                             int circleCenterColor, int outerCirclesStorkColor, int textColor) {
        super(context);

        this.context = context;



        this.buttonText = buttonText;
        this.icon = new Utility().resizeIcon(icon, Constants.ICONS_RENDER_QUALITY_HIGH,Constants.ICONS_RENDER_QUALITY_ALIAS);

        this.circleCenterColor = circleCenterColor;
        this.outerCirclesStorkColor = outerCirclesStorkColor;
        this.textColor= textColor;
        this.iconSize = iconSize;
        this.textSize = textSize;
        this.radius = circleRadius;

        setBackgroundColor(Color.TRANSPARENT);
        //context.parentView.getBackground().setAlpha(128); //your parent view's visibility is now %50 and child view's visibility remains same.
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    public CustomRoundButton(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public CustomRoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomRoundButton, 0, 0);

        try {
            circleCenterColor = a.getInteger(R.styleable.CustomRoundButton_circle_center_color, 0);

        } finally {
            a.recycle();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int viewWidthHalf = this.getMeasuredWidth() / 2;
        int viewHeightHalf = this.getMeasuredHeight() / 2;

        double distanceOfClock = Math.sqrt((viewWidthHalf - event.getX()) * (viewWidthHalf - event.getX()) + (viewHeightHalf - event.getY()) * (viewHeightHalf - event.getY()));
        if(calcPixelIndependent(radius) >= distanceOfClock)
            return super.onTouchEvent(event);
        else
            return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setBackgroundColor(Color.TRANSPARENT);

       // drawImageViewShape(canvas);

        Paint mCanvas = new Paint();
        Utility utility = new Utility();

        int viewWidthHalf = this.getMeasuredWidth() / 2;
        int viewHeightHalf = this.getMeasuredHeight() / 2;

        if(blHighLightMe) {
            // circle
            mCanvas.setStyle(Paint.Style.FILL);
            mCanvas.setAntiAlias(true);
            mCanvas.setColor(circleCenterColor);
            //mCanvas.setAlpha(buttonAlpha);

            canvas.drawCircle(viewWidthHalf, viewHeightHalf, calcPixelIndependent(radius), mCanvas);


            mCanvas.setStyle(Paint.Style.STROKE);
            mCanvas.setStrokeWidth(outerCirclesStorkThickness);
            mCanvas.setAntiAlias(true);
            mCanvas.setColor(outerCirclesStorkColor);
            //mCanvas.setAlpha(buttonAlpha);

            canvas.drawCircle(viewWidthHalf, viewHeightHalf, calcPixelIndependent(radius), mCanvas);

        }


        // circle icon
        int bitmapWidthHeight = (int) utility.getSystemIndependentPixel(context,iconSize);
        Bitmap bitmap = convertToBitmap(icon,bitmapWidthHeight,bitmapWidthHeight);

        int bitmapXPos = viewWidthHalf - bitmap.getWidth()/2;
        int bitmapYPos = viewHeightHalf - bitmap.getHeight()/2;
        canvas.drawBitmap(bitmap, bitmapXPos, bitmapYPos, mCanvas);

        // buttonText

        //mCanvas.setStrokeWidth(3);
        mCanvas.setStyle(Paint.Style.FILL);
        mCanvas.setTextAlign(Paint.Align.CENTER);
       // mCanvas.setUnderlineText(true);
        mCanvas.setAntiAlias(true);
        mCanvas.setColor(textColor);
        mCanvas.setTextSize(textSize);
        mCanvas.setShadowLayer(2.0f, 5.0f, 5.0f, Color.BLACK);
        //Log.e("Acha", utility.wordEllipsizeMaker(buttonText,12,15));

        canvas.drawText(Utility.wordEllipsizeMaker(buttonText, 12, 15), viewWidthHalf, this.getMeasuredHeight()-6 , mCanvas);

        bitmap.recycle();
        bitmap=null;
        // TextView tv = new TextView(context);


        /*Rect rectText = new Rect();
        paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

        newCanvas.drawText(captionString,
                0, rectText.height(), paintText);
        */



/*
            mCanvas.setStyle(Paint.Style.FILL);
            mCanvas.setStrokeWidth(outerCirclesStorkThickness);
            mCanvas.setAntiAlias(true);
            mCanvas.setColor(circleCenterColor);
            mCanvas.setAlpha(buttonAlpha);
            Paint _paintBlur = new Paint();
            _paintBlur.set(mCanvas);
            _paintBlur.setColor(Color.argb(127, 74, 138, 255));
            _paintBlur.setStrokeWidth(30f);
            _paintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
*/

        //canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, _paintBlur);





    }

    @Override
    public void setOnDragListener(OnDragListener l) {
        super.setOnDragListener(l);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {

        if(event.getAction() == DragEvent.ACTION_DRAG_ENTERED)
        {
            redrawButton(true);
        }

        if(event.getAction() == DragEvent.ACTION_DRAG_EXITED)
        {
            redrawButton(false);
        }

        return super.onDragEvent(event);
    }

    public void redrawButton(boolean isClearHighlightCircle){
        blHighLightMe=isClearHighlightCircle;
        invalidate();
        requestLayout();
    }

    public Drawable getIcon() {
        return icon;
    }

/* public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    *//**
     * This method converts device specific pixels to device independent pixels.
     *
     * @param px
     *            A value in px (pixels) unit. Which we need to convert into db
     * @param context
     *            Context to get resources and device specific display metrics
     * @return A float value to represent db equivalent to px value
     *//*
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;

    }*/

    private float calcPixelIndependent(float pixelToConvert){

        float scale = getResources().getDisplayMetrics().density;
        //return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixelToConvert, getResources().getDisplayMetrics());
        return (pixelToConvert * scale + 0.5f);

    }


    public int getAppGroupCode() {
        return app_group_code;
    }

    public void setAppGroupCode(int app_group_code) {
        this.app_group_code = app_group_code;
    }

    public void setPadding(Padding padding){

        this.padding = padding;
    }

    public Padding getPadding(){
        return this.padding;
    }

    private void drawImageViewShape(Canvas canvas){

    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        invalidate();
        requestLayout();
    }

    public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {

        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    public int getCategoryInt() {
        return categoryInt;
    }

    public void setCategoryInt(int categoryInt) {
        this.categoryInt = categoryInt;
    }

    public int getCircleCenterColor() {
        return circleCenterColor;
    }

    public void setCircleCenterColor(int circleCenterColor) {
        this.circleCenterColor = circleCenterColor;
        // redraw the view, call onDraw()
        invalidate();
        requestLayout();
    }

    public GroupsModel getGroupsModel() {
        return groupsModel;
    }

    public void setGroupsModel(GroupsModel groupsModel) {
        this.groupsModel = groupsModel;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }


    public int getButtonAlpha() {
        return buttonAlpha;
    }

    public void setButtonAlpha(int buttonAlpha) {
        this.buttonAlpha = buttonAlpha;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = this.buttonText;
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
