package com._56duong.capacitordeviceid;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class FloatingService extends Service {

  private WindowManager windowManager;
  private View floatingView;
  private float x, y;
  private float touchX, touchY;

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

//    floatingView.setOnClickListener(v -> {
//
//      Intent launchIntent =
//        getPackageManager()
//          .getLaunchIntentForPackage(getPackageName());
//
//      launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//      startActivity(launchIntent);
//
//      stopSelf();
//    });

    floatingView.setOnTouchListener((v, event) -> {
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          x = params.x;
          y = params.y;
          touchX = event.getRawX();
          touchY = event.getRawY();
          return true;

        case MotionEvent.ACTION_MOVE:
          params.x = (int) (x + (event.getRawX() - touchX));
          params.y = (int) (y + (event.getRawY() - touchY));
          windowManager.updateViewLayout(floatingView, params);
          return true;

        case MotionEvent.ACTION_UP:
          // Click if not moved much (optional)
          if (Math.abs(event.getRawX() - touchX) < 10 && Math.abs(event.getRawY() - touchY) < 10) {
            Intent launchIntent = getPackageManager()
                    .getLaunchIntentForPackage(getPackageName());
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launchIntent);
            stopSelf();
          }
          return true;
      }
      return false;
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
