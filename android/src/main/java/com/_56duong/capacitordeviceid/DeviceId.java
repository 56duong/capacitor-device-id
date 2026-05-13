package com._56duong.capacitordeviceid;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.getcapacitor.JSObject;

public class DeviceId {
    private Context context;

    public DeviceId(Context context) {
        this.context = context;
    }

    public JSObject getDeviceId() {
        String id = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ANDROID_ID
        );

        JSObject ret = new JSObject();
        ret.put("uniqueId", id);
        ret.put("manufacturer", Build.MANUFACTURER);
        ret.put("model", Build.MODEL);
        ret.put("osVersion", Build.VERSION.RELEASE);

        return ret;
    }
}