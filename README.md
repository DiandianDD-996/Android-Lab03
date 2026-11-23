# 实验 3 · Android 界面组件

## 运行

在 Android Studio 中打开本仓库根目录，使用 JDK 17、Android SDK Platform 35 和 Build Tools 34.0.0。选择 API 26 或更高版本的设备，等待 Gradle 同步后运行 app。Windows 命令行也可在仓库根目录执行：

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

第一条只构建，第二条需要已连接且获授权的设备或启动的模拟器。仓库只跟踪源码、资源与构建配置，不提交本机 SDK、缓存、密钥或 APK。

## 验收路径

1. 动物列表与通知：点击狮子、老虎等不同项，核对 Toast 名称，并拉下通知栏。标题应为所点动物，图标与应用图标一致。Android 13 及以上首次需允许通知；拒绝后可去系统设置开启。列表使用 SimpleAdapter 和课程配图。
2. 自定义对话框：点击打开，检查用户名、密码、Cancel、Sign in；取消关闭，确认给出提示。通过 Builder.setView 加载布局，没有网络登录、不会保存或发送账号密码。
3. XML 菜单：在系统菜单或“打开菜单”中选小/中/大（10/16/20sp）、红/黑；观察测试文本；普通菜单项提供 Toast。
4. 上下文操作：长按列表行开启 ActionMode，再点其他行多选，操作栏显示选中数，可以删除或退出。
5. RecyclerView：添加多条至超出屏幕，滑动至底部再向上，点不同动物核对 Toast。列表增加记录后立即显示，Adapter 在点击时按当前绑定位置取数据。

关键文件：`Main.java`、`res/layout/animal.xml`、`res/layout/login.xml`、`res/menu/sample.xml`。
