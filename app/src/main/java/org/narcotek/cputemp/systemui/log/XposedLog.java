package org.narcotek.cputemp.systemui.log;

import de.robv.android.xposed.XposedBridge;

/**
 * Small helper class which allows logging to Xposed in a similiar way Android's log class does
 */
public final class XposedLog {

    private static final String PACKAGE = "org.narcotek.cputemp";

    public static final void log(String tag, String msg, Throwable t) {
        // TODO format throwable string output
        log(tag, msg + "\n" + t.toString());
    }

    public static void log(String tag, String msg) {
        XposedBridge.log(PACKAGE + "/" + tag + ": " + msg);
    }

}
