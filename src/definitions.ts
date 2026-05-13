export interface DeviceIdPlugin {
  /**
   * Get device ID and information
   * @returns Promise with device information
   * @example
   * import { DeviceId } from 'capacitor-device-id';
   *
   * DeviceId.getDeviceId().then((result) => {
   *   console.log(result);
   * }).catch((error) => {
   *   console.error('Error getting device ID:', error);
   * });
   */
  getDeviceId(): Promise<DeviceIdResult>;
}

export interface DeviceIdResult {
  uniqueId: string;
  manufacturer: string;
  model: string;
  osVersion: string;
}