package org.mushare.rate;

import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by dklap on 3/11/2017.
 */

public abstract class MyFragment extends Fragment {
    public abstract void onFragmentRecalled();

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        View v = getView();
        if (v != null && enter) {
            v.setTranslationY(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
                    getResources().getDisplayMetrics()));
            v.animate().y(0).setInterpolator(new DecelerateInterpolator()).setDuration(300)
                    .withLayer();
        }
        return null;
//        Animation animation = super.onCreateAnimation(transit, enter, nextAnim);
//        if (animation == null && nextAnim != 0) {
//            animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
//        }
//
//        if (animation != null) {
//            getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
//
//            animation.setAnimationListener(new Animation.AnimationListener() {
//
//                @Override
//                public void onAnimationStart(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    getView().setLayerType(View.LAYER_TYPE_NONE, null);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
//        }
//        return animation;
    }
}
