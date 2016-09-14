package org.narcotek.cputemp.systemui.xposed;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.ViewGroup;

import org.narcotek.cputemp.common.helper.IntentStrings;
import org.narcotek.cputemp.common.helper.PrefBroadcastHelper;
import org.narcotek.cputemp.systemui.helper.PrefUpdateReceiver;
import org.narcotek.cputemp.systemui.helper.ScreenChangeReceiver;
import org.narcotek.cputemp.systemui.log.XposedLog;
import org.narcotek.cputemp.systemui.ui.TempViewManager;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * This class is run after boot. This is where the SystemUI gets hooked and the temperature view
 * manager initialized.
 */
public final class TempModule implements IXposedHookInitPackageResources, IXposedHookLoadPackage {

    private static final String TAG = TempModule.class.getSimpleName();

    public static final String SYSUI_PACKAGE = "com.android.systemui";

    // Hooking methods for dark status icons on 6.0+
    @Override
    public void handleLoadPackage(LoadPackageParam lpParam) throws Throwable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && lpParam.packageName.equals(SYSUI_PACKAGE)) {
            // Target class and method
            String targetClass = "com.android.systemui.statusbar.phone.StatusBarIconController";
            String targetMethod = "setIconsDark";

            if (XposedHelpers.findMethodExactIfExists(targetClass, lpParam.classLoader,
                    targetMethod, boolean.class) != null) {
                // For Android version 6.0; parameter is "boolean dark"
                XposedHelpers.findAndHookMethod(targetClass, lpParam.classLoader, targetMethod,
                        boolean.class, new DarkIconsHook60());

                XposedLog.log(TAG, "Successfully hooked setIconsDark (6.0)!");
            } else if (XposedHelpers.findMethodExactIfExists(targetClass, lpParam.classLoader,
                    targetMethod, boolean.class, boolean.class) != null) {
                // For Android version 6.0.1+; parameters are "boolean dark" and "boolean animate"
                XposedHelpers.findAndHookMethod(targetClass, lpParam.classLoader, targetMethod,
                        boolean.class, boolean.class, new DarkIconsHook601());

                XposedLog.log(TAG, "Successfully hooked setIconsDark (6.0.1+)!");
            } else {
                XposedLog.log(TAG, "No setIconsDark method available!");
            }
        }
    }

    // Hooking status bar layout inflation
    @Override
    public void handleInitPackageResources(InitPackageResourcesParam resParam) throws Throwable {
        // Hooking into SystemUI
        if (resParam.packageName.equals(SYSUI_PACKAGE)) {
            if (resParam.res.getIdentifier("status_bar_simple", "layout", SYSUI_PACKAGE) != 0) {
                // Layout on MIUI ROMs
                resParam.res.hookLayout(SYSUI_PACKAGE, "layout", "status_bar_simple", new MIUIStatusBarHook());

                XposedLog.log(TAG, "Successfully hooked MIUI status bar!");
            } else if (resParam.res.getIdentifier("status_bar", "layout", SYSUI_PACKAGE) != 0) {
                // Layout on AOSP based ROMs
                resParam.res.hookLayout(SYSUI_PACKAGE, "layout", "status_bar", new AOSPStatusBarHook());

                XposedLog.log(TAG, "Successfully hooked AOSP status bar!");
            } else {
                XposedLog.log(TAG, "ROM is not supported(yet); status bar not modified.");
            }
        }
    }

    /**
     * Small method which initializes the temperature view manager and the different broadcast
     * receivers needed for the temperature views
     *
     * @param context   Any Context of the SystemUI app
     * @param leftArea  Reference to a ViewGroup on the left side of the status bar
     * @param rightArea Reference to a ViewGroup on the right side of the status bar
     */
    public static void initialize(Context context, ViewGroup leftArea, ViewGroup rightArea) {
        XposedLog.log(TAG, "Initializing manager and receivers...");

        /*
         * Usually the leftArea field is the notification area or the status bar itself. The
         * rightArea is usually the system icon area containing the network status icon, the
         * bluetooth icon, etc.
         */
        // Creating instance of singleton manager and initialization
        TempViewManager tvm = TempViewManager.getInstance();
        tvm.initialize(context, leftArea, rightArea);

        // Broadcast receiver for preference transmission
        PrefUpdateReceiver prefUpdateReceiver = new PrefUpdateReceiver();
        context.registerReceiver(prefUpdateReceiver,
                new IntentFilter(IntentStrings.INTENT_CPUTEMP_PREF_UPDATE));

        // Broadcast receiver for screen on/off broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        ScreenChangeReceiver screenChangeReceiver = new ScreenChangeReceiver();
        context.registerReceiver(screenChangeReceiver, filter);

        XposedLog.log(TAG, "Broadcasting preference request...");

        // Requesting preferences from the CPUTemp app
        PrefBroadcastHelper.broadcastRequestPreferences(context);
    }

}