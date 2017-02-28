package org.mushare.rate.tab.rate;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by dklap on 2/6/2017.
 */

public class RateRecyclerView extends RecyclerView {
    boolean touchEnabled = true;

    public RateRecyclerView(Context context) {
        super(context);
    }

    public RateRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RateRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTouchEnabled(boolean touchEnabled) {
        this.touchEnabled = touchEnabled;
//        if (touchEnabled) animate().alpha(1).setDuration(300).withLayer();
//        else animate().alpha(0.6f).setDuration(200).withLayer();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        return touchEnabled && super.onTouchEvent(ev);
//    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return !touchEnabled || super.onInterceptTouchEvent(e);
    }
}
