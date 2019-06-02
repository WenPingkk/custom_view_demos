package com.sean.bezier.bezier_drag_demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * Author WenPing
 * CreateTime 2019/6/2.
 * Description:
 */
public class MessageBubbleView extends View {

    //1.画两个点,画固定圆和拖动圆
    //2.半径分别为设定的大,小半径.
    //3.定圆不是一直都绘制.根据和和拖拽圆的位置距离来判定
    private PointF mFixedLocationPoint, mDragPoint;
    //拖动圆的半径
    private int mDragRadius = 12;

    private Paint mPaint;

    //配置最大和最小半径
    private int mFixedLocationRadiusMax = 7;
    private int mFixedLocationRadiusMin = 3;
    private int mFixedLocationRadius;

    //bitmap
    private Bitmap mDragBitmap;

    public MessageBubbleView(Context context) {
        this(context, null);
    }

    public MessageBubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageBubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
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
            canvas.drawPath(bezierPath, mPaint);
        }
        //1.绘制 bitmap 位置为拖拽点位置减去bitmap位置的宽高的一半.
        //2.目的是让它居中
        if (mDragBitmap != null) {
            canvas.drawBitmap(mDragBitmap,
                    mDragPoint.x - mDragBitmap.getWidth() / 2,
                    mDragPoint.y - mDragBitmap.getHeight() / 2,
                    null);
        }
    }

    /**
     * 设置方法
     * @param dragBitmap
     */
    public void setDragBitmap(Bitmap dragBitmap) {
        this.mDragBitmap = dragBitmap;
    }

    /**
     * 通过attach方法,确定静止view,和监听器
     * @param view
     * @param listener
     */
    public static void attach(View view, BubbleMessageTouchListener.BubbleDisappearListener listener) {
        view.setOnTouchListener(new BubbleMessageTouchListener(view,view.getContext(),listener));
    }

    /**
     * 在抬手操作时 根据当前距离进行对应操作
     */
    public void handleActionUp() {
        //固定圆的半径大于最小半径,放手时,进行还原操作,带动画
        if (mFixedLocationRadius > mFixedLocationRadiusMin) {
            //动画处理
            ValueAnimator animator = ObjectAnimator.ofFloat(1);
            animator.setDuration(500);
            final PointF start = new PointF(mDragPoint.x, mDragPoint.y);
            final PointF end = new PointF(mFixedLocationPoint.x, mFixedLocationPoint.y);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float percent = (float) animation.getAnimatedValue();
                    PointF pointF = BubbleUtils.getPointByPercent(start, end, percent);

                    //用代码更新拖拽点
                    updateDragPoint(pointF.x,pointF.y);
                }
            });
            animator.setInterpolator(new OvershootInterpolator());
            animator.start();

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mListener != null) {
                        //实现还原操作
                        mListener.restore();
                    }
                }
            });
        } else {
            if (mListener != null) {
                mListener.dismiss(mDragPoint);
            }
        }
    }

    /**
     * 更新拖拽圆的位置
     *
     * @param x
     * @param y
     */
    public void updateDragPoint(float x, float y) {
        mDragPoint.x = x;
        mDragPoint.y = y;

        //重新绘制
        invalidate();
    }

    /**
     * 初始化定点
     *
     * @param x
     * @param y
     */
    public void initPoint(float x, float y) {
        mFixedLocationPoint = new PointF(x, y);
        mDragPoint = new PointF(x, y);
        invalidate();
    }

    /**
     * 绘制贝塞尔曲线路径
     *
     * @return
     */
    private Path getBezierPath() {
        //1,定圆的半径为 变化值.比最小值小时就不绘制定圆
        double distance = getDistance(mDragPoint, mFixedLocationPoint);
        mFixedLocationRadius = (int) (mFixedLocationRadiusMax - distance / 14);
        if (mFixedLocationRadius < mFixedLocationRadiusMin) {
            //超过一定距离,贝塞尔曲线和固定圆 不绘制
            return null;
        }

        Path bezierPath = new Path();
        //求斜率
        float dy = (mDragPoint.y - mFixedLocationPoint.y);
        float dx = (mDragPoint.x - mFixedLocationPoint.x);
        float tanA = dy / dx;
        //求角度
        double arcTanA = Math.atan(tanA);
        //下面是绘制 四个点,这四个点是在曲线上的.和两个定圆相切
        // p0
        float p0x = (float) (mFixedLocationPoint.x + mFixedLocationRadius * Math.sin(arcTanA));
        float p0y = (float) (mFixedLocationPoint.y - mFixedLocationRadius * Math.cos(arcTanA));

        // p1
        float p1x = (float) (mDragPoint.x + mDragRadius * Math.sin(arcTanA));
        float p1y = (float) (mDragPoint.y - mDragRadius * Math.cos(arcTanA));

        // p2
        float p2x = (float) (mDragPoint.x - mDragRadius * Math.sin(arcTanA));
        float p2y = (float) (mDragPoint.y + mDragRadius * Math.cos(arcTanA));

        // p3
        float p3x = (float) (mFixedLocationPoint.x - mFixedLocationRadius * Math.sin(arcTanA));
        float p3y = (float) (mFixedLocationPoint.y + mFixedLocationRadius * Math.cos(arcTanA));

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

    private MessageBubbleListener mListener;

    public void setMessageBubbleListener(MessageBubbleListener listener) {
        this.mListener = listener;
    }

    /**
     * 接口回调
     * 1.设置监听器
     * 2.根据距离进行还原/dismiss操作
     */
    public interface MessageBubbleListener {
        // 还原
        void restore();

        // 消失爆炸
        void dismiss(PointF pointF);
    }
}
