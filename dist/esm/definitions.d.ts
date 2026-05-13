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
}
export interface DeviceIdResult {
    uniqueId: string;
    manufacturer: string;
    model: string;
    osVersion: string;
}
