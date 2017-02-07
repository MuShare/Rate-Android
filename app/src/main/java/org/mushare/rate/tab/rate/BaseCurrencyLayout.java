package org.mushare.rate.tab.rate;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by dklap on 1/31/2017.
 */

public class BaseCurrencyLayout extends RelativeLayout {
    public BaseCurrencyLayout(Context context) {
        super(context);
    }

    public BaseCurrencyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCurrencyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public BaseCurrencyLayout(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }
}
