package com.sean.loadingviewdemo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author WenPing
 * CreateTime 2019/5/23.
 * Description:
 */
public class CircleView extends View{

    private Paint mPaint;
    private int mColor;

    public CircleView(Context context) {
        this(context,null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //这是什么意思?  设置防抖动
        mPaint.setDither(true);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        int cx = getWidth()/2;
        int cy = getHeight()/2;
        canvas.drawCircle(cx, cy, cx,mPaint);
    }

    /**
     * 切换颜色
     * @param color
     */
    public void exchangeColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
        invalidate();
    }

    /**
     * 获取当前的颜色
     * @return
     */
    public int getColor() {
        return mColor;
    }
}
