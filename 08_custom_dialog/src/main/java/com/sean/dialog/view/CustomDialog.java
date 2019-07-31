package com.sean.dialog.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.sean.dialog.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author WenPing
 * CreateTime 2019/7/24.
 * Description:
 */
public class CustomDialog extends Dialog {


    private Window mWindow;
    private WheelView mHourWheelView;
    private WheelView mMinWheelView;
    private TextView mTvHour;
    private TextView mTvMin;

    public CustomDialog(Context context) {
        this(context, 0);
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
        mWindow = getWindow();
        mWindow.setWindowAnimations(R.style.dialog_from_bottom_anim);
        mWindow.setGravity(Gravity.BOTTOM);
    }

    private boolean isFull = false;

    public CustomDialog isFullWidth(boolean isFull) {
        this.isFull = isFull;
        return this;
    }

    /**
     * @return
     */
    private CustomDialog fullWidth() {
        WindowManager.LayoutParams layoutParams = mWindow.getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(layoutParams);
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog);
        initData();
        initView();
        initListener();
    }

    private void initListener() {
        mHourWheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mTvHour.setText(hourDatas.get(index));
            }
        });
        mMinWheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mTvMin.setText(minData.get(index));
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 24; i++) {
            hourDatas.add("" + i);
        }

        for (int i = 0; i < 60; i++) {
            minData.add("" + i);
        }
    }

    private List<String> hourDatas = new ArrayList<>();
    private List<String> minData = new ArrayList<>();
    private void initView() {
        mTvHour = findViewById(R.id.tv_hour);
        mTvMin = findViewById(R.id.tv_min);
        mHourWheelView = findViewById(R.id.wheel_view_hour);
        mMinWheelView = findViewById(R.id.wheel_view_min);

        mHourWheelView.setAdapter(new ArrayWheelAdapter(hourDatas));
        mMinWheelView.setAdapter(new ArrayWheelAdapter(minData));
        mHourWheelView.setCurrentItem(0);
        mMinWheelView.setCurrentItem(0);
        mTvHour.setText(hourDatas.get(0));
        mTvMin.setText(minData.get(0));
    }

    @Override
    public void show() {
        super.show();
        if (isFull) {
            fullWidth();
        }
    }
}
