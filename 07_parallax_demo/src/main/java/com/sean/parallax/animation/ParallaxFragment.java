package com.sean.parallax.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.sean.parallax.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author WenPing
 * CreateTime 2019/6/16.
 * Description: 视差动画的fragment
 */
public class ParallaxFragment extends Fragment implements LayoutInflater.Factory2 {
    public static final String LAYOUT_ID_KEY = "layout_id_key";

    /**
     * 填充器
     */
    private CompatViewInflater mCompatViewInflater;

    /**
     * 存放所有需要位移的view
     */
    private List<View> mParallaxViews = new ArrayList<>();


    private int[] mParallaxAttrs = new int[]{
            R.attr.translationXIn,
            R.attr.translationXOut,
            R.attr.translationYIn,
            R.attr.translationYOut};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**
         * 获取layoutId
         */
        int layoutId = getArguments().getInt(LAYOUT_ID_KEY);

        /**
         *为甚么要clone inflater?
         *Inflater 单例设计模式.代表着所有的View的创建都会是该 Fragment 去创建的
         */
        inflater = inflater.cloneInContext(getActivity());
        LayoutInflaterCompat.setFactory2(inflater, this);
        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        /**
         * 在这里 将View 创建
         * 拦截,然后获取View之后解析
         * 1.创建View
         * 如果返回null,onCreateView[上面的那个] 重调
         */
        View view = createView(parent, name, context, attrs);
        if (view != null) {
            //解析所有 「关注」的属性
            analysisAttrs(view, context, attrs);
        }
        return view;
    }

    /**
     * 这里用到了解析方式 思路源自 TextView源码
     * 对 关注的属性集合进行 过滤
     *
     * @param view
     * @param context
     * @param attrs
     */
    private void analysisAttrs(View view, Context context, AttributeSet attrs) {
        /**
         * 别低估这句代码
         * 获取 mParallaxAttrs 属性集合的所有属性
         */
        TypedArray typedArray = context.obtainStyledAttributes(attrs, mParallaxAttrs);
        if (typedArray != null && typedArray.getIndexCount() != 0) {
            int count = typedArray.getIndexCount();
            ParallaxTag tag = new ParallaxTag();
            for (int i = 0; i < count; i++) {
                Log.e("mTag", "i:" + i);
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    /**
                     * 分别是什么意思?
                     */
                    case 0:
                        tag.translationXIn = typedArray.getFloat(attr, 0f);
                        break;
                    case 1:
                        tag.translationXOut = typedArray.getFloat(attr, 0f);
                        break;

                    case 2:
                        tag.translationYIn = typedArray.getFloat(attr, 0f);
                        break;
                    case 3:
                        tag.translationYOut = typedArray.getFloat(attr, 0f);
                        break;
                    default:
                        break;
                }
            }
            view.setTag(R.id.parallax_tag, tag);
            mParallaxViews.add(view);
        }
        typedArray.recycle();
    }

    public View createView(View parent, final String name, @NonNull Context context,
                           @NonNull AttributeSet attrs) {
        final boolean isPre21 = Build.VERSION.SDK_INT < 21;

        if (mCompatViewInflater == null) {
            mCompatViewInflater = new CompatViewInflater();
        }

        // We only want the View to inherit it's context if we're running pre-v21
        final boolean inheritContext = isPre21 && shouldInheritContext((ViewParent) parent);

        return mCompatViewInflater.createView(parent, name, context, attrs, inheritContext,
                isPre21, /* Only read android:theme pre-L (L+ handles this anyway) */
                true /* Read read app:theme as a fallback at all times for legacy reasons */
        );
    }

    private boolean shouldInheritContext(ViewParent parent) {
        if (parent == null) {
            // The initial parent is null so just return false
            return false;
        }
        while (true) {
            if (parent == null) {
                // Bingo. We've hit a view which has a null parent before being terminated from
                // the loop. This is (most probably) because it's the root view in an inflation
                // call, therefore we should inherit. This works as the inflated layout is only
                // added to the hierarchy at the end of the inflate() call.
                return true;
            } else if (!(parent instanceof View)
                    || ViewCompat.isAttachedToWindow((View) parent)) {
                // We have either hit the window's decor view, a parent which isn't a View
                // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                // is currently added to the view hierarchy. This means that it has not be
                // inflated in the current inflate() call and we should not inherit the context.
                return false;
            }
            parent = parent.getParent();
        }
    }

    public List<View> getParallaxViews() {
        return mParallaxViews;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }
}
