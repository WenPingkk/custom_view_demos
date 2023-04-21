package com.sean.customprogressbar;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * author:WenPing
 * description:
 * date:2023/4/15 9:12
 */
public class CustomHorizontalProgressBar extends View {

    private static final String TAG = "CustomHorizontalProgressBar";

    private int normalProgressHeight;

    // 指示器的高度
    private int scrollIndicatorHeight;

    private int scrollIndicatorWidth;

    private Paint scrollingStateBgPaint;

    private Paint scrollingStateProgressPaint;

    private Paint scrollingStateIndicatorPaint;

    private int progress = 0;

    private final int max = 100;

    private int progressValue;

    private int progressWidth;

    private int paddingLeft;

    private Paint justReleaseBgPaint;

    private Paint justReleaseProgressPaint;

    private Paint justReleaseIndicatorPaint;

    private float animatedFraction;

    private static final float TARGET_SCALE = 0.5f;

    private int bottomValue;

    private ValueAnimator valueAnimator;

    private int progressLeftRightRadius;

    // 初始状态
    private ProgressState state;

    private static final long DURATION = 500L;

    public CustomHorizontalProgressBar(Context context) {
        this(context, null);
    }

    public CustomHorizontalProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomHorizontalProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initPaint();
        // 手势释放和播放中的状态; 如果初始化是normal状态，animatedFraction为1f
        state = ProgressState.RELEASE_PLAYING_STATE;
        animatedFraction = TARGET_SCALE;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomHorizontalProgressBar);
        // 播放状态时的进度条的高度
        normalProgressHeight = typedArray.getDimensionPixelSize(R.styleable.CustomHorizontalProgressBar_normal_height, 40);
        // 播放状态时指示器的高度
        scrollIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.CustomHorizontalProgressBar_scroll_indicator_height, 4);
        // 播放状态时指示器的整体宽度
        scrollIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.CustomHorizontalProgressBar_scroll_indicator_width, 4);
        typedArray.recycle();
        // 属性动画
        valueAnimator = ValueAnimator.ofFloat(1f, TARGET_SCALE);
        valueAnimator.setDuration(DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedFraction = (float) animation.getAnimatedValue();
                Log.e(TAG, "animatedFraction:" + animatedFraction);
                invalidate();
            }
        });
    }

    private void initPaint() {
        initPlayingStatePaint();
        initJustReleaseStatePaint();
        initScrollStatePaint();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    /**
     * 监听手势动作
     *
     * @param event The motion event.
     * @return
     */
    @SuppressLint("LongLogTag")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float downX = 0f;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                state = ProgressState.SCROLL_STATE;
                downX = event.getX();
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float dx = moveX - downX;
                // 限制最长值
                if (dx >= progressWidth) {
                    dx = progressWidth;
                }
                progress = (int) ((dx / progressWidth) * max);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                // 手势释放开始动画
                state = ProgressState.RELEASE_PLAYING_STATE;
                valueAnimator.start();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        // 一半的高度作为指示器和进度条重合的位置，以及圆角举行半径
        progressLeftRightRadius = normalProgressHeight / 2;
        // 进度条横线所在的坐标值
        bottomValue = getHeight() / 2 - getPaddingBottom() + progressLeftRightRadius;
        // 进度条的整体宽度
        progressWidth = getWidth() - paddingLeft - paddingRight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgressBackground(canvas, state);
        drawProgress(canvas, state);
        drawIndicator(canvas, state);
    }

    @SuppressLint("LongLogTag")
    private void drawIndicator(Canvas canvas, ProgressState state) {
        if (state == ProgressState.SCROLL_STATE) {
            int left = Math.max(progressValue - progressLeftRightRadius, 0);
            if (left <= paddingLeft) {
                left = paddingLeft;
            }
            // 进度条有圆形的边角，所以right = left + scrollIndicatorWidth + progressLeftRightRadius
            RectF rectF = new RectF(left, bottomValue - normalProgressHeight - scrollIndicatorHeight, left + scrollIndicatorWidth + progressLeftRightRadius,
                    bottomValue + scrollIndicatorHeight);
            canvas.drawRoundRect(rectF,
                    scrollIndicatorWidth + progressLeftRightRadius,
                    scrollIndicatorWidth + progressLeftRightRadius,
                    scrollingStateIndicatorPaint);
        } else if (state == ProgressState.RELEASE_PLAYING_STATE) {
            int left = Math.max(progressValue - progressLeftRightRadius, 0);
            if (left <= paddingLeft) {
                left = paddingLeft;
            }
            // 释放时 top值会变小
            float top = bottomValue - (normalProgressHeight + scrollIndicatorHeight) * animatedFraction;
            RectF rectF = new RectF(left, top, left + (scrollIndicatorWidth + progressLeftRightRadius) * animatedFraction,
                    bottomValue + scrollIndicatorHeight * animatedFraction);
            canvas.drawRoundRect(rectF,
                    (scrollIndicatorWidth + progressLeftRightRadius) * animatedFraction,
                    (scrollIndicatorWidth + progressLeftRightRadius) * animatedFraction,
                    justReleaseIndicatorPaint);
        }
    }

    private void drawProgress(Canvas canvas, ProgressState state) {
        float percent = progress * 1.0f / max;
        progressValue = (int) (progressWidth * percent);
        int rx = progressLeftRightRadius;
        if (progressValue <= rx) {
            return;
        }
        if (state == ProgressState.SCROLL_STATE) {
            RectF rectF = new RectF(paddingLeft, bottomValue - normalProgressHeight, progressValue, bottomValue);
            canvas.drawRoundRect(rectF, rx, rx, scrollingStateProgressPaint);
        } else if (state == ProgressState.RELEASE_PLAYING_STATE) {
            RectF rectF = new RectF(paddingLeft, bottomValue - normalProgressHeight * animatedFraction, progressValue, bottomValue);
            canvas.drawRoundRect(rectF, rx, rx, justReleaseProgressPaint);
        }
    }

    /**
     * bottomValue 是不变的
     *
     * @param canvas canvas
     * @param state  state
     */
    private void drawProgressBackground(Canvas canvas, ProgressState state) {
        if (state == ProgressState.RELEASE_PLAYING_STATE) {
            RectF rectF = new RectF(paddingLeft, bottomValue - normalProgressHeight * animatedFraction, progressWidth, bottomValue);
            canvas.drawRoundRect(rectF, progressLeftRightRadius, progressLeftRightRadius, justReleaseBgPaint);
        } else if (state == ProgressState.SCROLL_STATE) {
            RectF rectF = new RectF(paddingLeft, bottomValue - normalProgressHeight, progressWidth, bottomValue);
            canvas.drawRoundRect(rectF, progressLeftRightRadius, progressLeftRightRadius, scrollingStateBgPaint);
        }
    }

    private void initScrollStatePaint() {
        // 拖拉时的进度的颜色 #d7d7d7
        // 拖拉时指示器的背景色 #ffffff
        // 拖拉时进度条的背景色 #383838
        scrollingStateBgPaint = new Paint();
        scrollingStateBgPaint.setAntiAlias(true);
        scrollingStateBgPaint.setStyle(Paint.Style.FILL);
        scrollingStateBgPaint.setColor(Color.parseColor("#383838"));

        scrollingStateProgressPaint = new Paint();
        scrollingStateProgressPaint.setAntiAlias(true);
        scrollingStateProgressPaint.setStyle(Paint.Style.FILL);
        scrollingStateProgressPaint.setColor(Color.parseColor("#d7d7d7"));

        scrollingStateIndicatorPaint = new Paint();
        scrollingStateIndicatorPaint.setColor(Color.parseColor("#f2f2f2"));
        scrollingStateIndicatorPaint.setAntiAlias(true);
        scrollingStateIndicatorPaint.setStyle(Paint.Style.FILL);
    }

    private void initJustReleaseStatePaint() {
        // 刚释放时当前进度和指示器的背景色 #fffffd
        // 刚释放时进度条的背景色 #4b4a48
        justReleaseBgPaint = new Paint();
        justReleaseBgPaint.setAntiAlias(true);
        justReleaseBgPaint.setStyle(Paint.Style.FILL);
        justReleaseBgPaint.setColor(Color.parseColor("#4b4a48"));

        justReleaseProgressPaint = new Paint();
        justReleaseProgressPaint.setAntiAlias(true);
        justReleaseProgressPaint.setStyle(Paint.Style.FILL);
        justReleaseProgressPaint.setColor(Color.parseColor("#fffffd"));

        justReleaseIndicatorPaint = new Paint();
        justReleaseIndicatorPaint.setColor(Color.parseColor("#fffffd"));
        justReleaseIndicatorPaint.setAntiAlias(true);
        justReleaseIndicatorPaint.setStyle(Paint.Style.FILL);
    }

    private void initPlayingStatePaint() {
        // 默认进度套背景色 #7a7977
        // 进度条指示器的颜色 #878984
        Paint playingBgPaint = new Paint();
        playingBgPaint.setAntiAlias(true);
        playingBgPaint.setStyle(Paint.Style.FILL);
        playingBgPaint.setColor(Color.parseColor("#7a7977"));

        Paint playingStateProgressPaint = new Paint();
        playingStateProgressPaint.setAntiAlias(true);
        playingStateProgressPaint.setStyle(Paint.Style.FILL);
        playingStateProgressPaint.setColor(Color.parseColor("#878984"));

        Paint playingIndicatorPaint = new Paint();
        playingIndicatorPaint.setColor(Color.parseColor("#737570"));
        playingIndicatorPaint.setAntiAlias(true);
        playingIndicatorPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 进度条的状态
     */
    private enum ProgressState {
        SCROLL_STATE, RELEASE_PLAYING_STATE
    }
}
