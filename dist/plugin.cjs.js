'use strict';

var core = require('@capacitor/core');

const DeviceId = core.registerPlugin('DeviceId', {
    web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.DeviceIdWeb()),
});

class DeviceIdWeb extends core.WebPlugin {
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
    async readUsbFile() {
        throw new Error('Not supported on web');
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    DeviceIdWeb: DeviceIdWeb
});

exports.DeviceId = DeviceId;
//# sourceMappingURL=plugin.cjs.js.map
