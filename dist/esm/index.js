import { registerPlugin } from '@capacitor/core';
const DeviceId = registerPlugin('DeviceId', {
    web: () => import('./web').then((m) => new m.DeviceIdWeb()),
});
export * from './definitions';
export { DeviceId };
//# sourceMappingURL=index.js.map