package com.sean.bezier.bezier_drag_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MessageBubbleView.attach(findViewById(R.id.text_view), new BubbleMessageTouchListener.BubbleDisappearListener() {
            @Override
            public void dismiss(View view) {
                Toast.makeText(MainActivity.this,"Dismiss view",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
