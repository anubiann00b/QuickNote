package me.shreyasr.quicknote;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import me.shreyasr.quicknote.window.NotepadWindow;
import wei.mark.standout.StandOutWindow;

public class App extends Application {

    private static App instance;
    public static App get() { return instance; }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        backupManager = new BackupManager(this);
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setDryRun(BuildConfig.DEBUG);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.tracker_id));
        tracker.setScreenName("QuickNote Tracker");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
    }

    Tracker tracker = null;
    GoogleAnalytics analytics = null;

    public static void track(String category, String action) {
        instance.tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    public static void screen(String name) {
        instance.tracker.setScreenName(name);
        instance.tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    BackupManager backupManager = null;
    public BackupManager getBackupManager() { return backupManager; }

    public String getAppName() {
        return getString(R.string.app_name);
    }

    public int getAppIcon() {
        return R.drawable.ic_launcher;
    }

    public String getPersistentNotificationTitle() {
        return getAppName();
    }

    public String getPersistentNotificationMessage() {
        return getString(R.string.saveAndClose);
    }

    public Intent getPersistentNotificationIntent() {
        return StandOutWindow.getCloseAllIntent(this, NotepadWindow.class);
    }

    public Notification getPersistentNotification() {
        Notification.Builder n = new Notification.Builder(this);
        n.setSmallIcon(getAppIcon());
        n.setContentTitle(getPersistentNotificationTitle());
        n.setContentText(getPersistentNotificationMessage());
        n.setPriority(Notification.PRIORITY_MIN);
        n.setContentIntent(PendingIntent.getService(this, 0, getPersistentNotificationIntent(), PendingIntent.FLAG_UPDATE_CURRENT));
        return n.build();
    }

    public SharedPreferences getSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    public Point getScreenSize() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static void enableDialog(MaterialDialog dialog, IBinder windowToken) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.token = windowToken;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        window.setAttributes(params);
    }
}