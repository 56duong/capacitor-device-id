import { WebPlugin } from '@capacitor/core';
export class DeviceIdWeb extends WebPlugin {
    async getDeviceId() {
        throw new Error('Method not implemented.');
    }
    async setKeyboardEnabled() {
        throw new Error('Not supported on web');
    }
}
//# sourceMappingURL=web.js.map