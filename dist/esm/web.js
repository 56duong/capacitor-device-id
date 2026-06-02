import { WebPlugin } from '@capacitor/core';
export class DeviceIdWeb extends WebPlugin {
    async getDeviceId() {
        throw new Error('getDeviceId is not supported on web');
    }
    async setKeyboardEnabled() {
        throw new Error('setKeyboardEnabled is not supported on web');
    }
    async showFloatingButton() {
        throw new Error('showFloatingButton is not supported on web');
    }
    async openWifiSettings() {
        throw new Error('openWifiSettings is not supported on web');
    }
    async setBluetoothEnabled() {
        throw new Error('setBluetoothEnabled is not supported on web');
    }
    async openTeamViewer() {
        throw new Error('openTeamViewer is not supported on web');
    }
    scanUsb() {
        throw new Error('scanUsb is not supported on web');
    }
    async listFiles() {
        throw new Error('listFiles is not supported on web');
    }
    async readUsbFile() {
        throw new Error('readUsbFile is not supported on web');
    }
}
//# sourceMappingURL=web.js.map