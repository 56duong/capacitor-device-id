var capacitorDeviceId = (function (exports, core) {
    'use strict';

    const DeviceId = core.registerPlugin('DeviceId', {
        web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.DeviceIdWeb()),
    });

    class DeviceIdWeb extends core.WebPlugin {
        async getDeviceId() {
            throw new Error('getDeviceId is not supported on web');
        }
        async setKeyboardEnabled() {
            throw new Error('setKeyboardEnabled is not supported on web');
        }
        async showFloatingButton() {
            throw new Error('showFloatingButton is not supported on web');
        }
        async setKeyboardOverlayConfig() {
            throw new Error('setKeyboardOverlayConfig is not supported on web');
        }
        async hideKeyboardOverlay() {
            throw new Error('hideKeyboardOverlay is not supported on web');
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

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        DeviceIdWeb: DeviceIdWeb
    });

    exports.DeviceId = DeviceId;

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
