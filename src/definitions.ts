export interface DeviceIdPlugin {
  /**
   * Get device ID and information
   * @returns Promise with device information
   * @example
   * import { DeviceId } from 'capacitor-device-id';
   *
   * DeviceId.getDeviceId().then((result) => {
   *   console.log(result); // {"uniqueId":"505e998085b8cc91","manufacturer":"samsung","model":"SM-G570Y","osVersion":"8.0.0"}
   * }).catch((error) => {
   *   console.error('Error getting device ID:', error);
   * });
   */
  getDeviceId(): Promise<DeviceIdResult>;

  /**
   * Show or hide the keyboard
   * @param options { enabled: boolean } - true to show the keyboard, false to hide it
   * @returns Promise that resolves when the operation is complete
   * @example
   * import { DeviceId } from 'capacitor-device-id';
   * DeviceId.setKeyboardEnabled({ enabled: true }).then(() => {
   *   console.log('Keyboard enabled');
   * }).catch((error) => {
   *   console.error('Error setting keyboard enabled:', error);
   * });
   */
  setKeyboardEnabled(options: {
    enabled: boolean;
  }): Promise<void>;

  /**
   * Show floating overlay back button
   */
  showFloatingButton(): Promise<void>;

  /**
   * Open Android Wi-Fi settings
   */
  openWifiSettings(): Promise<void>;

  /**
   * Open TeamViewer QuickSupport
   */
  openTeamViewer(): Promise<void>;

  /**
   * Scan connected USB storage devices
   */
  scanUsb(): Promise<ScanUsbResult>;

  /**
   * USB attached event
   */
  addListener(
    eventName: 'usbAttached',
    listenerFunc: (data: ScanUsbResult) => void,
  ): Promise<any>;

  /**
   * USB detached event
   */
  addListener(
    eventName: 'usbDetached',
    listenerFunc: () => void,
  ): Promise<any>;

  /**
   * List files/folders inside a directory
   *
   * @example
   * const result = await DeviceId.listFiles({
   *   path: '/storage/601B-309F'
   * });
   *
   * console.log(result.files);
   */
  listFiles(options: {
    path: string;
  }): Promise<ListFilesResult>;

  /**
   * Read a file from USB storage
   */
  readUsbFile(options: {
    path: string;
  }): Promise<ReadUsbFileResult>;
}

export interface DeviceIdResult {
  uniqueId: string;
  manufacturer: string;
  model: string;
  osVersion: string;
}

export interface UsbFile {
  name: string;
  path: string;
  isDirectory: boolean;
  size: number;
}

export interface UsbDevice {
  path: string;
  name: string;
}

export interface ScanUsbResult {
  devices: UsbDevice[];
}

export interface ListFilesResult {
  files: UsbFile[];
}

export interface ReadUsbFileResult {
  data: string;
  name: string;
  path: string;
}