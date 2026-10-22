package com.brader.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout root, appGrid;
    private final List<View> tiles = new ArrayList<>();
    private int purple = Color.rgb(167, 139, 250);

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        buildUi();
        loadApps();
    }

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d;
    }
    private TextView text(String s, int size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setGravity(Gravity.CENTER_VERTICAL); return t;
    }
    private void buildUi() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(34, 24, 34, 24);
        root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{Color.rgb(20,16,42), Color.rgb(7,8,18), Color.rgb(26,13,42)}));
        setContentView(root);
        LinearLayout header = new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL); header.setOrientation(LinearLayout.HORIZONTAL);
        TextView brand = text("BRADER  TV", 28, Color.WHITE); brand.setTypeface(Typeface.DEFAULT_BOLD); header.addView(brand, new LinearLayout.LayoutParams(0, 56, 1));
        String now = new SimpleDateFormat("EEE, d MMM  •  HH:mm", Locale.getDefault()).format(new Date());
        TextView clock = text(now, 16, Color.LTGRAY); clock.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL); header.addView(clock);
        root.addView(header);
        TextView sub = text("Your apps. Your screen.", 15, Color.rgb(190,181,220)); sub.setPadding(2, 0, 0, 16); root.addView(sub);
        LinearLayout bar = new LinearLayout(this); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setPadding(16, 10, 16, 10); bar.setBackground(bg(0x442A2347, 18));
        TextView title = text("APPS", 14, purple); title.setTypeface(Typeface.DEFAULT_BOLD); bar.addView(title, new LinearLayout.LayoutParams(0, 34, 1));
        TextView refresh = text("↻  Refresh", 14, Color.WHITE); refresh.setFocusable(true); refresh.setPadding(14, 4, 14, 4); refresh.setBackground(bg(0x553F3560, 12)); refresh.setOnClickListener(v -> loadApps()); bar.addView(refresh);
        root.addView(bar);
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(false); LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, 0, 1); sp.topMargin = 14; root.addView(scroll, sp);
        appGrid = new LinearLayout(this); appGrid.setOrientation(LinearLayout.VERTICAL); scroll.addView(appGrid);
        TextView footer = text("Select an app with the remote  •  Hold OK for app info", 12, 0xFF9B96AD); footer.setGravity(Gravity.CENTER); footer.setPadding(0, 12, 0, 0); root.addView(footer);
    }

    private void loadApps() {
        if (appGrid == null) return;
        appGrid.removeAllViews(); tiles.clear();
        PackageManager pm = getPackageManager();
        Intent query = new Intent(Intent.ACTION_MAIN); query.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<android.content.pm.ResolveInfo> found = pm.queryIntentActivities(query, 0);
        if (found.isEmpty()) { query.removeCategory(Intent.CATEGORY_LEANBACK_LAUNCHER); query.addCategory(Intent.CATEGORY_LAUNCHER); found = pm.queryIntentActivities(query, 0); }
        Collections.sort(found, (a,b) -> String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(pm).toString(), b.loadLabel(pm).toString()));
        LinearLayout row = null; int col = 0;
        for (android.content.pm.ResolveInfo info : found) {
            if (info.activityInfo.packageName.equals(getPackageName())) continue;
            if (row == null || col == 4) { row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.TOP); appGrid.addView(row); col = 0; }
            final String pkg = info.activityInfo.packageName;
            LinearLayout tile = new LinearLayout(this); tile.setOrientation(LinearLayout.VERTICAL); tile.setGravity(Gravity.CENTER); tile.setPadding(8, 14, 8, 14); tile.setFocusable(true); tile.setClickable(true); tile.setBackground(bg(0x332F2A47, 18));
            TextView icon = text("", 26, Color.WHITE); icon.setGravity(Gravity.CENTER); try { icon.setCompoundDrawablesWithIntrinsicBounds(null, info.loadIcon(pm), null, null); } catch (Exception ignored) {}
            tile.addView(icon, new LinearLayout.LayoutParams(-1, 58));
            TextView label = text(info.loadLabel(pm).toString(), 14, Color.WHITE); label.setGravity(Gravity.CENTER); label.setMaxLines(2); tile.addView(label, new LinearLayout.LayoutParams(-1, 42));
            tile.setOnFocusChangeListener((v, has) -> v.setBackground(bg(has ? 0xFF40365F : 0x332F2A47, 18)));
            tile.setOnClickListener(v -> launch(pkg));
            tile.setOnLongClickListener(v -> { new AlertDialog.Builder(this).setTitle(info.loadLabel(pm)).setItems(new String[]{"Open app", "App info"}, (d, which) -> { if (which == 0) launch(pkg); else { Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS); i.setData(android.net.Uri.parse("package:" + pkg)); startActivity(i); } }).show(); return true; });
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, 124, 1); tp.setMargins(5, 5, 5, 5); row.addView(tile, tp); tiles.add(tile); col++;
        }
        if (tiles.isEmpty()) { TextView empty = text("No TV apps found.", 18, Color.WHITE); appGrid.addView(empty); }
        if (!tiles.isEmpty()) tiles.get(0).requestFocus();
    }
    private void launch(String pkg) {
        try { Intent i = getPackageManager().getLaunchIntentForPackage(pkg); if (i != null) startActivity(i); else Toast.makeText(this, "Can't open this app", Toast.LENGTH_SHORT).show(); }
        catch (Exception e) { Toast.makeText(this, "Unable to launch app", Toast.LENGTH_SHORT).show(); }
    }
    @Override public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            View v = getCurrentFocus(); if (v != null) return v.performLongClick();
        }
        return super.onKeyLongPress(keyCode, event);
    }
}
