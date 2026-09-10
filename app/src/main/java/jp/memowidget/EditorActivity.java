package jp.memowidget;

import android.app.*;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.widget.*;
import java.util.*;

public class EditorActivity extends Activity {
    private EditText title,body;
    private String noteId;
    private int widgetId;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);setResult(RESULT_CANCELED);
        widgetId=getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        if(widgetId!=AppWidgetManager.INVALID_APPWIDGET_ID) {
            android.appwidget.AppWidgetProviderInfo info=AppWidgetManager.getInstance(this).getAppWidgetInfo(widgetId);
            if(info==null || !getPackageName().equals(info.provider.getPackageName())) { finish(); return; }
        }
        noteId=state!=null?state.getString("note_id"):getIntent().getStringExtra("note_id");
        if(noteId==null && widgetId!=AppWidgetManager.INVALID_APPWIDGET_ID) noteId=Notes.bound(this,widgetId);
        if(noteId==null)noteId=UUID.randomUUID().toString();
        Notes.Note note=Notes.get(this,noteId);
        LinearLayout root=Ui.page(this);
        root.addView(Ui.text(this,"メモを書く",28,true));
        root.addView(Ui.text(this,"保存すると、ホーム画面にも届きます。",14,false));
        if(widgetId!=AppWidgetManager.INVALID_APPWIDGET_ID)root.addView(Ui.button(this,"既存のメモを選ぶ",this::selectNote));
        title=new EditText(this);title.setId(1001);title.setHint("タイトル（省略可）");title.setSingleLine(true);title.setTextColor(Ui.INK);title.setTextSize(20);title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(120)});root.addView(title);
        body=new EditText(this);body.setId(1002);body.setHint("ここにメモを書いてください…");body.setTextColor(Ui.INK);body.setTextSize(18);body.setGravity(Gravity.TOP|Gravity.START);
        body.setInputType(android.text.InputType.TYPE_CLASS_TEXT|android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE|android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        body.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10000)});root.addView(body,new LinearLayout.LayoutParams(-1,0,1));
        if(note!=null){title.setText(note.title());body.setText(note.body());}
        root.addView(Ui.button(this,"保存して閉じる",this::save));
        root.addView(Ui.button(this,"キャンセル",this::finish));
        if(note!=null) root.addView(Ui.button(this,"このメモを削除",()->new AlertDialog.Builder(this).setTitle("メモを削除しますか？").setMessage("ウィジェットの表示も解除されます。元に戻せません。")
            .setNegativeButton("キャンセル",null).setPositiveButton("削除",(d,w)->{if(Notes.delete(this,noteId)){MemoWidget.refresh(this);finish();}else error();}).show()));
    }
    @Override public void onSaveInstanceState(Bundle state){state.putString("note_id",noteId);super.onSaveInstanceState(state);}
    private void selectNote(){
        java.util.List<Notes.Note> notes=Notes.all(this);
        if(notes.isEmpty()){Toast.makeText(this,"保存済みのメモがありません",Toast.LENGTH_SHORT).show();return;}
        String[] names=new String[notes.size()];for(int i=0;i<names.length;i++)names[i]=notes.get(i).title().trim().isEmpty()?notes.get(i).body():notes.get(i).title();
        new AlertDialog.Builder(this).setTitle("表示するメモを選択（編集中の内容は破棄）").setItems(names,(d,w)->{
            if(Notes.bind(this,widgetId,notes.get(w).id()))complete();else error();
        }).setNegativeButton("キャンセル",null).show();
    }
    private void save(){
        if(title.getText().toString().trim().isEmpty() && body.getText().toString().trim().isEmpty()){body.setError("メモを入力してください");return;}
        if(!Notes.save(this,noteId,title.getText().toString(),body.getText().toString())){error();return;}
        if(widgetId!=AppWidgetManager.INVALID_APPWIDGET_ID && !Notes.bind(this,widgetId,noteId)){error();return;}
        complete();
    }
    private void complete(){MemoWidget.refresh(this);setResult(RESULT_OK,new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId));finish();}
    private void error(){Toast.makeText(this,"保存できませんでした。空き容量を確認してください。",Toast.LENGTH_LONG).show();}
}
