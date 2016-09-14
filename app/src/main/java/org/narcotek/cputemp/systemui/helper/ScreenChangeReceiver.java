package org.narcotek.cputemp.systemui.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.narcotek.cputemp.systemui.log.XposedLog;
import org.narcotek.cputemp.systemui.ui.TempViewManager;

/**
 * Broadcast receiver for screen changes (screen on/off); stops the temperature view manager's
 * update routine if the screen is off and starts it again when the screen turns on
 */
public final class ScreenChangeReceiver extends BroadcastReceiver {

    private static final String TAG = ScreenChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        TempViewManager tvm = TempViewManager.getInstance();

        if (tvm.isEnabled() && tvm.isInitialized()) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON) && !tvm.isUpdating()) {
                XposedLog.log(TAG, "Received screen on!");
                tvm.startUpdating(); // Screen on -> updating again
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && tvm.isUpdating()) {
                XposedLog.log(TAG, "Received screen off!");
                tvm.stopUpdating();
            }
        }
    }

}
