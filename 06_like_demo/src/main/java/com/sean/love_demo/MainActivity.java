package com.sean.love_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private LoveLayout mLoveLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoveLayout = findViewById(R.id.love_layout);
    }

    public void btn_love(View view) {
        mLoveLayout.addLove();
    }
}
