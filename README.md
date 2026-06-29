# capacitor-device-id

A Capacitor plugin to retrieve device id information.

## Install

### Latest Version

```bash
npm install https://github.com/56duong/capacitor-device-id
npx cap sync
```

### Specific Version (v0.0.1)

```bash
npm install https://github.com/56duong/capacitor-device-id#v0.0.1
npx cap sync
```

## Example Usage

```typescript
import { DeviceId, DeviceIdResult } from 'capacitor-device-id';

async function getDeviceInfo() {
  try {
    const result = await DeviceId.getDeviceId();
    console.log(result); // {"uniqueId":"505e998085b8cc91","manufacturer":"samsung","model":"SM-G570Y","osVersion":"8.0.0"}
  } catch (err) {
    console.error('Error getting device info:', err);
  }
}
```

## API

<docgen-index>

* [`getDeviceId()`](#getdeviceid)
* [`setKeyboardEnabled(...)`](#setkeyboardenabled)
* [`showFloatingButton()`](#showfloatingbutton)
* [`openWifiSettings()`](#openwifisettings)
* [`setBluetoothEnabled(...)`](#setbluetoothenabled)
* [`openTeamViewer()`](#openteamviewer)
* [`scanUsb()`](#scanusb)
* [`addListener('usbAttached', ...)`](#addlistenerusbattached-)
* [`addListener('usbDetached', ...)`](#addlistenerusbdetached-)
* [`listFiles(...)`](#listfiles)
* [`readUsbFile(...)`](#readusbfile)
* [`scanNetworkPrinters(...)`](#scannetworkprinters)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getDeviceId()

```typescript
getDeviceId() => Promise<DeviceIdResult>
```

Get device ID and information

**Returns:** <code>Promise&lt;<a href="#deviceidresult">DeviceIdResult</a>&gt;</code>

--------------------


### setKeyboardEnabled(...)

```typescript
setKeyboardEnabled(options: { enabled: boolean; }) => Promise<void>
```

Show or hide the keyboard

| Param         | Type                               | Description                                               |
| ------------- | ---------------------------------- | --------------------------------------------------------- |
| **`options`** | <code>{ enabled: boolean; }</code> | : boolean } - true to show the keyboard, false to hide it |

--------------------


### showFloatingButton()

```typescript
showFloatingButton() => Promise<void>
```

Show floating overlay back button

--------------------


### openWifiSettings()

```typescript
openWifiSettings() => Promise<void>
```

Open Android Wi-Fi settings

--------------------


### setBluetoothEnabled(...)

```typescript
setBluetoothEnabled(options: { enabled: boolean; }) => Promise<void>
```

Toggle Bluetooth on/off

| Param         | Type                               |
| ------------- | ---------------------------------- |
| **`options`** | <code>{ enabled: boolean; }</code> |

--------------------


### openTeamViewer()

```typescript
openTeamViewer() => Promise<void>
```

Open TeamViewer QuickSupport

--------------------


### scanUsb()

```typescript
scanUsb() => Promise<ScanUsbResult>
```

Scan connected USB storage devices

**Returns:** <code>Promise&lt;<a href="#scanusbresult">ScanUsbResult</a>&gt;</code>

--------------------


### addListener('usbAttached', ...)

```typescript
addListener(eventName: 'usbAttached', listenerFunc: (data: ScanUsbResult) => void) => Promise<any>
```

USB attached event

| Param              | Type                                                                       |
| ------------------ | -------------------------------------------------------------------------- |
| **`eventName`**    | <code>'usbAttached'</code>                                                 |
| **`listenerFunc`** | <code>(data: <a href="#scanusbresult">ScanUsbResult</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------


### addListener('usbDetached', ...)

```typescript
addListener(eventName: 'usbDetached', listenerFunc: () => void) => Promise<any>
```

USB detached event

| Param              | Type                       |
| ------------------ | -------------------------- |
| **`eventName`**    | <code>'usbDetached'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code> |

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------


### listFiles(...)

```typescript
listFiles(options: { path: string; }) => Promise<ListFilesResult>
```

List files/folders inside a directory

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ path: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#listfilesresult">ListFilesResult</a>&gt;</code>

--------------------


### readUsbFile(...)

```typescript
readUsbFile(options: { path: string; }) => Promise<ReadUsbFileResult>
```

Read a file from USB storage

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ path: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#readusbfileresult">ReadUsbFileResult</a>&gt;</code>

--------------------


### scanNetworkPrinters(...)

```typescript
scanNetworkPrinters(options?: ScanNetworkPrintersOptions | undefined) => Promise<ScanNetworkPrintersResult>
```

Scan the local WiFi subnet for ESC/POS printers listening on port 9100.
Scans all 254 IPs in parallel — typically completes in 2–5 seconds.

| Param         | Type                                                                              |
| ------------- | --------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#scannetworkprintersoptions">ScanNetworkPrintersOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#scannetworkprintersresult">ScanNetworkPrintersResult</a>&gt;</code>

--------------------


### Interfaces


#### DeviceIdResult

| Prop               | Type                |
| ------------------ | ------------------- |
| **`uniqueId`**     | <code>string</code> |
| **`manufacturer`** | <code>string</code> |
| **`model`**        | <code>string</code> |
| **`osVersion`**    | <code>string</code> |


#### ScanUsbResult

| Prop          | Type                     |
| ------------- | ------------------------ |
| **`devices`** | <code>UsbDevice[]</code> |


#### UsbDevice

| Prop       | Type                |
| ---------- | ------------------- |
| **`path`** | <code>string</code> |
| **`name`** | <code>string</code> |


#### ListFilesResult

| Prop        | Type                   |
| ----------- | ---------------------- |
| **`files`** | <code>UsbFile[]</code> |


#### UsbFile

| Prop              | Type                 |
| ----------------- | -------------------- |
| **`name`**        | <code>string</code>  |
| **`path`**        | <code>string</code>  |
| **`isDirectory`** | <code>boolean</code> |
| **`size`**        | <code>number</code>  |


#### ReadUsbFileResult

| Prop       | Type                |
| ---------- | ------------------- |
| **`data`** | <code>string</code> |
| **`name`** | <code>string</code> |
| **`path`** | <code>string</code> |


#### ScanNetworkPrintersResult

| Prop           | Type                         | Description                               |
| -------------- | ---------------------------- | ----------------------------------------- |
| **`printers`** | <code>PrinterDevice[]</code> | Printers found on the network             |
| **`subnet`**   | <code>string</code>          | Subnet that was scanned, e.g. "192.168.1" |


#### PrinterDevice

| Prop       | Type                | Description                                                 |
| ---------- | ------------------- | ----------------------------------------------------------- |
| **`ip`**   | <code>string</code> | IPv4 address of the discovered printer                      |
| **`port`** | <code>number</code> | Port that responded (matches the probed port, default 9100) |


#### ScanNetworkPrintersOptions

| Prop                   | Type                | Description                                                                                                                   |
| ---------------------- | ------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| **`timeoutMs`**        | <code>number</code> | Max milliseconds to wait for the full scan to complete. Default: 10000 (10 seconds)                                           |
| **`connectTimeoutMs`** | <code>number</code> | TCP connect timeout per IP in milliseconds. Lower = faster scan, but may miss slow routers. Default: 300                      |
| **`port`**             | <code>number</code> | Port to probe. Default: 9100 (RAW/JetDirect — standard for ESC/POS printers). Change to 515 for LPD or 631 for IPP if needed. |

</docgen-api>
