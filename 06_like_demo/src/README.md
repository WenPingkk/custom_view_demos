# 实现贝塞尔曲线 点赞效果
# target 理解 自定义 TypeEvaluator 使用方式;动画顺序;

##1.设置图片位置
- LayoutParams 使用
```

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
```
## 2.动画集合
- - getBezierAnimatorSet 重点
```
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

```

## 3.以下一番操作都是为了 获取在更新方法中 设置高度和位置;
- LoveTypeEvaluator 的使用.
- 理解 四个点分别的意思.起始点,终止点,在ObjectAnimator.ofObject()方法中传入;中间的两点通过构造方法传入.

```

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

```