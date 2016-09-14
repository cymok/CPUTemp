package org.narcotek.cputemp.systemui.xposed;

import org.narcotek.cputemp.systemui.log.XposedLog;
import org.narcotek.cputemp.systemui.ui.TempViewManager;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Method hook for setIconsDark (6.0.1)
 */
public class DarkIconsHook601 extends XC_MethodHook {

    private static final String TAG = DarkIconsHook601.class.getSimpleName();

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        XposedLog.log(TAG, "Method setIconsDark (6.0.1+) called with parameters: " + param.args[0] + "; " + param.args[1]);

        TempViewManager tmv = TempViewManager.getInstance();
        tmv.setDark((boolean) (param.args[0]), (boolean) (param.args[1]));
    }

}
