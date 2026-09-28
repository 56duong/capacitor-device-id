export interface DeviceIdPlugin {
    /**
     * Get device ID and information
     * @returns Promise with device information
     * @example
     * import { DeviceId } from 'capacitor-device-id';
     *
     * DeviceId.getDeviceId().then((result) => {
     *   console.log(result); // {"uniqueId":"505e998085b8cc91","manufacturer":"samsung","model":"SM-G570Y","osVersion":"8.0.0"}
     * }).catch((error) => {
     *   console.error('Error getting device ID:', error);
     * });
     */
    getDeviceId(): Promise<DeviceIdResult>;
    /**
     * Show or hide the keyboard
     * @param options { enabled: boolean } - true to show the keyboard, false to hide it
     * @returns Promise that resolves when the operation is complete
     * @example
     * import { DeviceId } from 'capacitor-device-id';
     * DeviceId.setKeyboardEnabled({ enabled: true }).then(() => {
     *   console.log('Keyboard enabled');
     * }).catch((error) => {
     *   console.error('Error setting keyboard enabled:', error);
     * });
     */
    setKeyboardEnabled(options: {
        enabled: boolean;
    }): Promise<void>;
    /**
     * Show floating overlay back button
     */
    showFloatingButton(): Promise<void>;
    /**
     * Open Android Wi-Fi settings
     */
    openWifiSettings(): Promise<void>;
    /**
     * Toggle Bluetooth on/off
     *
     * @param options.enabled
     * - true: enable Bluetooth
     * - false: disable Bluetooth
     *
     * @example
     * await DeviceId.setBluetoothEnabled({
     *   enabled: true
     * });
     *
     * await DeviceId.setBluetoothEnabled({
     *   enabled: false
     * });
     */
    setBluetoothEnabled(options: {
        enabled: boolean;
    }): Promise<void>;
    /**
     * Open TeamViewer QuickSupport
     */
    openTeamViewer(): Promise<void>;
    /**
     * Scan connected USB storage devices
     */
    scanUsb(): Promise<ScanUsbResult>;
    /**
     * USB attached event
     */
    addListener(eventName: 'usbAttached', listenerFunc: (data: ScanUsbResult) => void): Promise<any>;
    /**
     * USB detached event
     */
    addListener(eventName: 'usbDetached', listenerFunc: () => void): Promise<any>;
    /**
     * List files/folders inside a directory
     *
     * @example
     * const result = await DeviceId.listFiles({
     *   path: '/storage/601B-309F'
     * });
     *
     * console.log(result.files);
     */
    listFiles(options: {
        path: string;
    }): Promise<ListFilesResult>;
    /**
     * Read a file from USB storage
     */
    readUsbFile(options: {
        path: string;
    }): Promise<ReadUsbFileResult>;
    /**
     * Scan the local WiFi subnet for ESC/POS printers listening on port 9100.
     * Scans all 254 IPs in parallel — typically completes in 2–5 seconds.
     *
     * @returns List of discovered printer IPs, the subnet scanned, and a timestamp.
     *
     * @example
     * const result = await DeviceId.scanNetworkPrinters();
     * console.log(result.printers);
     * // [{ ip: "192.168.1.45", port: 9100 }, { ip: "192.168.1.102", port: 9100 }]
     *
     * @example with timeout option
     * const result = await DeviceId.scanNetworkPrinters({ timeoutMs: 5000 });
     */
    scanNetworkPrinters(options?: ScanNetworkPrintersOptions): Promise<ScanNetworkPrintersResult>;
    /**
     * Shows Screen 2 on the secondary Android display.
     *
     * The native plugin finds the available secondary display and opens Screen 2 in a separate WebView.
     * Screen 2 loads the same app bundle as Screen 1, so the app must be able to tell which screen it is running on.
     *
     * Flow:
     *   Screen 1 -> Native Plugin -> Secondary Android Display -> Screen 2 WebView
     *
     * Screen 2 contract (names are fixed, the native plugin calls / injects them):
     *   - `window.SecondScreenBridge.isSecondScreen()`
     *       Injected by the native plugin into Screen 2's WebView ONLY, before the page loads.
     *       It does not exist on Screen 1. Use it to detect which screen the app is running on,
     *       e.g. to skip login, splash and other Screen 1 startup work on Screen 2.
     *   - `window.secondScreenReady()`
     *       Implement it on Screen 2. Native polls until it exists, then calls it once
     *       so the app can navigate to its second-screen route. Screen 2 stays hidden until then.
     *   - `window.updateSecondScreen(data)`
     *       Implement it on Screen 2. See `updateSecondScreen()`.
     *
     * Rejects if the device has no secondary presentation display.
     *
     * @example
     * const { displayId } = await DeviceId.showSecondScreen();
     */
    showSecondScreen(): Promise<ShowSecondScreenResult>;
    /**
     * Hides Screen 2 from the secondary Android display.
     *
     * The native plugin closes Screen 2 and destroys its WebView.
     *
     * Flow:
     *   Screen 1 -> Native Plugin -> Screen 2 WebView -> Closed
     *
     * @example
     * await DeviceId.hideSecondScreen();
     */
    hideSecondScreen(): Promise<void>;
    /**
     * Updates Screen 2.
     *
     * The native plugin sends the data to Screen 2, where it is received by `window.updateSecondScreen(data)`.
     *
     * Implement `window.updateSecondScreen(data)` on Screen 2
     * to receive data from Screen 1.
     *
     * Flow:
     *   Screen 1 -> Native Plugin -> Screen 2 WebView -> window.updateSecondScreen(data)
     *
     * Screen 1 and Screen 2 use separate WebViews, so data must
     * pass through the native plugin. The native plugin only acts
     * as a bridge.
     *
     * @example
     * await DeviceId.updateSecondScreen({ data: currentInvoice });
     */
    updateSecondScreen(options: {
        data: any;
    }): Promise<void>;
}
export interface DeviceIdResult {
    uniqueId: string;
    manufacturer: string;
    model: string;
    osVersion: string;
}
export interface UsbFile {
    name: string;
    path: string;
    isDirectory: boolean;
    size: number;
}
export interface UsbDevice {
    path: string;
    name: string;
}
export interface ScanUsbResult {
    devices: UsbDevice[];
}
export interface ListFilesResult {
    files: UsbFile[];
}
export interface ReadUsbFileResult {
    data: string;
    name: string;
    path: string;
}
export interface ScanNetworkPrintersOptions {
    /**
     * Max milliseconds to wait for the full scan to complete.
     * Default: 10000 (10 seconds)
     */
    timeoutMs?: number;
    /**
     * TCP connect timeout per IP in milliseconds.
     * Lower = faster scan, but may miss slow routers.
     * Default: 300
     */
    connectTimeoutMs?: number;
    /**
     * Port to probe. Default: 9100 (RAW/JetDirect — standard for ESC/POS printers).
     * Change to 515 for LPD or 631 for IPP if needed.
     */
    port?: number;
}
export interface PrinterDevice {
    /** IPv4 address of the discovered printer */
    ip: string;
    /** Port that responded (matches the probed port, default 9100) */
    port: number;
}
export interface ScanNetworkPrintersResult {
    /** Printers found on the network */
    printers: PrinterDevice[];
    /** Subnet that was scanned, e.g. "192.168.1" */
    subnet: string;
}
export interface ShowSecondScreenResult {
    displayId: number;
}
