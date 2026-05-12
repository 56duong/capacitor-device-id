// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorDeviceId",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorDeviceId",
            targets: ["DeviceIdPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "DeviceIdPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/DeviceIdPlugin"),
        .testTarget(
            name: "DeviceIdPluginTests",
            dependencies: ["DeviceIdPlugin"],
            path: "ios/Tests/DeviceIdPluginTests")
    ]
)