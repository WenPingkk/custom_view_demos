package com.sean.bezier_demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Author WenPing
 * CreateTime 2019/5/27.
 * Description:
 */
public class MessageBubbleView extends View{

    private PointF mFixedLocationPoint,mDragPoint;

    private int mDragRadius = 12;

    private Paint mPaint;

    private int mFixedLocationRadiusMax = 7;
    private int mFixedLocationRadiusMin = 3;
    private int mFixedLocationRadius;

    public MessageBubbleView(Context context) {
        this(context,null);
    }

    public MessageBubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MessageBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDragRadius = dip2px(mDragRadius);
        mFixedLocationRadiusMax = dip2px(mFixedLocationRadiusMax);
        mFixedLocationRadiusMin = dip2px(mFixedLocationRadiusMin);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        //设置防抖动
        mPaint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDragPoint == null || mFixedLocationPoint == null) {
            return;
        }
        //画一个圆 拖拽圆
        canvas.drawCircle(mDragPoint.x, mDragPoint.y, mDragRadius, mPaint);

        //获取贝塞尔路径
        Path bezierPath = getBezierPath();
        if (bezierPath != null) {
            //小到一定程度就不画
            canvas.drawCircle(mFixedLocationPoint.x, mFixedLocationPoint.y, mFixedLocationRadius, mPaint);
            //画出贝塞尔曲线
            canvas.drawPath(bezierPath,mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下去指定当前的位置
                float downX = event.getX();
                float downY = event.getY();
                initPoint(downX, downY);
            break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                updateDragPoint(moveX, moveY);
                break;
            case MotionEvent.ACTION_UP:
                break;

            default:
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 更新拖拽圆的位置
     * @param x
     * @param y
     */
    private void updateDragPoint(float x, float y) {
        mDragPoint.x = x;
        mDragPoint.y = y;
    }

    /**
     * 初始化定点
     * @param x
     * @param y
     */
    private void initPoint(float x, float y) {
        mFixedLocationPoint = new PointF(x, y);
        mDragPoint = new PointF(x, y);
    }

    /**
     * 绘制贝塞尔曲线路径
     * @return
     */
    private Path getBezierPath() {
        double distance = getDistance(mDragPoint, mFixedLocationPoint);

        mFixedLocationRadius = (int) (mFixedLocationRadiusMax - distance/ 14);
        if (mFixedLocationRadius < mFixedLocationRadiusMin) {
            //超过一定距离,贝塞尔曲线和固定圆 不绘制
            return null;
        }

        Path bezierPath = new Path();
        //求斜率
        float dy = (mDragPoint.y-mFixedLocationPoint.y);
        float dx = (mDragPoint.x-mFixedLocationPoint.x);
        float tanA = dy / dx;
        //求角度
        double arcTanA = Math.atan(tanA);
        //下面是绘制 四个点,这四个点是在曲线上的.和两个定圆相切
        // p0
        float p0x = (float) (mFixedLocationPoint.x + mFixedLocationRadius*Math.sin(arcTanA));
        float p0y = (float) (mFixedLocationPoint.y - mFixedLocationRadius*Math.cos(arcTanA));

        // p1
        float p1x = (float) (mDragPoint.x + mDragRadius*Math.sin(arcTanA));
        float p1y = (float) (mDragPoint.y - mDragRadius*Math.cos(arcTanA));

        // p2
        float p2x = (float) (mDragPoint.x - mDragRadius*Math.sin(arcTanA));
        float p2y = (float) (mDragPoint.y + mDragRadius*Math.cos(arcTanA));

        // p3
        float p3x = (float) (mFixedLocationPoint.x - mFixedLocationRadius*Math.sin(arcTanA));
        float p3y = (float) (mFixedLocationPoint.y + mFixedLocationRadius*Math.cos(arcTanA));

        //拼装贝塞尔曲线路径
        bezierPath.moveTo(p0x, p0y);

        //两个点
        PointF controlPoint = getControlPoint();
        ///画一条线甚至是画弧线时会形成平滑的曲线，该曲线又称为"贝塞尔曲线"(Bezier curve)
        bezierPath.quadTo(controlPoint.x, controlPoint.y, p1x, p1y);

        bezierPath.lineTo(p2x, p2y);
        bezierPath.quadTo(controlPoint.x, controlPoint.y, p3x, p3y);
        //封闭
        bezierPath.close();
        return bezierPath;
    }

    private PointF getControlPoint() {
        return new PointF((mDragPoint.x + mFixedLocationPoint.x) / 2,
                (mDragPoint.y + mFixedLocationPoint.y) / 2);
    }

    private double getDistance(PointF point1, PointF point2) {
        return Math.sqrt((point1.x - point2.x) * (point1.x - point2.x)
                + (point1.y - point2.y) * (point1.y - point2.y));
    }


    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }
}
