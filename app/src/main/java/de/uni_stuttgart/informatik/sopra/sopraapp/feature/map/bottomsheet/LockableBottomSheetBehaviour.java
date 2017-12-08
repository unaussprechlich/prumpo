package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * https://stackoverflow.com/a/36775907/8596346
 *
 * @param <V>
 */
@SuppressWarnings("unused")
public class LockableBottomSheetBehaviour<V extends View> extends BottomSheetBehavior<V> {
    private boolean isLocked = true;

    public LockableBottomSheetBehaviour() {
        super();
    }

    public LockableBottomSheetBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static <V extends View> LockableBottomSheetBehaviour from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        return LockableBottomSheetBehaviour.class.cast(((CoordinatorLayout.LayoutParams) params).getBehavior());
    }

    public void allowUserSwipe(boolean enabled) {
        this.isLocked = !enabled;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        return !isLocked && super.onInterceptTouchEvent(parent, child, event);
    }
}
