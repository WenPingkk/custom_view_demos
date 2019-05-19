package com.sean.multi_choose_item;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sean.multi_choose_item.adapter.ListScreenMenuAdapter;
import com.sean.multi_choose_item.views.ListDataScreenView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListDataScreenView listDataScreenView = findViewById(R.id.list_data_screen_view);
        listDataScreenView.setAdapter(new ListScreenMenuAdapter(this));
    }
}
