package com._56duong.capacitordeviceid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;

import android.app.Activity;
import android.view.WindowManager;

@CapacitorPlugin(name = "DeviceId")
public class DeviceIdPlugin extends Plugin {

    @PluginMethod
    public void getDeviceId(PluginCall call) {
        JSObject deviceInfo = new DeviceId(getContext()).getDeviceId();
        call.resolve(deviceInfo);
    }

    @PluginMethod
    public void setKeyboardEnabled(PluginCall call) {
        boolean enabled = Boolean.TRUE.equals(call.getBoolean("enabled", true));
        Activity activity = getActivity();
        activity.runOnUiThread(() -> {
            if (enabled) {
                // allow keyboard
                activity.getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                );
            } else {
                // disable keyboard
                activity.getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                );
            }
        });
        call.resolve();
    }


    @PluginMethod
    public void showFloatingButton(PluginCall call) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(getContext())) {

                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getContext().getPackageName())
                );

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getContext().startActivity(intent);

                call.reject("Overlay permission required");

                return;
            }
        }

        Intent serviceIntent =
                new Intent(getContext(), FloatingService.class);

        getContext().startService(serviceIntent);

        call.resolve();
    }


    @PluginMethod
    public void openWifiSettings(PluginCall call) {

        Intent wifiIntent =
                new Intent(Settings.ACTION_WIFI_SETTINGS);

        wifiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        getContext().startActivity(wifiIntent);

        call.resolve();
    }


    @PluginMethod
    public void openTeamViewer(PluginCall call) {

        Intent intent = new Intent();

        intent.setClassName(
                "com.teamviewer.quicksupport.market",
                "com.teamviewer.quicksupport.ui.QSActivity"
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        );

        try {

            getContext().startActivity(intent);

            call.resolve();

        } catch (Exception e) {

            call.reject(e.toString());
        }
    }



    private BroadcastReceiver usbReceiver;

    @Override
    public void load() {
        super.load();
        registerUsbReceiver();
    }

    @Override
    protected void handleOnDestroy() {
        super.handleOnDestroy();
        if (usbReceiver != null) {
            try {
                getContext().unregisterReceiver(usbReceiver);
            } catch (Exception ignored) {
            }
        }
    }

    private void registerUsbReceiver() {
        usbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                    Log.d("USB", "USB device attached");
                    // Wait for storage mount
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        JSArray devices = getUsbStorageList();
                        JSObject ret = new JSObject();
                        ret.put("devices", devices);
                        notifyListeners("usbAttached", ret);
                    }, 2000);
                } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    Log.d("USB", "USB device detached");
                    JSObject ret = new JSObject();
                    notifyListeners("usbDetached", ret);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        getContext().registerReceiver(usbReceiver, filter);
    }

    @PluginMethod
    public void scanUsb(PluginCall call) {
        JSArray result = getUsbStorageList();
        JSObject ret = new JSObject();
        ret.put("devices", result);
        call.resolve(ret);
    }

    private JSArray getUsbStorageList() {
        JSArray result = new JSArray();
        try {
            File[] dirs = getContext().getExternalFilesDirs(null);
            if (dirs == null) {
                return result;
            }
            for (File dir : dirs) {
                if (dir == null) continue;
                String fullPath = dir.getAbsolutePath();
                Log.d("USB", "External dir: " + fullPath);
                // Skip internal storage
                if (fullPath.contains("emulated")) {
                    continue;
                }
                // Usually:
                // /storage/601B-309F/Android/data/your.package/files
                String usbRoot = fullPath;
                int androidIndex = fullPath.indexOf("/Android");
                if (androidIndex > 0) {
                    usbRoot = fullPath.substring(0, androidIndex);
                }
                Log.d("USB", "USB root: " + usbRoot);
                File usbDir = new File(usbRoot);
                JSObject item = new JSObject();
                item.put("path", usbRoot);
                item.put("name", usbDir.getName());
                result.put(item);
            }
        } catch (Exception ex) {
            Log.e("USB", "USB scan error", ex);
        }
        return result;
    }

    @PluginMethod
    public void listFiles(PluginCall call) {
        try {
            String path = call.getString("path");
            if (path == null) {
                call.reject("Path required");
                return;
            }
            File dir = new File(path);
            if (!dir.exists()) {
                call.reject("Directory not found");
                return;
            }
            if (!dir.isDirectory()) {
                call.reject("Path is not directory");
                return;
            }
            File[] files = dir.listFiles();
            JSArray result = new JSArray();
            if (files != null) {
                for (File file : files) {
                    // skip hidden
                    if (file.getName().startsWith(".")) {
                        continue;
                    }
                    JSObject item = new JSObject();
                    item.put("name", file.getName());
                    item.put("path", file.getAbsolutePath());
                    item.put("isDirectory", file.isDirectory());
                    item.put("size", file.length());
                    result.put(item);
                }
            }
            JSObject ret = new JSObject();
            ret.put("files", result);
            call.resolve(ret);
        } catch (Exception ex) {
            call.reject(ex.getMessage(), ex);
        }
    }

}