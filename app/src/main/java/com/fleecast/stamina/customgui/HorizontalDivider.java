package com.fleecast.stamina.customgui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by nnt on 17/04/16.
 */
public class HorizontalDivider extends TextView {
    private  float dividerLineThickness = 5.0f;
    private  int dividerLineColor= -1;

    public HorizontalDivider(Context context, AttributeSet attrs){
        super(context, attrs);
      
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure( widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension( getMeasuredWidth(),getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas){

        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        canvas.save();
        
        Paint paint = getPaint();

        if(dividerLineColor>-1)
            paint.setColor(dividerLineColor);
        else
            paint.setColor(getCurrentTextColor());

        paint.setStrokeWidth(dividerLineThickness);

        canvas.drawLine(0f, (float) getHeight()-dividerLineThickness, (float) getMeasuredWidth(), (float) getMeasuredHeight()-dividerLineThickness,paint);

        canvas.translate(getCompoundPaddingLeft(), 0.0f);

        getLayout().draw(canvas);
        canvas.restore();
    }


    public int getDividerLineColor() {
        return dividerLineColor;
    }

    public void setDividerLineColor(int dividerLineColor) {
        this.dividerLineColor = dividerLineColor;
    }

    public float getDividerLineThickness() {
        return dividerLineThickness;
    }

    public void setDividerLineThickness(float dividerLineThickness) {
        this.dividerLineThickness = dividerLineThickness;
    }
}