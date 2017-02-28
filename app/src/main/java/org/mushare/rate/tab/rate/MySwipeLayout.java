package org.mushare.rate.tab.rate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.daimajia.swipe.SwipeLayout;

/**
 * Created by dklap on 2/7/2017.
 */

public class MySwipeLayout extends SwipeLayout {
    boolean touchEnabled = true;
    OpenByUserListener listener;

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

    protected void processHandRelease(float xvel, float yvel, boolean isCloseBeforeDragged) {
        float minVelocity = 50;
        View surfaceView = getSurfaceView();
        DragEdge currentDragEdge = getDragEdge();
        if (currentDragEdge == null || surfaceView == null) {
            return;
        }
        float willOpenPercent = (isCloseBeforeDragged ? .25f : .75f);
        if (currentDragEdge == DragEdge.Left) {
            if (xvel > minVelocity) {
                open();
                if (listener != null) listener.onOpenByUser();
            } else if (xvel < -minVelocity) close();
            else {
                float openPercent = 1f * getSurfaceView().getLeft() / getDragDistance();
                if (openPercent > willOpenPercent) {
                    open();
                    if (listener != null) listener.onOpenByUser();
                } else close();
            }
        } else if (currentDragEdge == DragEdge.Right) {
            if (xvel > minVelocity) close();
            else if (xvel < -minVelocity) {
                open();
                if (listener != null) listener.onOpenByUser();
            } else {
                float openPercent = 1f * (-getSurfaceView().getLeft()) / getDragDistance();
                if (openPercent > willOpenPercent) {
                    open();
                    if (listener != null) listener.onOpenByUser();
                } else close();
            }
        } else if (currentDragEdge == DragEdge.Top) {
            if (yvel > minVelocity) {
                open();
                if (listener != null) listener.onOpenByUser();
            } else if (yvel < -minVelocity) close();
            else {
                float openPercent = 1f * getSurfaceView().getTop() / getDragDistance();
                if (openPercent > willOpenPercent) {
                    open();
                    if (listener != null) listener.onOpenByUser();
                } else close();
            }
        } else if (currentDragEdge == DragEdge.Bottom) {
            if (yvel > minVelocity) close();
            else if (yvel < -minVelocity) {
                open();
                if (listener != null) listener.onOpenByUser();
            } else {
                float openPercent = 1f * (-getSurfaceView().getTop()) / getDragDistance();
                if (openPercent > willOpenPercent) {
                    open();
                    if (listener != null) listener.onOpenByUser();
                } else close();
            }
        }
    }

    void setOnOpenByUserListener(OpenByUserListener openByUserListener) {
        listener = openByUserListener;
    }

    interface OpenByUserListener {
        void onOpenByUser();
    }
}
