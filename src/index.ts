import { registerPlugin } from '@capacitor/core';

import type { DeviceIdPlugin } from './definitions';

const DeviceId = registerPlugin<DeviceIdPlugin>('DeviceId', {
  web: () => import('./web').then((m) => new m.DeviceIdWeb()),
});

export * from './definitions';
export { DeviceId };
