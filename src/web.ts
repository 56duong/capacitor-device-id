import { WebPlugin } from '@capacitor/core';

import type { DeviceIdPlugin, DeviceIdResult } from './definitions';

export class DeviceIdWeb extends WebPlugin implements DeviceIdPlugin {
  async getDeviceId(): Promise<DeviceIdResult> {
    throw new Error('Method not implemented.');
  }
  async setKeyboardEnabled(): Promise<void> {
    throw new Error('Not supported on web');
  }
}