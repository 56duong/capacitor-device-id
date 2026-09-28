package com._56duong.capacitordeviceid;

import android.Manifest;
import android.app.Presentation;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import com.getcapacitor.Bridge;
import com.getcapacitor.BridgeWebViewClient;
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
import android.view.Display;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

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
        if (secondScreenPresentation != null) {
            secondScreenPresentation.dismiss();
            secondScreenPresentation = null;
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

// MARK: Second Screen
//
// Flow:
//   1. showSecondScreen()
//      -> Native finds the secondary Android display.
//      -> Opens a Presentation with a separate WebView.
//      -> Loads the same Capacitor app URL.
//      -> Waits for `window.secondScreenReady()` before showing Screen 2.
//
//   2. updateSecondScreen({ data })
//      -> Called from Screen 1.
//      -> Native receives the data and forwards it to Screen 2.
//      -> Screen 2 receives it through `window.updateSecondScreen(data)`.
//
//      Screen 1 -> Native Plugin -> Screen 2 WebView
//               -> window.updateSecondScreen(data)
//
//      Screen 1 and Screen 2 use separate WebViews, so data cannot be
//      sent directly between them. Native acts as the bridge.
//
//   3. hideSecondScreen()
//      -> Closes the Presentation and destroys the Screen 2 WebView.

    private Presentation secondScreenPresentation;

    @PluginMethod
    public void showSecondScreen(PluginCall call) {
        Activity activity = getActivity();

        activity.runOnUiThread(() -> {
            try {
                DisplayManager displayManager = (DisplayManager) activity.getSystemService(Context.DISPLAY_SERVICE);

                // DISPLAY_CATEGORY_PRESENTATION filters to displays meant for
                // secondary-screen use (HDMI out, USB-C display, etc), not just
                // any connected display.
                Display[] displays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

                Log.d("SecondScreen", "Presentation displays: " + displays.length);

                if (displays.length == 0) {
                    call.reject("No secondary presentation display found");
                    return;
                }

                Display secondDisplay = displays[0];

//                // Diagnostic for verifying Sunmi/other hardware exposes the customer
//                // display the same way the emulator's virtual display does. Flags to
//                // look for: FLAG_PRESENTATION should be set (that's why it showed up
//                // in DISPLAY_CATEGORY_PRESENTATION at all); FLAG_SECURE and
//                // FLAG_SUPPORTS_PROTECTED_BUFFERS are what some vendor docs check for
//                // as well. getState() confirms it's actually ON, not just present.
//                Log.d("SecondScreen", "Display flags: " + secondDisplay.getFlags()
//                        + " (FLAG_PRESENTATION=" + ((secondDisplay.getFlags() & Display.FLAG_PRESENTATION) != 0)
//                        + ", FLAG_SECURE=" + ((secondDisplay.getFlags() & Display.FLAG_SECURE) != 0) + ")");
//                Log.d("SecondScreen", "Display state: " + secondDisplay.getState());
//                Log.d("SecondScreen", "Display size: " + secondDisplay.getWidth() + "x" + secondDisplay.getHeight());
//                DisplayMetrics metrics = new DisplayMetrics();
//                secondDisplay.getRealMetrics(metrics);
//                Log.d("SecondScreen", "Display DPI: " + metrics.densityDpi + ", size: " + metrics.widthPixels + "x" + metrics.heightPixels);

                Log.d("SecondScreen", "Using display: " + secondDisplay.getDisplayId());

                // Reopening should not leak the previous Presentation/WebView.
                if (secondScreenPresentation != null) {
                    secondScreenPresentation.dismiss();
                    secondScreenPresentation = null;
                }

                secondScreenPresentation = new SecondScreenPresentation(activity, secondDisplay, getBridge());
                secondScreenPresentation.show();

                JSObject result = new JSObject();
                result.put("displayId", secondDisplay.getDisplayId());

                call.resolve(result);

            } catch (Exception e) {
                Log.e("SecondScreen", "Failed to show second screen", e);
                call.reject("Failed to show second screen: " + e.getMessage(), e);
            }
        });
    }

    @PluginMethod
    public void hideSecondScreen(PluginCall call) {
        Activity activity = getActivity();

        activity.runOnUiThread(() -> {
            try {
                if (secondScreenPresentation != null) {
                    secondScreenPresentation.dismiss();
                    secondScreenPresentation = null;
                }

                call.resolve();

            } catch (Exception e) {
                Log.e("SecondScreen", "Failed to hide second screen", e);
                call.reject("Failed to hide second screen: " + e.getMessage(), e);
            }
        });
    }

    @PluginMethod
    public void updateSecondScreen(PluginCall call) {
        // Accepts an arbitrary nested object from JS, e.g.:
        //   { data: { invoiceNumber, items: [...], total, status, ... } }
        // The shape is intentionally opaque to native code - it's just
        // forwarded to the WebView and rendered with `| json` on the Angular
        // side, so new data fields don't require touching this plugin.
        JSObject data = call.getObject("data", new JSObject());

        if (secondScreenPresentation instanceof SecondScreenPresentation) {
            SecondScreenPresentation screen = (SecondScreenPresentation) secondScreenPresentation;

            getActivity().runOnUiThread(() ->
                    screen.updateData(data)
            );
        }

        call.resolve();
    }

    private static class SecondScreenPresentation extends Presentation {

        private WebView webView;
        private final Bridge bridge;
        private boolean ready = false;
        private JSObject pendingData;

        SecondScreenPresentation(Context context, Display display, Bridge bridge) {
            super(context, display);
            this.bridge = bridge;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            webView = new WebView(getContext());

            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);

            // Exposes window.SecondScreenBridge.isSecondScreen() to JS. Registered
            // BEFORE loadUrl(), so it's already present when Angular's bootstrap
            // code runs - no timing/parsing race like a URL param would have.
            webView.addJavascriptInterface(new Object() {
                @JavascriptInterface
                public boolean isSecondScreen() {
                    return true;
                }
            }, "SecondScreenBridge");

            // Hidden until window.secondScreenReady() confirms the Angular
            // route has actually mounted - avoids a flash of the main POS UI
            // on the external display before it navigates away.
            webView.setAlpha(0f);

            webView.setWebViewClient(new BridgeWebViewClient(bridge) {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.d("SecondScreen", "Page finished: " + url);
                    waitForSecondScreenReady(view);
                }
            });

            // Same app URL as the main screen - the WebView loads the whole
            // app and app.component.ts's router.navigate(['/second-screen'])
            // is what actually switches it to the second-screen route.
            String url = bridge.getAppUrl();
            webView.loadUrl(url);

            setContentView(webView);
        }

        // Polls for window.secondScreenReady() every 100ms until it's defined,
        // since onPageFinished fires before Angular has bootstrapped and
        // registered the global. Once it succeeds, fades the WebView in.
        private void waitForSecondScreenReady(WebView view) {
            view.evaluateJavascript(
                    "(function() {" +
                            "if (window.secondScreenReady) {" +
                            "    window.secondScreenReady();" +
                            "    return 'opened';" +
                            "}" +
                            "return 'not-ready';" +
                            "})()",
                    result -> {
                        Log.d("SecondScreen", "secondScreenReady result: " + result);

                        if ("\"opened\"".equals(result)) {
                            ready = true;
                            if (pendingData != null) {
                                sendData(pendingData);
                                pendingData = null;
                            }
                            view.postDelayed(() -> view.setAlpha(1f), 100);
                            return;
                        }

                        view.postDelayed(() -> waitForSecondScreenReady(view), 100);
                    }
            );
        }

        @Override
        public void dismiss() {
            if (webView != null) {
                webView.stopLoading();
                webView.loadUrl("about:blank");
                webView.setWebViewClient(null);
                webView.destroy();
                webView = null;
            }

            super.dismiss();
        }

        // Native -> Screen 2:
        // Execute `window.updateSecondScreen(data)` inside Screen 2's WebView.
        void updateData(JSObject data) {
            pendingData = data;
            if (webView == null || !ready) return;
            sendData(data);
        }

        private void sendData(JSObject data) {
            if (webView == null) return;

            String json = data.toString();
            webView.evaluateJavascript(
                    "window.updateSecondScreen(" + json + ");",
                    null
            );
        }
    }

}
