import { WebPlugin } from '@capacitor/core';
import type { DeviceIdPlugin, DeviceIdResult } from './definitions';
export declare class DeviceIdWeb extends WebPlugin implements DeviceIdPlugin {
    getDeviceId(): Promise<DeviceIdResult>;
    setKeyboardEnabled(): Promise<void>;
    showFloatingButton(): Promise<void>;
    openWifiSettings(): Promise<void>;
    openTeamViewer(): Promise<void>;
}
