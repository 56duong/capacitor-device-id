import { WebPlugin } from '@capacitor/core';
export class DeviceIdWeb extends WebPlugin {
    async getDeviceId() {
        throw new Error('Method not implemented.');
    }
    async setKeyboardEnabled() {
        throw new Error('Not supported on web');
    }
    async showFloatingButton() {
        throw new Error('Not supported on web');
    }
    async openWifiSettings() {
        throw new Error('Not supported on web');
    }
    async openTeamViewer() {
        throw new Error('Not supported on web');
    }
    scanUsb() {
        throw new Error('Method not implemented.');
    }
    async listFiles() {
        throw new Error('Not supported on web');
    }
}
//# sourceMappingURL=web.js.map