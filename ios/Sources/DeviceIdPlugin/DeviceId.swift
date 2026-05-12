import Foundation

@objc public class DeviceId: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
