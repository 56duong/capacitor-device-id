package com._56duong.capacitordeviceid;

import android.content.Context;
import android.provider.Settings;

import com.getcapacitor.Logger;

public class DeviceId {
    private Context context;

    public DeviceId(Context context) {
        this.context = context;
    }

    public String getAndroidId() {
        return Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ANDROID_ID
        );
    }
}
