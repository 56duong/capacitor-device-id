import { WebPlugin } from '@capacitor/core';

import type { DeviceIdPlugin, DeviceIdResult, ScanUsbResult } from './definitions';

export class DeviceIdWeb extends WebPlugin implements DeviceIdPlugin {

  async getDeviceId(): Promise<DeviceIdResult> {
    throw new Error('Method not implemented.');
  }

  async setKeyboardEnabled(): Promise<void> {
    throw new Error('Not supported on web');
  }

  async showFloatingButton(): Promise<void> {
    throw new Error('Not supported on web');
  }

  async openWifiSettings(): Promise<void> {
    throw new Error('Not supported on web');
  }

  async openTeamViewer(): Promise<void> {
    throw new Error('Not supported on web');
  }

  scanUsb(): Promise<ScanUsbResult> {
    throw new Error('Method not implemented.');
  }

  async listFiles(): Promise<any> {
    throw new Error('Not supported on web');
  }

  async readUsbFile(): Promise<any> {
    throw new Error('Not supported on web');
  }

}