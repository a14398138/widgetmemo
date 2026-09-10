package jp.memowidget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.*;
import android.widget.*;

final class Ui {
    static final int INK=Color.rgb(41,54,46), GREEN=Color.rgb(65,98,79);
    static int dp(Activity a,int n) { return Math.round(n*a.getResources().getDisplayMetrics().density); }
    static LinearLayout page(Activity a) {
        LinearLayout l=new LinearLayout(a); l.setOrientation(LinearLayout.VERTICAL); l.setBackgroundColor(Color.rgb(246,245,239));
        int p=dp(a,22); l.setPadding(p,p,p,p); a.setContentView(l);
        if(Build.VERSION.SDK_INT>=30) l.setOnApplyWindowInsetsListener((v,i)->{android.graphics.Insets b=i.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.ime());v.setPadding(p+b.left,p+b.top,p+b.right,p+b.bottom);return i;});
        return l;
    }
    static TextView text(Activity a,String value,int size,boolean bold) {
        TextView t=new TextView(a); t.setText(value);t.setTextSize(size);t.setTextColor(INK);
        if(bold)t.setTypeface(null,Typeface.BOLD); t.setPadding(0,dp(a,8),0,dp(a,8));return t;
    }
    static Button button(Activity a,String value,Runnable action) {
        Button b=new Button(a);b.setText(value);b.setTextColor(GREEN);b.setAllCaps(false);b.setMinHeight(dp(a,48));b.setOnClickListener(v->action.run());return b;
    }
    static GradientDrawable card() { GradientDrawable d=new GradientDrawable();d.setColor(Color.rgb(255,242,190));d.setCornerRadius(32);return d; }
}
