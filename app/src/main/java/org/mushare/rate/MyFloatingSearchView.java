package org.mushare.rate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.arlib.floatingsearchview.FloatingSearchView;

/**
 * Created by dklap on 2/3/2017.
 * To solve a bug (setSearchFocused() function no work) caused by setVisibility() in API 16
 */

public class MyFloatingSearchView extends FloatingSearchView {
    boolean visible;

    public MyFloatingSearchView(Context context) {
        this(context, null);
    }

    public MyFloatingSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAlpha(0);
        setCloseSearchOnKeyboardDismiss(false); //should be false, otherwise setSearchFocused
        // (false) in onBackPressed() always return false
    }


    @Override
    public void setVisibility(int visibility) {
        visible = (visibility == VISIBLE);
        if (visible) setAlpha(1);
        else animate().alpha(0).setDuration(200).withLayer();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return visible && super.onTouchEvent(ev);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !visible || super.onInterceptTouchEvent(ev);
    }
}
