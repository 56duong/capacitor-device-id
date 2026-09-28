import { WebPlugin } from '@capacitor/core';
import type { DeviceIdPlugin, DeviceIdResult, ScanUsbResult, ShowSecondScreenResult } from './definitions';
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
    showSecondScreen(): Promise<ShowSecondScreenResult>;
    hideSecondScreen(): Promise<void>;
    updateSecondScreen(_options: {
        data: any;
    }): Promise<void>;
}
