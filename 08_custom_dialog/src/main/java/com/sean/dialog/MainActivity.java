package com.sean.dialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sean.dialog.view.CustomDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.btn_show_dialog);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CustomDialog dialog = new CustomDialog(this, R.style.dialog).isFullWidth(true);
        dialog.show();
    }


}
