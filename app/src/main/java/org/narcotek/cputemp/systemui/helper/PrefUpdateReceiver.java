package org.narcotek.cputemp.systemui.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.narcotek.cputemp.systemui.log.XposedLog;
import org.narcotek.cputemp.systemui.root.RootNotGrantedException;
import org.narcotek.cputemp.systemui.ui.TempViewManager;

import java.io.IOException;
import java.util.HashMap;

/**
 * Communication class for the SystemUI part of CPUTemp; receives preference updates (requested or
 * not) and updates the temperature label's preferences
 */
public final class PrefUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = PrefUpdateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        TempViewManager tvm = TempViewManager.getInstance();

        XposedLog.log(TAG, "Received preference update!");
        try {
            if (tvm.isUpdating()) {
                tvm.stopUpdating();
            }

            XposedLog.log(TAG, "Setting preferences...");
            tvm.setPreferences((HashMap) intent.getSerializableExtra("prefs"));

            if (tvm.isEnabled()) {
                tvm.startUpdating();
            }
        } catch (RootNotGrantedException ex) {
            XposedLog.log(TAG, "Root was not granted!", ex);

            tvm.handleError("Root was not granted!");
        } catch (IOException ex) {
            XposedLog.log(TAG, "\"su\" command was denied!", ex);

            tvm.handleError("\"su\" command was denied!");
        }
    }

}
