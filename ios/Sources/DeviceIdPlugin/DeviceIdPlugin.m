#import <Capacitor/Capacitor.h>

CAP_PLUGIN(DeviceIdPlugin, "DeviceId",
    CAP_PLUGIN_METHOD(getDeviceId, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(scanNetworkPrinters, CAPPluginReturnPromise);
)