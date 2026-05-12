import { WebPlugin } from '@capacitor/core';

import type { DeviceIdPlugin } from './definitions';

export class DeviceIdWeb extends WebPlugin implements DeviceIdPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
