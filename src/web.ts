import { WebPlugin } from '@capacitor/core';

import type { DeviceIdPlugin, DeviceIdResult, ScanUsbResult } from './definitions';

export class DeviceIdWeb extends WebPlugin implements DeviceIdPlugin {

  async getDeviceId(): Promise<DeviceIdResult> {
    throw new Error('getDeviceId is not supported on web');
  }

  async setKeyboardEnabled(): Promise<void> {
    throw new Error('setKeyboardEnabled is not supported on web');
  }

  async showFloatingButton(): Promise<void> {
    throw new Error('showFloatingButton is not supported on web');
  }

  async setKeyboardOverlayConfig(): Promise<void> {
    throw new Error('setKeyboardOverlayConfig is not supported on web');
  }

  async hideKeyboardOverlay(): Promise<void> {
    throw new Error('hideKeyboardOverlay is not supported on web');
  }

  async openWifiSettings(): Promise<void> {
    throw new Error('openWifiSettings is not supported on web');
  }

  async setBluetoothEnabled(): Promise<void> {
    throw new Error('setBluetoothEnabled is not supported on web');
  }

  async openTeamViewer(): Promise<void> {
    throw new Error('openTeamViewer is not supported on web');
  }

  scanUsb(): Promise<ScanUsbResult> {
    throw new Error('scanUsb is not supported on web');
  }

  async listFiles(): Promise<any> {
    throw new Error('listFiles is not supported on web');
  }

  async readUsbFile(): Promise<any> {
    throw new Error('readUsbFile is not supported on web');
  }

}