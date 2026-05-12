import Foundation
import Capacitor

@objc(DeviceIdPlugin)
public class DeviceIdPlugin: CAPPlugin {
    private let implementation = DeviceId()
    
    @objc func getDeviceId(_ call: CAPPluginCall) {
        let deviceInfo = implementation.getDeviceId()
        call.resolve(deviceInfo)
    }
}