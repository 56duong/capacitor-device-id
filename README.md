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
* [`openTeamViewer()`](#openteamviewer)
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


### openTeamViewer()

```typescript
openTeamViewer() => Promise<void>
```

Open TeamViewer QuickSupport

--------------------


### Interfaces


#### DeviceIdResult

| Prop               | Type                |
| ------------------ | ------------------- |
| **`uniqueId`**     | <code>string</code> |
| **`manufacturer`** | <code>string</code> |
| **`model`**        | <code>string</code> |
| **`osVersion`**    | <code>string</code> |

</docgen-api>
