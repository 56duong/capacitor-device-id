import Foundation
import UIKit

@objc public class DeviceId: NSObject {
    @objc public func getDeviceId() -> [String: String] {
        return [
            "uniqueId": UIDevice.current.identifierForVendor?.uuidString ?? "",
            "manufacturer": "Apple",
            "model": UIDevice.current.model,
            "osVersion": UIDevice.current.systemVersion
        ]
    }
}