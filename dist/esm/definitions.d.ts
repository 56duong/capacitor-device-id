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
    /** Unix timestamp (ms) when the scan completed */
    scannedAt: number;
}
