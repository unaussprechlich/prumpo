package de.uni_stuttgart.informatik.sopra.sopraapp.util;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class AnimationHelper {

    @FunctionalInterface
    public interface IOnAnimationEnd{

        void onAnimationEnd();

    }

    /**
     * Shows progress below the signup/login button
     * @param view the progress bar
     * @param animatorListenerAdapter a callback to delay stuff
     */
    public static void showProgress(View view, AnimatorListenerAdapter animatorListenerAdapter ) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0);
        view.animate().setDuration(1000)
                .alpha(1)
                .setListener(animatorListenerAdapter)
                .start();
    }

    /**
     * Animate the visibility of a View
     * @param visibility
     * @param view
     */
    public static void viewVisibilityHide(final boolean visibility, View view){
        view.setVisibility(visibility ? View.VISIBLE : View.GONE);

        view.animate()
                .setDuration(300)
                .alpha(visibility ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(visibility ? View.VISIBLE : View.GONE);
                    }
                }).start();
    }

    /**
     * Slides a View of the top and set it's alpha to 0
     * @param view the view to be animated
     */
    public static void slideOfTop(View view){
        view.animate()
                .translationY(-2000)
                .alpha(0)
                .setDuration(1500)
                .setInterpolator(new DecelerateInterpolator(5.f))
                .start();
    }

    public static void slideOfBottom(View view, IOnAnimationEnd iOnAnimationEnd){
        view.animate()
                .translationY(2000)
                .alpha(0)
                .setDuration(1500)
                .setInterpolator(new DecelerateInterpolator(5.f))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        iOnAnimationEnd.onAnimationEnd();
                    }
                })
                .start();
    }

    /**
     * Slides a View of the bottom and set it's alpha to 0
     * @param view the view to be animated
     */
    public static void slideOfBottom(View view){
        view.animate()
                .translationY(2000)
                .alpha(0)
                .setDuration(1500)
                .setInterpolator(new DecelerateInterpolator(5.f))
                .start();
    }
}
