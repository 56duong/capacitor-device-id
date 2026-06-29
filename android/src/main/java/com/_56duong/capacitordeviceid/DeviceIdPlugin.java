package com._56duong.capacitordeviceid;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

@CapacitorPlugin(name = "DeviceId")
public class DeviceIdPlugin extends Plugin {

    @PluginMethod
    public void getDeviceId(PluginCall call) {
        JSObject deviceInfo = new DeviceId(getContext()).getDeviceId();
        call.resolve(deviceInfo);
    }

    @PluginMethod
    public void setKeyboardEnabled(PluginCall call) {
        boolean enabled = Boolean.TRUE.equals(call.getBoolean("enabled", true));
        Activity activity = getActivity();
        activity.runOnUiThread(() -> {
            if (enabled) {
                // allow keyboard
                activity.getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                );
            } else {
                // disable keyboard
                activity.getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                );
            }
        });
        call.resolve();
    }


    @PluginMethod
    public void showFloatingButton(PluginCall call) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(getContext())) {

                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getContext().getPackageName())
                );

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getContext().startActivity(intent);

                call.reject("Overlay permission required");

                return;
            }
        }

        Intent serviceIntent =
                new Intent(getContext(), FloatingService.class);

        getContext().startService(serviceIntent);

        call.resolve();
    }


    @PluginMethod
    public void openWifiSettings(PluginCall call) {

        Intent wifiIntent =
                new Intent(Settings.ACTION_WIFI_SETTINGS);

        wifiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        getContext().startActivity(wifiIntent);

        call.resolve();
    }



    @PluginMethod
    public void setBluetoothEnabled(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                call.reject("BLUETOOTH_CONNECT permission not granted");
                return;
            }
        }
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null) {
                call.reject("Bluetooth not supported");
                return;
            }
            boolean enabled = Boolean.TRUE.equals(call.getBoolean("enabled", true));
            if (enabled) {
                adapter.enable();
            } else {
                adapter.disable();
            }
            call.resolve();
        } catch (SecurityException e) {
            call.reject("Bluetooth permission denied", e);
        } catch (Exception e) {
            call.reject(e.toString(), e);
        }
    }


    @PluginMethod
    public void openTeamViewer(PluginCall call) {

        Intent intent = new Intent();

        intent.setClassName(
                "com.teamviewer.quicksupport.market",
                "com.teamviewer.quicksupport.ui.QSActivity"
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        );

        try {

            getContext().startActivity(intent);

            call.resolve();

        } catch (Exception e) {

            call.reject(e.toString());
        }
    }



    private BroadcastReceiver usbReceiver;

    @Override
    public void load() {
        super.load();
        registerUsbReceiver();
    }

    @Override
    protected void handleOnDestroy() {
        super.handleOnDestroy();
        if (usbReceiver != null) {
            try {
                getContext().unregisterReceiver(usbReceiver);
            } catch (Exception ignored) {
            }
        }
    }

    private void registerUsbReceiver() {
        usbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                    Log.d("USB", "USB device attached");
                    // Wait for storage mount
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        JSArray devices = getUsbStorageList();
                        JSObject ret = new JSObject();
                        ret.put("devices", devices);
                        notifyListeners("usbAttached", ret);
                    }, 2000);
                } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    Log.d("USB", "USB device detached");
                    JSObject ret = new JSObject();
                    notifyListeners("usbDetached", ret);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        getContext().registerReceiver(usbReceiver, filter);
    }

    @PluginMethod
    public void scanUsb(PluginCall call) {
        JSArray result = getUsbStorageList();
        JSObject ret = new JSObject();
        ret.put("devices", result);
        call.resolve(ret);
    }

    private JSArray getUsbStorageList() {
        JSArray result = new JSArray();
        try {
            File[] dirs = getContext().getExternalFilesDirs(null);
            if (dirs == null) {
                return result;
            }
            for (File dir : dirs) {
                if (dir == null) continue;
                String fullPath = dir.getAbsolutePath();
                Log.d("USB", "External dir: " + fullPath);
                // Skip internal storage
                if (fullPath.contains("emulated")) {
                    continue;
                }
                // Usually:
                // /storage/601B-309F/Android/data/your.package/files
                String usbRoot = fullPath;
                int androidIndex = fullPath.indexOf("/Android");
                if (androidIndex > 0) {
                    usbRoot = fullPath.substring(0, androidIndex);
                }
                Log.d("USB", "USB root: " + usbRoot);
                File usbDir = new File(usbRoot);
                JSObject item = new JSObject();
                item.put("path", usbRoot);
                item.put("name", usbDir.getName());
                result.put(item);
            }
        } catch (Exception ex) {
            Log.e("USB", "USB scan error", ex);
        }
        return result;
    }

    @PluginMethod
    public void listFiles(PluginCall call) {
        try {
            String path = call.getString("path");
            if (path == null) {
                call.reject("Path required");
                return;
            }
            File dir = new File(path);
            if (!dir.exists()) {
                call.reject("Directory not found");
                return;
            }
            if (!dir.isDirectory()) {
                call.reject("Path is not directory");
                return;
            }
            File[] files = dir.listFiles();
            JSArray result = new JSArray();
            if (files != null) {
                for (File file : files) {
                    // skip hidden
                    if (file.getName().startsWith(".")) {
                        continue;
                    }
                    JSObject item = new JSObject();
                    item.put("name", file.getName());
                    item.put("path", file.getAbsolutePath());
                    item.put("isDirectory", file.isDirectory());
                    item.put("size", file.length());
                    result.put(item);
                }
            }
            JSObject ret = new JSObject();
            ret.put("files", result);
            call.resolve(ret);
        } catch (Exception ex) {
            call.reject(ex.getMessage(), ex);
        }
    }

    @PluginMethod
    public void readUsbFile(PluginCall call) {
        try {
            String path = call.getString("path");
            if (path == null || path.isEmpty()) {
                call.reject("Path is required");
                return;
            }

            File file = new File(path);

            if (!file.exists()) {
                call.reject("File does not exist");
                return;
            }

            if (!file.isFile()) {
                call.reject("Path is not a file");
                return;
            }

            byte[] bytes = new byte[(int) file.length()];

            try (FileInputStream fis = new FileInputStream(file)) {
                int offset = 0;
                int read;

                while (offset < bytes.length
                        && (read = fis.read(bytes, offset, bytes.length - offset)) != -1) {
                    offset += read;
                }

                if (offset != bytes.length) {
                    call.reject("Failed to read entire file");
                    return;
                }
            }

            String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);

            JSObject ret = new JSObject();
            ret.put("data", base64);
            ret.put("name", file.getName());
            ret.put("path", file.getAbsolutePath());
            ret.put("size", file.length());

            call.resolve(ret);

        } catch (Exception ex) {
            call.reject("Failed to read file: " + ex.getMessage(), ex);
        }
    }



    @PluginMethod
    public void scanNetworkPrinters(PluginCall call) {
        int timeoutMs        = call.getInt("timeoutMs", 10000);       // full scan timeout
        int connectTimeoutMs = call.getInt("connectTimeoutMs", 300);  // per-IP connect timeout
        int port             = call.getInt("port", 9100);             // port to probe

        // Get current subnet
        String subnet = getSubnet(getContext());
        if (subnet == null) {
            call.reject("Could not determine subnet. Is WiFi connected?");
            return;
        }

        // Scan all 254 IPs in parallel
        // 50 threads: sweet spot — scans 254 IPs in ~2-3s without overwhelming the router
        ExecutorService executor = Executors.newFixedThreadPool(50);
        List<JSObject> found = Collections.synchronizedList(new ArrayList<>());

        for (int i = 1; i <= 254; i++) {
            final String ip = subnet + "." + i;
            executor.execute(() -> {
                if (isPortOpen(ip, port, connectTimeoutMs)) {
                    JSObject printer = new JSObject();
                    printer.put("ip", ip);
                    printer.put("port", port);
                    found.add(printer);
                }
            });
        }

        executor.shutdown();
        try {
            boolean finished = executor.awaitTermination(timeoutMs, TimeUnit.MILLISECONDS);
            if (!finished) {
                // Timed out — return whatever was found so far, don't reject
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            call.reject("Scan was interrupted");
            return;
        }

        // Build result
        JSArray printerArray = new JSArray();
        for (JSObject printer : found) {
            printerArray.put(printer);
        }

        JSObject result = new JSObject();
        result.put("printers", printerArray);
        result.put("subnet", subnet);

        call.resolve(result);
    }



    /**
     * Check if a TCP port is open on the given IP
     * @param ip
     * @param port
     * @param timeoutMs
     * @return
     */
    private boolean isPortOpen(String ip, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    /**
     * Get subnet prefix from current WiFi connection
     * e.g. device IP 192.168.1.42 -> returns "192.168.1"
     * @param context
     * @return
     */
    private String getSubnet(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);

            if (wifiManager == null) return null;

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo == null) return null;

            int ipInt = wifiInfo.getIpAddress();
            if (ipInt == 0) return null; // not connected

            // Android stores WiFi IP as little-endian int — unpack manually
            String ipAddress = String.format(
                    Locale.US,
                    "%d.%d.%d.%d",
                    (ipInt & 0xff),
                    (ipInt >> 8)  & 0xff,
                    (ipInt >> 16) & 0xff,
                    (ipInt >> 24) & 0xff
            );

            String[] parts = ipAddress.split("\\.");
            if (parts.length < 3) return null;

            return parts[0] + "." + parts[1] + "." + parts[2];

        } catch (Exception e) {
            return null;
        }
    }

}