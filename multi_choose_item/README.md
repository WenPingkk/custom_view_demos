## 多条目菜单筛选,下拉显示功能实现
### 1.adapter的实现
- tab显示效果
- 菜单内容显示效果
###ListDataScreenView 实现
- 代码形式绘制视图.
- 竖向线性布局 = mMenuTabView+[mMenuMiddleView]
- [mMenuMiddleView] = [阴影]+[mMenuContainerView]
- mMenuTabView 默认是全屏的.
###setAdapter方法中的实现
- tabView和menuView填充到对应视图中.
- 添加tab点击事件.
##tab点击事件实现.
- 根据mCurrentPosition 判断打开菜单,关闭菜单,更新菜单效果.
- 动画实现.
- 透明动画 加监听,然后再调用start方法,不然容易出现 空指针问题.
- 菜单关闭,对应 mCurrentPosition 重新赋值.