# 视差 viewpager
- 难点 解析 属性的过程;没有styleable 修饰的attr属性的解析过程
- inflater 单例方式在fragment解析属性过程中会遇到的问题.解决方式是什么? clone操作.
- viewpager 监听 位移滑动 重写的方法中 onScrollChanged方法 三个参数的意义和用法.
- -position 当前位置    positionOffset 0-1     positionOffsetPixels 0-屏幕的宽度px
## TextView源码解析属性的思想运用
- 理解这句代码的意思:TypedArray typedArray = context.obtainStyledAttributes(attrs, mParallaxAttrs);
- 后续的解析都是从这里开始的.