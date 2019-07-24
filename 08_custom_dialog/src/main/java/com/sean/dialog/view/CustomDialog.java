package com.sean.dialog.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.sean.dialog.R;

/**
 * Author WenPing
 * CreateTime 2019/7/24.
 * Description:
 */
public class CustomDialog extends Dialog {


    private Window mWindow;

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
    }

    @Override
    public void show() {
        super.show();
        if (isFull) {
            fullWidth();
        }
    }
}
