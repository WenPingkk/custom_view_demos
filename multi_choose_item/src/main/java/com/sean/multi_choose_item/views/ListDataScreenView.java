package com.sean.multi_choose_item.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
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
public class ListDataScreenView extends LinearLayout {

    // 1.1 创建头部用来存放 Tab
    private LinearLayout mMenuTabView;
    // 1.2 创建 FrameLayout 用来存放 = 阴影（View） + 菜单内容布局(FrameLayout)
    private FrameLayout mMenuMiddleView;
    // 阴影
    private View mShadowView;
    // 创建菜单用来存放菜单内容
    private FrameLayout mMenuContainerView;
    // 阴影的颜色
    private int mShadowColor = 0x88888888;
    // 筛选菜单的 Adapter
    private BaseMenuAdapter mAdapter;
    // 内容菜单的高度
    private int mMenuContainerHeight;
    // 当前打开的位置
    private int mCurrentPosition = -1;
    private long DURATION_TIME = 500;
    // 动画是否在执行
    private boolean mAnimatorExecute;


    public ListDataScreenView(Context context) {
        this(context, null);
    }

    public ListDataScreenView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListDataScreenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        setOrientation(VERTICAL);
        /**
         * 代码的形势 创建视图
         * 结构
         * 竖向线性布局 = mMenuTabView+[mMenuMiddleView]
         * [mMenuMiddleView] = [阴影]+[mMenuContainerView]
         */

        //把tab加到页面中
        mMenuTabView = new LinearLayout(context);
        mMenuTabView.setLayoutParams(new LinearLayoutCompat
                .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(mMenuTabView);

        /**
         * 创建 存放 菜单内容和阴影的视图
         */
        mMenuMiddleView = new FrameLayout(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        mMenuMiddleView.setLayoutParams(params);
        addView(mMenuMiddleView);


        /**
         * 阴影是不限制大小,默认是MATCH_PARENT,MATCH_PARENT
         */
        mShadowView = new View(context);
        mShadowView.setBackgroundColor(mShadowColor);
        mShadowView.setAlpha(0f);
        mShadowView.setVisibility(GONE);
        mMenuMiddleView.addView(mShadowView);
        mShadowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 点击阴影效果 关闭当前菜单
                 */
                closeMenu();
            }
        });
        /**
         * 把菜单内容放到父视图中
         * 这里没有确认高度,高度在onMeasure方法中实现
         */
        mMenuContainerView = new FrameLayout(context);
        mMenuContainerView.setBackgroundColor(Color.WHITE);
        mMenuMiddleView.addView(mMenuContainerView);
    }

    /**
     * 获取mMenuContainerHeight高度,以及平移操作
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mMenuContainerHeight == 0 && height > 0) {
            mMenuContainerHeight = (int) (height * 0.75f);
            ViewGroup.LayoutParams params = mMenuContainerView.getLayoutParams();
            params.height = mMenuContainerHeight;
            mMenuContainerView.setLayoutParams(params);
            //一开始是不出现的.向上平移 高度的距离
            mMenuContainerView.setTranslationY(-mMenuContainerHeight);
        }
    }

    public void setAdapter(BaseMenuAdapter adapter) {
        this.mAdapter = adapter;
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            /**
             * 加载tab内容
             */
            View tabView = adapter.getTabView(i, mMenuTabView);
            //强制 cast 为线性布局
            LinearLayout.LayoutParams params = (LayoutParams) tabView.getLayoutParams();
            params.weight = 1;
            tabView.setLayoutParams(params);
            mMenuTabView.addView(tabView);
            //设置tab点击效果
            setTabClick(tabView, i);
            /**
             * 加载menu内容
             */
            View menuView = adapter.getMenuView(i, mMenuContainerView);
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
                        //不关闭当前菜单,更换显示内容
                        View currentMenuView = mMenuContainerView.getChildAt(mCurrentPosition);
                        currentMenuView.setVisibility(GONE);
                        //更换tab显示
                        mAdapter.menuClose(mMenuTabView.getChildAt(mCurrentPosition));
                        mCurrentPosition = position;
                        currentMenuView = mMenuContainerView.getChildAt(mCurrentPosition);
                        currentMenuView.setVisibility(VISIBLE);
                        //更新tab显示
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
        // 获取当前位置显示当前菜单，菜单是加到了菜单容器
        View menuView = mMenuContainerView.getChildAt(position);
        menuView.setVisibility(View.VISIBLE);
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mMenuContainerView,
                "translationY", -mMenuContainerHeight, 0);
        translationAnimator.setDuration(DURATION_TIME);
        translationAnimator.start();

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 0, 1f);
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
                mAdapter.menuOpen(tabView);
            }
        });
        alphaAnimator.start();
    }

    private void closeMenu() {
        if (mAnimatorExecute) {
            return;
        }
        /**
         * 关闭 平移和透明度
         */
        mShadowView.setVisibility(VISIBLE);
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mMenuContainerView,
                "translationY", 0, -mMenuContainerHeight);
        translationAnimator.setDuration(DURATION_TIME);
        translationAnimator.start();

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 1f, 0);
        alphaAnimator.setDuration(DURATION_TIME);
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                View menuView = mMenuContainerView.getChildAt(mCurrentPosition);
                menuView.setVisibility(GONE);
                mAnimatorExecute = false;
                mShadowView.setVisibility(GONE);
                mCurrentPosition = -1;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimatorExecute = true;
                //关闭 tab显示
                mAdapter.menuClose(mMenuTabView.getChildAt(mCurrentPosition));
            }
        });
        alphaAnimator.start();
    }
}
