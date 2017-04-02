package org.mushare.rate.tab.rate;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by dklap on 1/9/2017.
 */

public class BaseCurrencyEditText extends AppCompatEditText {
    public BaseCurrencyEditText(Context context) {
        this(context, null);
    }

    public BaseCurrencyEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.editTextStyle);
    }

    public BaseCurrencyEditText(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    clearFocus();
                    return true;
                }
                return false;
            }
        });
        setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager keyboard = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(getWindowToken(), 0);
                } else {
                    InputMethodManager keyboard = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(BaseCurrencyEditText.this, InputMethodManager
                            .SHOW_FORCED);
                }
            }
        });
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        CharSequence text = getText();
        if (text != null) {
            if (selStart != text.length() || selEnd != text.length()) {
                setSelection(text.length(), text.length());
                return;
            }
        }
        super.onSelectionChanged(selStart, selEnd);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isFocused()) clearFocus();
            else requestFocus();
        }
        return true;
    }
}
