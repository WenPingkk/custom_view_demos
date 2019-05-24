package com.sean.loadingviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.sean.loadingviewdemo.views.CircleView;

/**
 * Author WenPing
 * CreateTime 2019/5/23.
 * Description:
 */
public class LoadingView extends RelativeLayout {
    private int mTranslationDistance = 30;
    private CircleView mLeftView, mMiddleView, mRightView;
    private long ANIMATION_TIME = 500;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        mTranslationDistance = dip2px(mTranslationDistance);
        //设置背景 白色
        setBackgroundColor(Color.WHITE);
        mLeftView = getCircleView(context);
        mLeftView.exchangeColor(Color.BLUE);
        mMiddleView = getCircleView(context);
        mMiddleView.exchangeColor(Color.RED);
        mRightView = getCircleView(context);
        mRightView.exchangeColor(Color.GREEN);


        addView(mLeftView);
        addView(mRightView);
        addView(mMiddleView);

        /**
         * 在onResume方法中执行
         */
        post(new Runnable() {
            @Override
            public void run() {
                expendAnimation();
            }
        });
    }

    private void expendAnimation() {
        //左边跑
        ObjectAnimator leftTranslationAnimator = ObjectAnimator.ofFloat(mLeftView, "translationX", 0, -mTranslationDistance);
        //右边跑
        ObjectAnimator rightTranslationAnimator = ObjectAnimator.ofFloat(mRightView, "translationX", 0, mTranslationDistance);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATION_TIME);
        set.playTogether(leftTranslationAnimator, rightTranslationAnimator);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //在动画结束时 开始往回运动
//                super.onAnimationEnd(animation);
                innerAnimation();
            }
        });
        set.start();
    }

    private void innerAnimation() {
        //左边->右边 距离为向左运动的距离
        ObjectAnimator leftTranslationAnimator = ObjectAnimator.ofFloat(mLeftView, "translationX", -mTranslationDistance, 0);
        //右边->左边
        ObjectAnimator rightTranslationAnimator = ObjectAnimator.ofFloat(mRightView, "translationX", mTranslationDistance, 0);
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AccelerateInterpolator());
        set.setDuration(ANIMATION_TIME);
        set.playTogether(leftTranslationAnimator, rightTranslationAnimator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int leftColor = mLeftView.getColor();
                int rightColor = mRightView.getColor();
                int mMiddleColor = mMiddleView.getColor();

                mMiddleView.exchangeColor(leftColor);
                mLeftView.exchangeColor(rightColor);
                mRightView.exchangeColor(mMiddleColor);
                //运动结束,三者更换颜色
                expendAnimation();
            }
        });
        set.start();
    }

    private CircleView getCircleView(Context context) {
        CircleView circleView = new CircleView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dip2px(10), dip2px(10));
        params.addRule(CENTER_IN_PARENT);
        circleView.setLayoutParams(params);
        return circleView;
    }

    /**
     * dp转为px
     *
     * @param dip
     * @return
     */
    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

}
