package jp.memowidget;

import android.app.PendingIntent;
import android.appwidget.*;
import android.content.*;
import android.os.Bundle;
import android.widget.RemoteViews;

public class MemoWidget extends AppWidgetProvider {
    public static final class Small extends MemoWidget {}
    public static final class Medium extends MemoWidget {}
    public static final class Large extends MemoWidget {}
    @Override public void onUpdate(Context c, AppWidgetManager m, int[] ids) { for(int id:ids) update(c,m,id); }
    @Override public void onAppWidgetOptionsChanged(Context c,AppWidgetManager m,int id,Bundle options) { update(c,m,id); }
    @Override public void onDeleted(Context c,int[] ids) { for(int id:ids) Notes.unbind(c,id); }
    static void update(Context c,AppWidgetManager m,int id) {
        Notes.Note note=Notes.get(c,Notes.bound(c,id));
        RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget_note);
        v.setTextViewText(R.id.widget_body,note==null || note.body().trim().isEmpty()?"タップしてメモを書く":note.body());
        int height=m.getAppWidgetOptions(id).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,110);
        v.setInt(R.id.widget_body,"setMaxLines",Math.max(1,(height-24)/22));
        Intent intent=new Intent(c,EditorActivity.class).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id);
        intent.setData(android.net.Uri.parse("memowidget://edit/"+id));
        PendingIntent pi=PendingIntent.getActivity(c,id,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        v.setOnClickPendingIntent(R.id.widget_body,pi);
        m.updateAppWidget(id,v);
    }
    static void refresh(Context c) {
        AppWidgetManager m=AppWidgetManager.getInstance(c);
        for(Class<?> type:new Class<?>[]{Small.class,Medium.class,Large.class})
            for(int id:m.getAppWidgetIds(new ComponentName(c,type))) update(c,m,id);
    }
}
