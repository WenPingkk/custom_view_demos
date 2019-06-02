package com.sean.bezier.bezier_drag_demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Author WenPing
 * CreateTime 2019/6/2.
 * Description:
 */
public class BubbleMessageTouchListener implements MessageBubbleView.MessageBubbleListener, View.OnTouchListener {
    // 原来需要拖动爆炸的View
    private View mStaticView;
    private WindowManager mWindowManager;
    private MessageBubbleView mMessageBubbleView;
    private WindowManager.LayoutParams mParams;
    private Context mContext;
    // 爆炸动画
    private FrameLayout mBombFrame;
    private ImageView mBombImage;
    private BubbleDisappearListener mDisappearListener;

    BubbleMessageTouchListener(View view, Context context, BubbleDisappearListener disappearListener) {
        mStaticView = view;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mMessageBubbleView = new MessageBubbleView(context);
        mMessageBubbleView.setMessageBubbleListener(this);
        //创建view
        mParams = new WindowManager.LayoutParams();
        //设置背景
        mParams.format = PixelFormat.TRANSPARENT;
        this.mContext = context;

        mBombFrame = new FrameLayout(context);
        mBombImage = new ImageView(context);
        mBombImage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        mBombFrame.addView(mBombImage);
        this.mDisappearListener = disappearListener;
    }

    /**
     * 从一个viwe中返回一个BitMap
     *
     * @param view
     * @return
     */
    private Bitmap getBitmapByView(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 把消息的view 移除,把原来的view显示出来
     */
    @Override
    public void restore() {
        mWindowManager.removeView(mMessageBubbleView);
        mStaticView.setVisibility(View.VISIBLE);
    }

    /**
     * 执行爆炸动画「帧动画」
     * 原来的view要移除
     * 要在mWindwowManager中加一个爆炸动画
     *
     * @param pointF
     */
    @Override
    public void dismiss(PointF pointF) {
        mWindowManager.removeView(mMessageBubbleView);
        mWindowManager.addView(mBombFrame, mParams);
        mBombImage.setBackgroundResource(R.drawable.anim_bubble_pop);

        //动画的位置
        AnimationDrawable animationDrawable = (AnimationDrawable) mBombImage.getBackground();
        mBombImage.setX(pointF.x - animationDrawable.getIntrinsicWidth() / 2);
        mBombImage.setY(pointF.y - animationDrawable.getIntrinsicHeight() / 2);
        //开启动画
        animationDrawable.start();

        //等动画执行结束,移除这个动画->mBombFrame
        mBombFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWindowManager.removeView(mBombFrame);
                //通知一下 接口调用
                if (mDisappearListener != null) {
                    mDisappearListener.dismiss(mStaticView);
                }
            }
        }, getAnimationDrawableTime(animationDrawable));
    }

    private long getAnimationDrawableTime(AnimationDrawable drawable) {
        int numberOfFrame = drawable.getNumberOfFrames();
        long time = 0;
        for (int i = 0; i < numberOfFrame; i++) {
            time += drawable.getDuration(i);
        }
        return time;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //1.在windowManager上写一个view
                mWindowManager.addView(mMessageBubbleView, mParams);
                //2.初始化 贝塞尔view的点
                //3.保证固定圆的中心在view的中心
                int[] location = new int[2];
                mStaticView.getLocationOnScreen(location);
                Bitmap bitmap = getBitmapByView(mStaticView);
                mMessageBubbleView.initPoint(location[0] + mStaticView.getWidth() / 2, location[1] + mStaticView.getWidth() / 2 - BubbleUtils.getStatusBarHeight(mContext));
                //给消息拖拽设置一Bitmap
                mMessageBubbleView.setDragBitmap(bitmap);

                //4.把自己隐藏掉
                mStaticView.setVisibility(View.INVISIBLE);
                break;
            case MotionEvent.ACTION_MOVE:
                mMessageBubbleView.updateDragPoint(event.getRawX(),
                        event.getRawY() - BubbleUtils.getStatusBarHeight(mContext));
                break;
            case MotionEvent.ACTION_UP:
                //进行抬手操作
                mMessageBubbleView.handleActionUp();
                break;
            default:
                break;
        }
        return true;
    }

    public interface BubbleDisappearListener {
        void dismiss(View view);
    }
}
