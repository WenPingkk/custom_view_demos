package com.sean.customview.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.sean.customview.R;

/**
 * Author WenPing
 * CreateTime 2019/5/18.
 * Description:
 */
public class LoadingView extends LinearLayout {

    private ShapeView mShapeView;
    private View mIndicatorView;
    private int mTranslationDistance = 0;
    private final long ANIMATOR_DURATION = 500;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTranslationDistance = dip2px(80);
        initLayout(context);
        post(new Runnable() {
            @Override
            public void run() {
                startFallAnimation();
            }
        });
    }

    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,getResources().getDisplayMetrics());
    }

    private void startFallAnimation() {
        //开始下落动画
        //结束时 进行上移动画并更换shape
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(mShapeView, "translationY", 0, mTranslationDistance);
        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(mIndicatorView, "scaleX", 1f, 0.3f);
        animatorSet.setDuration(ANIMATOR_DURATION);
        animatorSet.playTogether(translateAnimator,scaleAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mShapeView.exchangeView();
                startUpAnimation();
            }
        });
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private void startUpAnimation() {
        //在上移时,进行旋转操作
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(mShapeView, "translationY",0);
        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(mIndicatorView, "scaleX", 0.3f, 1f);
        animatorSet.setDuration(ANIMATOR_DURATION);
        animatorSet.playTogether(translateAnimator,scaleAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startFallAnimation();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                startRotation();
            }
        });
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    private void startRotation() {
        ObjectAnimator objectAnimator = null;
        switch (mShapeView.getCurrentShape()) {
            case Circle:
            case Square:
                objectAnimator = ObjectAnimator.ofFloat(mShapeView, "rotation", 0, 180);
                break;
            case Triangle:
                objectAnimator = ObjectAnimator.ofFloat(mShapeView, "rotation", 0, -120);
                break;
        }
        objectAnimator.setDuration(ANIMATOR_DURATION);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    private void initLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ui_loading_view, this);
        mShapeView = findViewById(R.id.shape_view);
        mIndicatorView = findViewById(R.id.shadow_view);
    }
}
