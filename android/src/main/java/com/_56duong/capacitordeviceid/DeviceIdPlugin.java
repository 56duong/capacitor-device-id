package com._56duong.capacitordeviceid;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "DeviceId")
public class DeviceIdPlugin extends Plugin {

    @PluginMethod
    public void getAndroidId(PluginCall call) {
        String androidId = new DeviceId(getContext()).getAndroidId();
        
        JSObject ret = new JSObject();
        ret.put("id", androidId);
        call.resolve(ret);
    }
}
