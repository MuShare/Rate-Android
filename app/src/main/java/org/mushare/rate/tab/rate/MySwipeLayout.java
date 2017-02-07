package org.mushare.rate.tab.rate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.daimajia.swipe.SwipeLayout;

/**
 * Created by dklap on 2/7/2017.
 */

public class MySwipeLayout extends SwipeLayout {
    boolean touchEnabled = true;

    public MySwipeLayout(Context context) {
        super(context);
    }

    public MySwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isTouchEnabled() {
        return touchEnabled;
    }

    public void setTouchEnabled(boolean touchEnabled) {
        this.touchEnabled = touchEnabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return !touchEnabled || super.onInterceptTouchEvent(e);
    }
}
