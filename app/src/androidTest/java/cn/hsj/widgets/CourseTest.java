package cn.hsj.widgets;

import static org.junit.Assert.*;

import android.content.*;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.*;
import org.junit.Test;

public class CourseTest {
  UiDevice d;

  UiObject2 obj(String text) {
    UiObject2 item = d.wait(Until.findObject(By.text(text)), 5000);
    assertNotNull("未找到：" + text, item);
    return item;
  }

  void has(String text) {
    obj(text);
  }

  void hasPart(String part) {
    assertTrue(part, d.wait(Until.hasObject(By.textContains(part)), 5000));
  }

  void click(String s) {
    obj(s).click();
    d.waitForIdle();
  }

  void back() {
    d.pressBack();
    d.waitForIdle();
  }

  void input(int i, String s) {
    d.findObjects(By.clazz("android.widget.EditText")).get(i).setText(s);
    d.waitForIdle();
  }

  @Test
  public void acceptance() throws Exception {
    d = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
    Intent launch = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
    assertNotNull(launch);
    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    ctx.startActivity(launch);
    d.waitForIdle();
    click("动物列表与通知");
    click("Elephant");
    if (android.os.Build.VERSION.SDK_INT >= 33) {
      UiObject2 allow =
          d.wait(
              Until.findObject(
                  By.res("com.android.permissioncontroller", "permission_allow_button")),
              1800);
      if (allow != null) allow.click();
    }
    d.openNotification();
    has("Elephant");
    back();
    back();
    click("自定义对话框");
    click("打开登录对话框");
    has("Username");
    has("Password");
    back();
    back();
    click("XML 菜单");
    click("打开菜单");
    click("字体大小");
    click("大");
    has("用于测试的内容");
    back();
    click("上下文操作");
    obj("One").longClick();
    has("1 selected");
    click("Two");
    has("2 selected");
    back();
    back();
    click("RecyclerView");
    has("Lion");
    for (int i = 0; i < 8; i++) click("增加一条记录");
    has("Tiger");
    click("Tiger");
  }
}
