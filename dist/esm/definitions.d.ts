export interface DeviceIdPlugin {
    /**
     * Get device ID and information
     * @returns Promise with device information
     */
    getDeviceId(): Promise<DeviceIdResult>;
}
export interface DeviceIdResult {
    uniqueId: string;
    manufacturer: string;
    model: string;
    osVersion: string;
}
