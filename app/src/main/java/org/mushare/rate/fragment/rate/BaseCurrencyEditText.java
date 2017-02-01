package org.mushare.rate.fragment.rate;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by dklap on 1/9/2017.
 */

public class BaseCurrencyEditText extends EditText {
    public BaseCurrencyEditText(Context context) {
        super(context);
    }

    public BaseCurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public BaseCurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
}
