package org.mushare.rate.tab.rate;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * Created by dklap on 1/9/2017.
 */

public class BaseCurrencyEditText extends AppCompatEditText {
    public BaseCurrencyEditText(Context context) {
        super(context);
    }

    public BaseCurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
