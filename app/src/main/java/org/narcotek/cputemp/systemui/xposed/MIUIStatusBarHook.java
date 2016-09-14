package org.narcotek.cputemp.systemui.xposed;

import android.content.Context;
import android.view.ViewGroup;

import org.narcotek.cputemp.systemui.log.XposedLog;

import de.robv.android.xposed.callbacks.XC_LayoutInflated;

import static org.narcotek.cputemp.systemui.xposed.TempModule.SYSUI_PACKAGE;

/**
 * Status bar layout hook for MIUI ROMs
 */
public class MIUIStatusBarHook extends XC_LayoutInflated {

    private static final String TAG = MIUIStatusBarHook.class.getSimpleName();

    @Override
    public void handleLayoutInflated(XC_LayoutInflated.LayoutInflatedParam liParam) {
        try {
            /*
             * Tested on MIUI 7; TODO MIUI support still needs testing
             *
             * Left area:
             * Notifications icons are inside a LinearLayout (notification_icon_area);
             * ONLY if notification icons are enabled
             *
             * Right area:
             * System icons inside a LinearLayout (status_bar_icons)
             */
            XposedLog.log(TAG, "MIUI status bar inflated (status_bar_simple).");

            int leftAreaId = liParam.res.getIdentifier("notification_icon_area", "id", SYSUI_PACKAGE);
            int rightAreaId = liParam.res.getIdentifier("statusbar_icon", "id", SYSUI_PACKAGE);

            // Getting the ViewGroup objects
            ViewGroup notificationIconArea = (ViewGroup) liParam.view.findViewById(leftAreaId);
            ViewGroup systemIconArea = (ViewGroup) liParam.view.findViewById(rightAreaId);

            // Getting the application context
            Context context = liParam.view.getContext();

            // Initializing the manager and the broadcast receivers
            TempModule.initialize(context, notificationIconArea, systemIconArea);

            XposedLog.log(TAG, "MIUI status bar modified; created and inserted label.");
        } catch (Throwable t) {
            // Try & catch to avoid recurring FCs of SystemUI
            XposedLog.log(TAG, "Error while modifying MIUI status bar!", t);
        }
    }

}
