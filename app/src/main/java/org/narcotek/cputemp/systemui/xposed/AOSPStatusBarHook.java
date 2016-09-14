package org.narcotek.cputemp.systemui.xposed;

import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;

import org.narcotek.cputemp.systemui.log.XposedLog;

import de.robv.android.xposed.SELinuxHelper;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

import static org.narcotek.cputemp.systemui.xposed.TempModule.SYSUI_PACKAGE;

/**
 * Status bar layout hook for AOSP based ROMs
 */
public class AOSPStatusBarHook extends XC_LayoutInflated {

    private static final String TAG = AOSPStatusBarHook.class.getSimpleName();

    @Override
    public void handleLayoutInflated(LayoutInflatedParam liParam) {
        try {
            /*
             * Tested on 5.0, 5.1 and 6.0; TODO 7.0 needs testing
             *
             * Left area:
             * 7.0 uses a AlphaOptimizedFrameLayout (notification_icon_area)
             * for notification icons
             * 5.0 - 6.0 use a AlphaOptimizedLinearLayout (notification_icon_area_inner)
             * for notification icons
             *
             * Right area:
             * 5.0+ uses a AlphaOptimizedLinearLayout (system_icon_area) for system
             * icons
             */
            XposedLog.log(TAG, "AOSP status bar inflated (status_bar).");

            int leftAreaId, rightAreaId;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                leftAreaId = liParam.res.getIdentifier("notification_icon_area", "id", SYSUI_PACKAGE);
            } else {
                leftAreaId = liParam.res.getIdentifier("notification_icon_area_inner", "id", SYSUI_PACKAGE);
            }

            rightAreaId = liParam.res.getIdentifier("system_icon_area", "id", SYSUI_PACKAGE);

            // Getting the ViewGroup objects
            ViewGroup leftArea = (ViewGroup) liParam.view.findViewById(leftAreaId);
            ViewGroup rightArea = (ViewGroup) liParam.view.findViewById(rightAreaId);

            // Getting the application context
            Context context = liParam.view.getContext();

            // Initializing the manager and the broadcast receivers
            TempModule.initialize(context, leftArea, rightArea);

            XposedLog.log(TAG, "AOSP status bar modified; created and inserted label.");
        } catch (Throwable t) {
            // Try & catch to avoid recurring FCs of SystemUI
            XposedLog.log(TAG, "Error while modifying AOSP status bar!", t);
        }
    }

}
