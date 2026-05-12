# capacitor-device-id

A Capacitor plugin to retrieve device id information.

## Install

```bash
npm install capacitor-device-id
npx cap sync
```

## API

<docgen-index>

* [`getDeviceId()`](#getdeviceid)
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


### Interfaces


#### DeviceIdResult

| Prop               | Type                |
| ------------------ | ------------------- |
| **`uniqueId`**     | <code>string</code> |
| **`manufacturer`** | <code>string</code> |
| **`model`**        | <code>string</code> |
| **`osVersion`**    | <code>string</code> |

</docgen-api>
