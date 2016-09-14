package org.narcotek.cputemp.systemui.xposed;

import org.narcotek.cputemp.systemui.log.XposedLog;
import org.narcotek.cputemp.systemui.ui.TempViewManager;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Method hook for setIconsDark (6.0)
 */
public class DarkIconsHook60 extends XC_MethodHook {

    private static final String TAG = DarkIconsHook60.class.getSimpleName();

    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        XposedLog.log(TAG, "Method setIconsDark (6.0) called with parameters: " + param.args[0]);

        TempViewManager tmv = TempViewManager.getInstance();
        tmv.setDark((boolean) (param.args[0]), true);
    }

}

