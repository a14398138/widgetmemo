package jp.memowidget;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;
import java.util.*;

final class Notes {
    record Note(String id, String title, String body, long updated) {}
    static SharedPreferences prefs(Context c) { return c.getSharedPreferences("notes", Context.MODE_PRIVATE); }
    static Note get(Context c, String id) {
        if (id == null) return null;
        String raw = prefs(c).getString("note_" + id, null);
        if (raw == null) return null;
        try { JSONObject j = new JSONObject(raw); return new Note(id,j.optString("title"),j.optString("body"),j.optLong("updated")); }
        catch (Exception e) { return null; }
    }
    static boolean save(Context c, String id, String title, String body) {
        try { JSONObject j = new JSONObject(); j.put("title", title); j.put("body", body); j.put("updated", System.currentTimeMillis());
            return prefs(c).edit().putString("note_" + id, j.toString()).commit();
        } catch (Exception e) { return false; }
    }
    static List<Note> all(Context c) {
        List<Note> result = new ArrayList<>();
        for (String key : prefs(c).getAll().keySet()) if (key.startsWith("note_")) { Note n=get(c,key.substring(5)); if(n!=null) result.add(n); }
        result.sort((a,b)->Long.compare(b.updated(),a.updated())); return result;
    }
    static String bound(Context c,int widget) { return prefs(c).getString("widget_"+widget,null); }
    static boolean bind(Context c,int widget,String id) { return prefs(c).edit().putString("widget_"+widget,id).commit(); }
    static void unbind(Context c,int widget) { prefs(c).edit().remove("widget_"+widget).apply(); }
    static boolean delete(Context c,String id) {
        SharedPreferences.Editor e=prefs(c).edit().remove("note_"+id);
        for (Map.Entry<String,?> v:prefs(c).getAll().entrySet()) if(v.getKey().startsWith("widget_") && id.equals(v.getValue())) e.remove(v.getKey());
        return e.commit();
    }
}
