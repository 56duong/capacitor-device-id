var capacitorDeviceId = (function (exports, core) {
    'use strict';

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
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        DeviceIdWeb: DeviceIdWeb
    });

    exports.DeviceId = DeviceId;

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
