import { WebPlugin } from '@capacitor/core';
import type { DeviceIdPlugin, DeviceIdResult, ScanUsbResult } from './definitions';
export declare class DeviceIdWeb extends WebPlugin implements DeviceIdPlugin {
    getDeviceId(): Promise<DeviceIdResult>;
    setKeyboardEnabled(): Promise<void>;
    showFloatingButton(): Promise<void>;
    openWifiSettings(): Promise<void>;
    setBluetoothEnabled(): Promise<void>;
    openTeamViewer(): Promise<void>;
    scanUsb(): Promise<ScanUsbResult>;
    listFiles(): Promise<any>;
    readUsbFile(): Promise<any>;
    scanNetworkPrinters(): Promise<any>;
}
