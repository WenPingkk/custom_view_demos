package com.sean.love_demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Author WenPing
 * CreateTime 2019/6/15.
 * Description:实现 点赞的效果
 */
public class LoveLayout extends RelativeLayout {

    /**
     * 随机 image颜色
     */
    private Random mRandom;

    /**
     * 图片颜色资源
     */
    private int[] mImageRes;

    /**
     * 确定 控件儿宽度和高度
     */
    private int mWidth, mHeight;

    /**
     * 图片的宽和高
     */

    /**
     * 加速器
     */
    private Interpolator[] mInterpolatorArray;

    private int mDrawableWidth, mDrawableHeight;


    public LoveLayout(Context context) {
        this(context, null);
    }

    public LoveLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    /**
     * 初始化 配置
     * @param context
     */
    private void initLayout(Context context) {
        /**
         * 1.随机数;
         * 2.图片资源;
         * 3.确定控件大小
         * 4.确定加速器
         */
        mRandom = new Random();
        mImageRes = new int[]{R.drawable.pl_blue,
                R.drawable.pl_red,
                R.drawable.pl_yellow};

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.pl_blue);
        assert drawable != null;
        mDrawableWidth = drawable.getIntrinsicWidth();
        mDrawableHeight = drawable.getIntrinsicHeight();

        mInterpolatorArray = new Interpolator[]{
                new AccelerateDecelerateInterpolator(),
                new AccelerateInterpolator(),
                new DecelerateInterpolator(),
                new LinearInterpolator()};
    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 获取控件儿的高度
         */
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    /**
     * 添加点赞的效果
     */
    public void addLove() {
        /**
         * 1.绘制出 图片资源
         * 2.确定 图片位置
         * 3.动画
         */
        final ImageView loveIv = new ImageView(getContext());
        loveIv.setImageResource(mImageRes[mRandom.nextInt(mImageRes.length - 1)]);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_BOTTOM);
        params.addRule(CENTER_HORIZONTAL);
        loveIv.setLayoutParams(params);

        addView(loveIv);

        AnimatorSet animatorSet = getAnimatorSet(loveIv);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                /**
                 * 动画结束时移除资源
                 */
                removeView(loveIv);
            }
        });
        animatorSet.start();

    }

    /**
     * 动画集合路径
     *
     * @param iv
     * @return
     */
    private AnimatorSet getAnimatorSet(ImageView iv) {

        AnimatorSet allAnimatorSet = new AnimatorSet();

        AnimatorSet innerAnimatorSet = new AnimatorSet();
        ObjectAnimator alphaAnimator = ObjectAnimator
                .ofFloat(iv, "alpha", 0.3f, 1f);
        ObjectAnimator scaleXAnimator = ObjectAnimator
                .ofFloat(iv, "scaleX", 0.3f, 1.f);
        ObjectAnimator scaleYAnimator = ObjectAnimator
                .ofFloat(iv, "scaleY", 0.3f, 1f);

        innerAnimatorSet.playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator);
        innerAnimatorSet.setDuration(500);
        allAnimatorSet.playTogether(innerAnimatorSet, getBezierAnimatorSet(iv));

        return allAnimatorSet;
    }

    /**
     * @param iv
     * @return
     */
    private Animator getBezierAnimatorSet(final ImageView iv) {
        /**
         * 确定四个点
         */
        /*
          第[0]个点 起点!
         */
        final PointF pointF0 = new PointF(
                mWidth / 2 - mDrawableWidth / 2,
                mHeight - mDrawableHeight);
        /*
         过程的两点
         */
        PointF pointF1 = getPointF(1);
        PointF pointF2 = getPointF(2);
        /*
        第[3]个点 终点
         */
        PointF pointF3 = new PointF(mRandom.nextInt(mWidth - mDrawableWidth), 0);

        LoveTypeEvaluator loveTypeEvaluator = new LoveTypeEvaluator(pointF1, pointF2);
        /**
         * ValueAnimator ,target
         */
        ValueAnimator bezierAnimator = ObjectAnimator.ofObject(loveTypeEvaluator, pointF0, pointF3);
        bezierAnimator.setInterpolator(mInterpolatorArray[mRandom.nextInt(mInterpolatorArray.length)]);
        bezierAnimator.setDuration(3000);
        bezierAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                iv.setX(pointF.x);
                iv.setY(pointF.y);
                //设置透明度
                float fraction = animation.getAnimatedFraction();
                iv.setAlpha(1 - fraction + 0.2f);
            }
        });
        return bezierAnimator;
    }

    /**
     * index==1 -> 宽度 小于 屏幕宽度 并不会超出;高度保持在底部1/2高度范围
     * index==2 -> ...;高度保持在底部超出1/2高度范围
     *
     * @param index
     * @return
     */
    private PointF getPointF(int index) {
        return new PointF(
                mRandom.nextInt(mWidth) - mDrawableWidth,
                mRandom.nextInt(mHeight / 2) + (index - 1) * (mHeight / 2));
    }
}
