package com._56duong.capacitordeviceid;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class FloatingService extends Service {

  private WindowManager windowManager;
  private View floatingView;

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    floatingView = LayoutInflater.from(this)
      .inflate(R.layout.floating_button, null);

    int layoutFlag;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    } else {
      layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
    }

    final WindowManager.LayoutParams params =
      new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        layoutFlag,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
      );

    params.gravity = Gravity.TOP | Gravity.START;
    params.x = 20;
    params.y = 100;

    windowManager =
      (WindowManager)getSystemService(WINDOW_SERVICE);

    windowManager.addView(floatingView, params);

    floatingView.setOnClickListener(v -> {

      Intent launchIntent =
        getPackageManager()
          .getLaunchIntentForPackage(getPackageName());

      launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      startActivity(launchIntent);

      stopSelf();
    });

    return START_NOT_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onDestroy() {

    super.onDestroy();

    if (floatingView != null) {
      windowManager.removeView(floatingView);
    }
  }
}
