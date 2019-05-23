package com.sean.loadingviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View loadingView = findViewById(R.id.load_view);
        //避免内存泄漏的方式是
        if (loadingView != null) {
            loadingView.clearAnimation();
            loadingView.setVisibility(View.GONE);
        }

    }
}
