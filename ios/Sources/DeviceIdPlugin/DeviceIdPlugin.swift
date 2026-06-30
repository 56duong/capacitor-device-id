import Foundation
import Capacitor

@objc(DeviceIdPlugin)
public class DeviceIdPlugin: CAPPlugin {
    private let implementation = DeviceId()
    
    @objc func getDeviceId(_ call: CAPPluginCall) {
        let deviceInfo = implementation.getDeviceId()
        call.resolve(deviceInfo)
    }

    @objc func scanNetworkPrinters(_ call: CAPPluginCall) {
        let timeoutMs        = call.getInt("timeoutMs", 10000)
        let connectTimeoutMs = call.getInt("connectTimeoutMs", 300)
        let port             = call.getInt("port", 9100)
    
        // Must run off main thread
        DispatchQueue.global(qos: .userInitiated).async {
            // 1. Get current device subnet
            guard let subnet = self.implementation.getWifiSubnet() else {
                call.reject("Could not determine subnet. Is WiFi connected?")
                return
            }
    
            // 2. Scan all 254 IPs concurrently
            let group       = DispatchGroup()
            let queue       = DispatchQueue(label: "printer.scan", attributes: .concurrent)
            // iOS handles ~20 concurrent NWConnections well; more gets throttled
            let semaphore   = DispatchSemaphore(value: 20)
            var found       = [[String: Any]]()
            let lock        = NSLock()
    
            let deadline    = DispatchTime.now() + .milliseconds(timeoutMs)
    
            for i in 1...254 {
                let ip = "\(subnet).\(i)"
                group.enter()
                semaphore.wait()
    
                queue.async {
                    defer {
                        semaphore.signal()
                        group.leave()
                    }
    
                    // Check if we've already exceeded the total timeout
                    if DispatchTime.now() > deadline { return }
    
                    if self.implementation.isPortOpen(ip: ip, port: port, timeoutMs: connectTimeoutMs) {
                        lock.lock()
                        found.append(["ip": ip, "port": port])
                        lock.unlock()
                    }
                }
            }
    
            // Wait for all, but respect total timeout
            _ = group.wait(timeout: deadline)
    
            // 3. Return result
            call.resolve([
                "printers":  found,
                "subnet":    subnet
            ])
        }
    }
}