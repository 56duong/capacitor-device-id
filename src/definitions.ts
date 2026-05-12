export interface DeviceIdPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
