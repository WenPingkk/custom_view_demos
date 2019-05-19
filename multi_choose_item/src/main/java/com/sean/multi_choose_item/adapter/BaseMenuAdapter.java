package com.sean.multi_choose_item.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Author WenPing
 * CreateTime 2019/5/19.
 * Description:
 */
public abstract class BaseMenuAdapter {

    public abstract int getCount();

    public abstract View getTabView(int position, ViewGroup parent);

    public abstract View getMenuView(int position, ViewGroup parent);

    /**
     * 关闭菜单
     * @param tabView
     */
    public void menuClose(View tabView) {
        
    }

    public void menuOpen(View tabView) {
        
    }
}
