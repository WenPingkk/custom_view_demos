package com.sean.parallax.animation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sean.parallax.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author WenPing
 * CreateTime 2019/6/16.
 * Description: 视差 viewpager
 */
public class ParallaxViewPager extends ViewPager {

    List<ParallaxFragment> mFragments = new ArrayList<>();

    public ParallaxViewPager(@NonNull Context context) {
        this(context, null);
    }

    public ParallaxViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLayout(FragmentManager fm, int[] reslayout) {
        /**
         * 把所有layout 填充到fragment,添加到集合中
         */
        for (int layoutId : reslayout) {
            ParallaxFragment fragment = new ParallaxFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ParallaxFragment.LAYOUT_ID_KEY, layoutId);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
        }
        setAdapter(new ParallaxPagerAdapter(fm, mFragments));

        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /**
                 * position:当前位置
                 * positionOffset:0~1
                 * positionOffsetPixels:0~屏幕位置宽度
                 *
                 * 左边滑动 x为负值,y为-值
                 * 右边滑动 x为正值,有为正值
                 */
                ParallaxFragment outFragment = mFragments.get(position);
                Log.e("mTag", "position-> " + position + ";positionOffset-> "
                        + positionOffset + ";positionOffsetPixels-> " + positionOffsetPixels);
                List<View> parallaxViews = outFragment.getParallaxViews();
                for (View parallaxView : parallaxViews) {
                    /**
                     * 为什么这么写? 左滑 == 滑出操作
                     * 右滑 == 滑入操作
                     */
                    ParallaxTag tag = (ParallaxTag) parallaxView.getTag(R.id.parallax_tag);
                    parallaxView.setTranslationX((-positionOffsetPixels) * tag.translationXOut);
                    parallaxView.setTranslationY((-positionOffsetPixels) * tag.translationYOut);
                }

                try {
                    ParallaxFragment inFragment = mFragments.get(position + 1);
                    parallaxViews = inFragment.getParallaxViews();
                    for (View parallaxView : parallaxViews) {
                        ParallaxTag tag = (ParallaxTag) parallaxView.getTag(R.id.parallax_tag);
                        /**
                         * 为什么这么写?
                         */
                        parallaxView.setTranslationX((getMeasuredWidth() - positionOffsetPixels) * tag.translationXIn);
                        parallaxView.setTranslationY((getMeasuredWidth() - positionOffsetPixels) * tag.translationYIn);
                    }


                } catch (Exception e) {

                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    class ParallaxPagerAdapter extends FragmentPagerAdapter {

        private List<ParallaxFragment> mParallaxFragmentList;

        public ParallaxPagerAdapter(FragmentManager fm, List<ParallaxFragment> fragments) {
            super(fm);
            this.mParallaxFragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mParallaxFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mParallaxFragmentList.size();
        }
    }

}
