package com.sean.parallax;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sean.parallax.animation.ParallaxViewPager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParallaxViewPager mParallaxViewPager = findViewById(R.id.parallax_vp);
        /**
         * 传入四个布局
         * 分别创建对应的四个fragment
         */
        mParallaxViewPager.setLayout(getSupportFragmentManager(),
                new int[]{R.layout.fragment_page_first,R.layout.fragment_page_second,
                        R.layout.fragment_page_third,R.layout.fragment_page_first});
    }
}
