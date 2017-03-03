package org.mushare.rate.tab.rate;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.mushare.rate.R;

/**
 * Created by dklap on 3/2/2017.
 */

public class MyTabLayout extends TabLayout {
    private boolean touchEnabled;

    public MyTabLayout(Context context) {
        super(context);
        setTouchEnabled(false);
    }

    public MyTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTouchEnabled(false);
    }

    public MyTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTouchEnabled(false);
    }

    public boolean isTouchEnabled() {
        return touchEnabled;
    }

    public void setTouchEnabled(boolean touchEnabled) {
        this.touchEnabled = touchEnabled;
        if (touchEnabled)
            setTabTextColors(getContext().getResources().getColor(R.color.colorTextSecondary),
                    getContext().getResources().getColor(R.color.colorAccent));
        else setTabTextColors(getContext().getResources().getColor(R.color.colorTextDisabled),
                getContext().getResources().getColor(R.color.colorTextDisabled));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return !touchEnabled || super.onInterceptTouchEvent(e);
    }
}
