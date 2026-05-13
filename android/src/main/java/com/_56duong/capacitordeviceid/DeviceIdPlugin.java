package com._56duong.capacitordeviceid;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "DeviceId")
public class DeviceIdPlugin extends Plugin {

    @PluginMethod
    public void getDeviceId(PluginCall call) {
        JSObject deviceInfo = new DeviceId(getContext()).getDeviceId();
        call.resolve(deviceInfo);
    }
}
