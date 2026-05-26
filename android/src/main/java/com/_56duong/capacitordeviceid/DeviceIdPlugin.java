package com._56duong.capacitordeviceid;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

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

}