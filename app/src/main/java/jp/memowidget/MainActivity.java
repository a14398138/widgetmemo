package jp.memowidget;

import android.app.*;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.os.Bundle;
import android.widget.*;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle state) { super.onCreate(state); }
    @Override public void onResume() { super.onResume(); render(); }
    private void render() {
        LinearLayout root=Ui.page(this);
        root.addView(Ui.text(this,"こつこつメモ",30,true));
        root.addView(Ui.text(this,"思いついたことを、ホーム画面に。",15,false));
        root.addView(Ui.button(this,"＋ 新しいメモ",()->startActivity(new Intent(this,EditorActivity.class))));
        root.addView(Ui.button(this,"ホーム画面にウィジェットを追加",this::chooseSize));
        root.addView(Ui.text(this,"ウィジェットをタップして編集。\n長押しすると、あとからサイズを変更できます。",13,false));
        ScrollView scroll=new ScrollView(this);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout list=new LinearLayout(this);list.setOrientation(1);scroll.addView(list);
        java.util.List<Notes.Note> notes=Notes.all(this);
        if(notes.isEmpty()) list.addView(Ui.text(this,"まだメモはありません\n「＋ 新しいメモ」から始めましょう。",18,false));
        for(Notes.Note n:notes) {
            LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setBackground(Ui.card());int p=Ui.dp(this,16);card.setPadding(p,p,p,p);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,Ui.dp(this,10),0,0);list.addView(card,lp);
            card.addView(Ui.text(this,n.title().trim().isEmpty()?"無題のメモ":n.title(),19,true));
            TextView body=Ui.text(this,n.body(),15,false);body.setMaxLines(4);body.setEllipsize(android.text.TextUtils.TruncateAt.END);card.addView(body);
            card.addView(Ui.text(this,DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).format(new Date(n.updated())),12,false));
            card.setContentDescription("メモを編集："+(n.title().trim().isEmpty()?"無題":n.title()));
            card.setOnClickListener(v->startActivity(new Intent(this,EditorActivity.class).putExtra("note_id",n.id())));
        }
    }
    private void chooseSize() {
        new AlertDialog.Builder(this).setTitle("ウィジェットのサイズ")
            .setItems(new String[]{"小（2 × 1）","中（2 × 2）","大（4 × 3）"},(d,which)->{
                AppWidgetManager m=AppWidgetManager.getInstance(this);
                if(m.isRequestPinAppWidgetSupported()) {
                    Class<?>[] types={MemoWidget.Small.class,MemoWidget.Medium.class,MemoWidget.Large.class};
                    m.requestPinAppWidget(new ComponentName(this,types[which]),null,null);
                    Toast.makeText(this,"配置後、ウィジェットをタップしてメモを設定してください",Toast.LENGTH_LONG).show();
                } else new AlertDialog.Builder(this).setMessage("ホーム画面の空きスペースを長押し → ウィジェット → こつこつメモから、サイズを選んで追加してください。").setPositiveButton("OK",null).show();
            }).setNegativeButton("キャンセル",null).show();
    }
}
