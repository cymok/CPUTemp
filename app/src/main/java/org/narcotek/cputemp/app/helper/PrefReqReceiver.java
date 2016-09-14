package org.narcotek.cputemp.app.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.narcotek.cputemp.common.helper.PrefBroadcastHelper;

/**
 * Communication class for the app part of CPUTemp; receives the preference request broadcast and
 * answers with the saved preferences
 */
public final class PrefReqReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PrefBroadcastHelper.broadcastPreferences(context);
    }

}
