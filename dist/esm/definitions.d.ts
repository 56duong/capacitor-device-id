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
}
export interface DeviceIdResult {
    uniqueId: string;
    manufacturer: string;
    model: string;
    osVersion: string;
}
