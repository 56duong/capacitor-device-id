'use strict';

var core = require('@capacitor/core');

const DeviceId = core.registerPlugin('DeviceId', {
    web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.DeviceIdWeb()),
});

class DeviceIdWeb extends core.WebPlugin {
    async getDeviceId() {
        throw new Error('Method not implemented.');
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    DeviceIdWeb: DeviceIdWeb
});

exports.DeviceId = DeviceId;
//# sourceMappingURL=plugin.cjs.js.map
