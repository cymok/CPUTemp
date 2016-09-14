package org.narcotek.cputemp.common.helper;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import java.util.HashMap;

/**
 * Helper class for the SystemUI part and the app part of CPUTemp; allows sending specific broadcasts
 */
public final class PrefBroadcastHelper {

    /**
     * Used by the SystemUI part to request the saved preferences
     *
     * @param context Any context of SystemUI to send the broadcast
     */
    public static void broadcastRequestPreferences(Context context) {
        Intent i = new Intent();
        i.setAction(IntentStrings.INTENT_CPUTEMP_REQ_PREF);

        context.sendBroadcast(i);
    }

    /**
     * Broadcasts the saved preferences
     *
     * @param context Any context of CPUTemp to send the broadcast
     */
    public static void broadcastPreferences(Context context) {
        Intent i = new Intent();
        i.setAction(IntentStrings.INTENT_CPUTEMP_PREF_UPDATE);
        i.putExtra("prefs", new HashMap(PreferenceManager.getDefaultSharedPreferences(context).getAll()));

        context.sendBroadcast(i);
    }

}
