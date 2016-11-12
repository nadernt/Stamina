package com.fleecast.stamina.customgui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ExtendedEditText extends EditText {

    public ExtendedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public ExtendedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public ExtendedEditText(Context context) {
        super(context);

    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            this.clearFocus();
            dispatchKeyEvent(event);
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }

}