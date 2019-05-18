package com.sean.customview.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.sean.customview.R;

/**
 * Author WenPing
 * CreateTime 2019/5/18.
 * Description:
 */
public class ShapeView extends View{

    /**
     * 枚举的形势 分别表示 圆形,方形和三角形
     * 用于筛选和绘制对应状态的形状
     */
    public enum Shape{
        Circle, Square,Triangle
    }

    public Shape getCurrentShape() {
        return mCurrentShape;
    }

    //默认第一个是 圆形
    private Shape mCurrentShape = Shape.Circle;

    private Paint mPaint;
    private Path mPath;

    public ShapeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //根据当前绘制规范得出的尺寸,选择小尺寸进行绘制
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(Math.min(width,height),Math.min(width,height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //View 类 父类实现为空实现,可以直接删除
        //super.onDraw(canvas);
        switch (mCurrentShape) {
            case Circle:
                drawCircle(canvas);
                break;
            case Square:
                drawSqure(canvas);
                break;
            case Triangle:
                drawTriangle(canvas);
                break;
        }

    }

    private void drawTriangle(Canvas canvas) {
        mPaint.setColor(ContextCompat.getColor(getContext(),R.color.triangle));
        if (mPath == null) {
            mPath = new Path();
            mPath.moveTo(getWidth()/2,0);
            mPath.lineTo(0, (float) ((getWidth()/2)*Math.sqrt(3)));
            mPath.lineTo(getWidth(), (float) ((getWidth() / 2) * Math.sqrt(3)));
            mPath.close();//封闭路径
        }
        canvas.drawPath(mPath,mPaint);
    }

    private void drawSqure(Canvas canvas) {
        mPaint.setColor(ContextCompat.getColor(getContext(),R.color.rect));
        canvas.drawRect(0,0,getWidth(),getHeight(),mPaint);
    }

    private void drawCircle(Canvas canvas) {
        int center = getWidth() / 2;
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.circle));
        canvas.drawCircle(center,center,center,mPaint);
    }

    /**
     * 更改当前动画图形
     */
    public void exchangeView() {
        switch (mCurrentShape) {
            case Circle:
                mCurrentShape = Shape.Square;
                break;
            case Square:
                mCurrentShape = Shape.Triangle;
                break;
            case Triangle:
                mCurrentShape = Shape.Circle;
                break;
        }
        invalidate();
    }
}
