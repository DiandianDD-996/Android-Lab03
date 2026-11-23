package cn.hsj.widgets;

import android.Manifest;
import android.app.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.*;
import java.util.*;

public class Main extends Activity {
  final String[] names = {"Lion", "Tiger", "Monkey", "Dog", "Cat", "Elephant"};
  final int[] pics = {
    R.drawable.lion,
    R.drawable.tiger,
    R.drawable.monkey,
    R.drawable.dog,
    R.drawable.cat,
    R.drawable.elephant
  };
  final ArrayList<Integer> data = new ArrayList<>();
  final String[] pages = {"界面组件", "动物列表与通知", "自定义对话框", "XML 菜单", "上下文操作", "RecyclerView"};
  int page;
  String pending;
  LinearLayout root;
  TextView sample;

  public void onCreate(Bundle s) {
    super.onCreate(s);
    if (s != null && s.containsKey("data")) data.addAll(s.getIntegerArrayList("data"));
    else for (int i = 0; i < names.length; i++) data.add(i);
    pending = s == null ? null : s.getString("pending");
    show(s == null ? 0 : s.getInt("page"));
  }

  protected void onSaveInstanceState(Bundle s) {
    super.onSaveInstanceState(s);
    s.putInt("page", page);
    s.putString("pending", pending);
    s.putIntegerArrayList("data", new ArrayList<>(data));
  }

  void toast(String s) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
  }

  void button(String label, Runnable action) {
    Button b = new Button(this);
    b.setAllCaps(false);
    b.setText(label);
    root.addView(b);
    b.setOnClickListener(v -> action.run());
  }

  void show(int p) {
    page = p;
    invalidateOptionsMenu();
    setTitle(pages[p]);
    root = new LinearLayout(this);
    root.setOrientation(1);
    setContentView(root);
    switch (p) {
      case 0:
        for (int i = 1; i < pages.length; i++) {
          final int idx = i;
          button(pages[i], () -> show(idx));
        }
        break;
      case 1:
        animals();
        break;
      case 2:
        button(
            "打开登录对话框",
            () ->
                new AlertDialog.Builder(this)
                    .setView(getLayoutInflater().inflate(R.layout.login, root, false))
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Sign in", (d, w) -> toast("已点击 Sign in"))
                    .show());
        break;
      case 3:
        sample = new TextView(this);
        sample.setText("用于测试的内容");
        root.addView(sample);
        button(
            "打开菜单",
            () -> {
              PopupMenu m = new PopupMenu(this, root.getChildAt(1));
              m.inflate(R.menu.sample);
              m.setOnMenuItemClickListener(this::choose);
              m.show();
            });
        break;
      case 4:
        contextList();
        break;
      case 5:
        recycler();
        break;
    }
  }

  void animals() {
    ListView lv = new ListView(this);
    root.addView(lv);
    ArrayList<Map<String, Object>> rows = new ArrayList<>();
    for (int i = 0; i < names.length; i++) {
      Map<String, Object> m = new HashMap<>();
      m.put("name", names[i]);
      m.put("pic", pics[i]);
      rows.add(m);
    }
    lv.setAdapter(
        new SimpleAdapter(
            this,
            rows,
            R.layout.animal,
            new String[] {"name", "pic"},
            new int[] {R.id.name, R.id.photo}));
    lv.setOnItemClickListener(
        (a, v, i, id) -> {
          String name = names[i];
          toast(name);
          if (Build.VERSION.SDK_INT >= 33
              && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                  != PackageManager.PERMISSION_GRANTED) {
            pending = name;
            requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, 32);
          } else notifyAnimal(name);
        });
  }

  void notifyAnimal(String name) {
    NotificationManager nm = getSystemService(NotificationManager.class);
    nm.createNotificationChannel(
        new NotificationChannel("animals", "动物列表", NotificationManager.IMPORTANCE_DEFAULT));
    nm.notify(
        100 + Arrays.asList(names).indexOf(name),
        new Notification.Builder(this, "animals")
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(name)
            .setContentText("已选择动物：" + name)
            .setAutoCancel(true)
            .build());
  }

  public void onRequestPermissionsResult(int r, String[] p, int[] g) {
    super.onRequestPermissionsResult(r, p, g);
    if (r == 32) {
      if (g.length > 0 && g[0] == PackageManager.PERMISSION_GRANTED && pending != null)
        notifyAnimal(pending);
      else toast("未授权通知；可在系统设置中开启");
      pending = null;
    }
  }

  public boolean onCreateOptionsMenu(Menu m) {
    if (page != 3) return false;
    getMenuInflater().inflate(R.menu.sample, m);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem i) {
    return choose(i) || super.onOptionsItemSelected(i);
  }

  boolean choose(MenuItem i) {
    int id = i.getItemId();
    if (id == R.id.plain) toast("普通菜单项");
    else if (id == R.id.red || id == R.id.black)
      sample.setTextColor(id == R.id.red ? Color.RED : Color.BLACK);
    else if (id == R.id.size10 || id == R.id.size16 || id == R.id.size20)
      sample.setTextSize(id == R.id.size10 ? 10 : id == R.id.size16 ? 16 : 20);
    else return false;
    return true;
  }

  void contextList() {
    ListView lv = new ListView(this);
    root.addView(lv);
    ArrayList<String> rows = new ArrayList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
    ArrayAdapter<String> a =
        new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, rows);
    lv.setAdapter(a);
    lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    lv.setMultiChoiceModeListener(
        new AbsListView.MultiChoiceModeListener() {
          public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add("删除").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            return true;
          }

          public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
          }

          public void onItemCheckedStateChanged(ActionMode m, int p, long id, boolean checked) {
            m.setTitle(lv.getCheckedItemCount() + " selected");
          }

          public boolean onActionItemClicked(ActionMode m, MenuItem i) {
            for (int j = rows.size() - 1; j >= 0; j--) if (lv.isItemChecked(j)) rows.remove(j);
            a.notifyDataSetChanged();
            m.finish();
            return true;
          }

          public void onDestroyActionMode(ActionMode m) {}
        });
  }

  void recycler() {
    RecyclerView list = new RecyclerView(this);
    Animals adapter = new Animals();
    button(
        "增加一条记录",
        () -> {
          int at = data.size();
          data.add(at % names.length);
          adapter.notifyItemInserted(at);
          list.scrollToPosition(at);
        });
    root.addView(list);
    list.setLayoutManager(new LinearLayoutManager(this));
    list.setAdapter(adapter);
  }

  static class Holder extends RecyclerView.ViewHolder {
    TextView name;
    ImageView pic;

    Holder(View v) {
      super(v);
      name = v.findViewById(R.id.name);
      pic = v.findViewById(R.id.photo);
    }
  }

  class Animals extends RecyclerView.Adapter<Holder> {
    public Holder onCreateViewHolder(ViewGroup p, int type) {
      return new Holder(getLayoutInflater().inflate(R.layout.animal, p, false));
    }

    public void onBindViewHolder(Holder h, int p) {
      int i = data.get(p);
      h.name.setText(names[i]);
      h.pic.setImageResource(pics[i]);
      h.itemView.setOnClickListener(
          v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) toast(names[data.get(pos)]);
          });
    }

    public int getItemCount() {
      return data.size();
    }
  }

  public void onBackPressed() {
    if (page != 0) show(0);
    else super.onBackPressed();
  }
}
