import Foundation
import UIKit
import Network

@objc public class DeviceId: NSObject {
    @objc public func getDeviceId() -> [String: String] {
        return [
            "uniqueId": UIDevice.current.identifierForVendor?.uuidString ?? "",
            "manufacturer": "Apple",
            "model": UIDevice.current.model,
            "osVersion": UIDevice.current.systemVersion
        ]
    }

    internal func isPortOpen(ip: String, port: Int, timeoutMs: Int) -> Bool {
        let semaphore = DispatchSemaphore(value: 0)
        var isOpen    = false
    
        let host       = NWEndpoint.Host(ip)
        let nwPort     = NWEndpoint.Port(integerLiteral: NWEndpoint.Port.IntegerLiteralType(port))
        let connection = NWConnection(host: host, port: nwPort, using: .tcp)
    
        connection.stateUpdateHandler = { state in
            switch state {
            case .ready:
                // Connected successfully — port is open
                isOpen = true
                connection.cancel()
                semaphore.signal()
            case .failed, .cancelled:
                semaphore.signal()
            case .waiting:
                // No route / refused — treat as closed
                connection.cancel()
                semaphore.signal()
            default:
                break
            }
        }
    
        connection.start(queue: DispatchQueue.global())
    
        // Wait up to connectTimeoutMs
        let result = semaphore.wait(timeout: .now() + .milliseconds(timeoutMs))
        if result == .timedOut {
            connection.cancel()
        }
    
        return isOpen
    }
    
    // ─────────────────────────────────────────────────────────────────────────────
    // Helper: get subnet prefix from active WiFi interface
    // e.g. device IP 192.168.1.42  →  returns "192.168.1"
    //
    // Uses getifaddrs() — works without special permissions on iOS
    // ─────────────────────────────────────────────────────────────────────────────
    
    internal func getWifiSubnet() -> String? {
        var ifaddr: UnsafeMutablePointer<ifaddrs>?
        guard getifaddrs(&ifaddr) == 0, let firstAddr = ifaddr else { return nil }
        defer { freeifaddrs(ifaddr) }
    
        var pointer = firstAddr
        while true {
            let interface = pointer.pointee
    
            // Only IPv4
            let family = interface.ifa_addr.pointee.sa_family
            guard family == UInt8(AF_INET) else {
                guard let next = interface.ifa_next else { break }
                pointer = next
                continue
            }
    
            // Only WiFi interface (en0 on iPhone/iPad)
            let name = String(cString: interface.ifa_name)
            guard name == "en0" else {
                guard let next = interface.ifa_next else { break }
                pointer = next
                continue
            }
    
            // Extract IP string
            var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
            getnameinfo(
                interface.ifa_addr,
                socklen_t(interface.ifa_addr.pointee.sa_len),
                &hostname,
                socklen_t(hostname.count),
                nil, 0,
                NI_NUMERICHOST
            )
    
            let ipString = String(cString: hostname) // e.g. "192.168.1.42"
            let parts    = ipString.split(separator: ".")
            guard parts.count == 4 else {
                guard let next = interface.ifa_next else { break }
                pointer = next
                continue
            }
    
            return "\(parts[0]).\(parts[1]).\(parts[2])"
        }
    
        return nil
    }
}