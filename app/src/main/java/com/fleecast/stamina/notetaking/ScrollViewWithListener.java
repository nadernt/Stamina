package com.fleecast.stamina.notetaking;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by nnt on 31/07/16.
 */
public class ScrollViewWithListener extends ScrollView {

    private boolean mCurrentlyTouching;
    private boolean mCurrentlyFling;

    public interface ScrollViewListener {
        public void onScrollChanged(ScrollViewWithListener scrollView, int x, int y, int oldx, int oldy);
        public void onEndScroll();
    }

    private ScrollViewListener scrollViewListener = null;

    public ScrollViewWithListener(Context context) {
        super(context);
    }

    public ScrollViewWithListener(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewWithListener(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
        mCurrentlyFling = true;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }

        if (Math.abs(t - oldt) < 2 || t >= getMeasuredHeight() || t == 0) {
            if(!mCurrentlyTouching){
                if (scrollViewListener != null) {
                    Log.d("SCROLL WITH LISTENER", "-- OnEndScroll");
                    scrollViewListener.onEndScroll();
                }
            }
            mCurrentlyFling = false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentlyTouching = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mCurrentlyTouching = false;
                if(!mCurrentlyFling){
                    if (scrollViewListener != null) {
                        Log.d("SCROLL WITH LISTENER", "-- OnEndScroll");
                        scrollViewListener.onEndScroll();
                    }
                }
                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentlyTouching = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mCurrentlyTouching = false;
                if(!mCurrentlyFling){
                    if (scrollViewListener != null) {
                        Log.d("SCROLL WITH LISTENER", "-- OnEndScroll");
                        scrollViewListener.onEndScroll();
                    }
                }
                break;

            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}