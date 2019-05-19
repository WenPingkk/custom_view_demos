package com.sean.multi_choose_item.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sean.multi_choose_item.adapter.BaseMenuAdapter;

/**
 * Author WenPing
 * CreateTime 2019/5/19.
 * Description:
 */
public class ListDataScreenView extends LinearLayout implements View.OnClickListener {
    private Context mContext;

    //创建头部,用来存放Tab
    private LinearLayout mMenuTabView;

    //创建FrameLayout存放 阴影(View)以及菜单内容布局
    private FrameLayout mMenuMiddleView;

    //阴影
    private View mShadowView;

    //存放菜单内容
    private FrameLayout mMenuContainerView;

    //阴影的颜色
    private int mShadowColor = 0x88888888;

    //筛选菜单的adapter
    private BaseMenuAdapter mAdapter;

    //内容菜单高度
    private int mMenuContainerHeight;

    //当前打开的位置
    private int mCurrentPosition = -1;

    private long DURATION_TIME = 500;

    private boolean mAnimatorExecute;

    public ListDataScreenView(Context context) {
        this(context, null);
    }

    public ListDataScreenView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListDataScreenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        //1.创建一个xml布局,加载,控件
        //2.简单的效果用代码创建
        setOrientation(VERTICAL);
        //1.1创建头部存放Tab
        mMenuTabView = new LinearLayout(mContext);
        mMenuTabView.setLayoutParams(
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
        addView(mMenuTabView);

        //1.2 创建frameLayout 用来存放 阴影 和菜单内容

        mMenuMiddleView = new FrameLayout(mContext);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        //权重的方式 给定高度
        params.weight = 1;
        mMenuMiddleView.setLayoutParams(params);
        addView(mMenuMiddleView);
        //创建阴影 可以不用设置 LayoutParams 默认就是 MATCH_PARENT
        mShadowView = new View(mContext);
        mShadowView.setBackgroundColor(mShadowColor);
        mShadowView.setAlpha(0f);
        mShadowView.setVisibility(GONE);
        mShadowView.setOnClickListener(this);
        mMenuMiddleView.addView(mShadowView);

        //创建菜单用来存放菜单内容
        mMenuContainerView = new FrameLayout(mContext);
        mMenuContainerView.setBackgroundColor(Color.WHITE);
        mMenuMiddleView.addView(mMenuContainerView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mMenuContainerHeight == 0 && height > 0) {
            mMenuContainerHeight = (int) (height * 75f / 100);
            ViewGroup.LayoutParams params = mMenuContainerView.getLayoutParams();
            params.height = mMenuContainerHeight;
            mMenuContainerView.setLayoutParams(params);
            //进来时阴影不显示,内容也是不显示的
            mMenuContainerView.setTranslationY(-mMenuContainerHeight);
        }
    }

    @Override
    public void onClick(View v) {
        closeMenu();
    }

    public void setAdapter(BaseMenuAdapter adapter) {
        this.mAdapter = adapter;
        //1.获取多少条 tab
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            //1.1获取tab
            View tabView = mAdapter.getTabView(i, mMenuTabView);
            mMenuTabView.addView(tabView);
            LinearLayout.LayoutParams params = (LayoutParams) tabView.getLayoutParams();
            params.weight = 1;
            tabView.setLayoutParams(params);

            //设置tab的点击效果
            setTabClick(tabView, i);

            //获取菜单的内容
            View menuView = mAdapter.getMenuView(i, mMenuContainerView);
            menuView.setVisibility(GONE);
            mMenuContainerView.addView(menuView);
        }
    }

    private void setTabClick(final View tabView, final int position) {
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPosition == -1) {
                    openMenu(tabView, position);
                } else {
                    if (mCurrentPosition == position) {
                        closeMenu();
                    } else {
                        //切换下一个显示
                        View currentMenu = mMenuContainerView.getChildAt(mCurrentPosition);
                        currentMenu.setVisibility(GONE);
                        //更改tab颜色
                        mAdapter.menuClose(mMenuTabView.getChildAt(mCurrentPosition));
                        mCurrentPosition = position;
                        currentMenu = mMenuContainerView.getChildAt(mCurrentPosition);
                        currentMenu.setVisibility(VISIBLE);
                        mAdapter.menuOpen(mMenuTabView.getChildAt(mCurrentPosition));
                    }
                }
            }
        });
    }

    private void openMenu(final View tabView, final int position) {
        if (mAnimatorExecute) {
            return;
        }

        mShadowView.setVisibility(VISIBLE);
        //获取当前位置显示菜单,菜单是加在菜单容器
        View menuView = mMenuContainerView.getChildAt(position);
        menuView.setVisibility(VISIBLE);

        //打开开启动画
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mMenuContainerView, "translationY", -mMenuContainerHeight, 0);
        translationAnimator.setDuration(DURATION_TIME);
        translationAnimator.start();
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 0f, 1f);
        alphaAnimator.setDuration(DURATION_TIME);
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorExecute = false;
                mCurrentPosition = position;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimatorExecute = true;
                //把当前的tab传到外面
                mAdapter.menuOpen(tabView);
            }
        });
        alphaAnimator.start();
    }

    private void closeMenu() {
        if (mAnimatorExecute) {
            return;
        }
        // 关闭动画  位移动画  透明度动画
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mMenuContainerView, "translationY", 0, -mMenuContainerHeight);
        translationAnimator.setDuration(DURATION_TIME);
        translationAnimator.start();
        mShadowView.setVisibility(View.VISIBLE);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 1f, 0f);
        alphaAnimator.setDuration(DURATION_TIME);
        // 要等关闭动画执行完才能去隐藏当前菜单
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                View menuView = mMenuContainerView.getChildAt(mCurrentPosition);
                menuView.setVisibility(View.GONE);
                mCurrentPosition = -1;
                mShadowView.setVisibility(GONE);
                mAnimatorExecute = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimatorExecute = true;
                mAdapter.menuClose(mMenuTabView.getChildAt(mCurrentPosition));
            }
        });
        alphaAnimator.start();
    }
}
