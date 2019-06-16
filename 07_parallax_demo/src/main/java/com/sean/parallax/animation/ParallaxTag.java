package com.sean.parallax.animation;

import android.support.annotation.NonNull;

/**
 * Author WenPing
 * CreateTime 2019/6/16.
 * Description:
 */
public class ParallaxTag {

    public float translationXIn;
    public float translationXOut;
    public float translationYIn;
    public float translationYOut;

    @NonNull
    @Override
    public String toString() {
        return "ParallaxTag{" +
                "translationXIn=" + translationXIn +
                ", translationXOut=" + translationXOut +
                ", translationYIn=" + translationYIn +
                ", translationYOut=" + translationYOut +
                '}';
    }
}
