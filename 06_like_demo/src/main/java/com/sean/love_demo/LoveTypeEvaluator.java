package com.sean.love_demo;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Author WenPing
 * CreateTime 2019/6/15.
 * Description: 自定义图片路径 属性动画
 */
public class LoveTypeEvaluator implements TypeEvaluator<PointF> {

    private PointF mPointF1, mPointF2;

    /**
     * 第[1]个点
     * 第[2]个点
     *
     * @param pointF1
     * @param pointF2
     */
    public LoveTypeEvaluator(PointF pointF1, PointF pointF2) {
        this.mPointF1 = pointF1;
        this.mPointF2 = pointF2;
    }

    /**
     * 第[0]个点
     * 第[3]个点
     *
     * @param fraction
     * @param pointF0 startValue
     * @param pointF3 endValue
     * @return
     */
    @Override
    public PointF evaluate(float fraction, PointF pointF0, PointF pointF3) {

        /**
         * fraction [0,1]
         *  https://www.jianshu.com/p/7c56103dcf63
         *  曲线控制的店有四个
         */

        PointF pointF = new PointF();

        pointF.x = pointF0.x * (1 - fraction) * (1 - fraction) * (1 - fraction)
                + 3 * mPointF1.x * fraction * (1 - fraction) * (1 - fraction)
                + 3 * mPointF2.x * fraction * fraction * (1 - fraction)
                + pointF3.x * fraction * fraction * fraction;

        pointF.y = pointF0.y * (1 - fraction) * (1 - fraction) * (1 - fraction)
                + 3 * mPointF1.y * fraction * (1 - fraction) * (1 - fraction)
                + 3 * mPointF2.y * fraction * fraction * (1 - fraction)
                + pointF3.y * fraction * fraction * fraction;

        return pointF;
    }
}
