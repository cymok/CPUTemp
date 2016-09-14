package org.narcotek.cputemp.systemui.ui;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

import org.narcotek.cputemp.systemui.io.DefaultTempReader;
import org.narcotek.cputemp.systemui.io.RootTempReader;
import org.narcotek.cputemp.systemui.io.TempReader;
import org.narcotek.cputemp.systemui.log.XposedLog;
import org.narcotek.cputemp.systemui.root.RootNotGrantedException;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Core class of the SystemUI part of CPUTemp; the singleton instance of this class manages the
 * temperature view(s), their preferences and the periodic updates. A thread pool is used to
 * periodically update the label(s) by reading their corresponding temperature file. This class and
 * all others of the "systemui" package are used only by the SystemUI app and Xposed.
 */
public final class TempViewManager {

    private static final String TAG = TempViewManager.class.getSimpleName();

    private static TempViewManager INSTANCE;

    /*
     * IMPORTANT: Currently only one temperature label; sometime soon support for more temperature
     * labels is going to be added
     */
    private TempView tempView;

    // General objects needed for injection of the label
    private Context context;
    private ViewGroup leftArea;
    private ViewGroup rightArea;

    // Flag telling whether or not CPUTemp is enabled
    private boolean enabled;

    // Objects for periodic updating
    private int updateInterval;
    private boolean updating;
    private final ScheduledThreadPoolExecutor exec;
    private ScheduledFuture future;
    private final Runnable updateRoutine;
    private TempReader tempReader;

    // Only for 6.0+; tells if the status bar uses a light colored background color and a contrasting foreground color
    private boolean dark;

    /**
     * Default constructor initializing final fields
     */
    private TempViewManager() {
        // Update routine is later passed the thread pool
        updateRoutine = new UpdateRoutineRunnable();

        // ThreadPoolExecutor initialization
        exec = new ScheduledThreadPoolExecutor(1);
    }

    /**
     * Returns if CPUTemp is enabled or not
     *
     * @return true/false
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns if the manager is currently periodically updating or not
     *
     * @return true/false
     */
    public boolean isUpdating() {
        return updating;
    }

    /**
     * Returns if the preferences have already been set at least once
     *
     * @return true/false
     */
    public boolean isInitialized() {
        return tempView != null;
    }

    /**
     * Returns the manager's singleton instance
     *
     * @return Singleton instance
     */
    public static TempViewManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TempViewManager();
        }
        return INSTANCE;
    }

    /**
     * Initializes the general objects for the manager
     *
     * @param context   Any Context of the SystemUI app
     * @param leftArea  Reference to a ViewGroup on the left side of the status bar
     * @param rightArea Reference to a ViewGroup on the right side of the status bar
     */
    public void initialize(Context context, ViewGroup leftArea, ViewGroup rightArea) {
        this.context = context;
        this.leftArea = leftArea;
        this.rightArea = rightArea;
    }

    /**
     * Sets the preferences for the manager and the temperature label
     *
     * @param prefs HashMap containing the preferences
     * @throws IOException             If the commands "su" or "id" could not be executed
     * @throws RootNotGrantedException If root was not granted
     */
    public void setPreferences(HashMap prefs) throws IOException, RootNotGrantedException {
        /*
         * String resources and shared preferences can't be accessed from here, because the TempView
         * is created in the context of the SystemUI app.
         */
        // General settings (TempViewManager)
        enabled = Boolean.parseBoolean(prefs.get("enable_ct").toString());

        if (enabled) {
            XposedLog.log(TAG, "CPUTemp is enabled; setting preferences...");

            updateInterval = Integer.valueOf(prefs.get("update_interval").toString());

            boolean useRoot = (Boolean) prefs.get("use_root");
            tempReader = useRoot ? new RootTempReader() : new DefaultTempReader();

            // Boolean flag telling if the status bar has a light background
            dark = false;

            // Temperature label(s) settings
            if (tempView == null) {
                tempView = new TempView(context, leftArea, rightArea);
            }

            XposedLog.log(TAG, this.toString());
            XposedLog.log(TAG, tempView.toString());

            tempView.setPreferences(prefs);
        } else {
            XposedLog.log(TAG, "CPUTemp is disabled; clearing fields...");

            tempReader = null;
            tempView = null;
        }
    }

    /**
     * This method sets an internal boolean which tells if the status bar uses a light background
     * color and contrasting foreground colors. If the parameter dark is true, the temperature
     * label's font color is set to the saved contrasting one. If the parameter is false the saved
     * normal font color is used. This method also automatically colorizes the temperature label.
     *
     * @param dark    True if a dark font color should be used
     * @param animate True if a color animation should be shown
     */
    public void setDark(boolean dark, boolean animate) {
        if (tempView != null) {
            this.dark = dark;
            tempView.colorize(dark, animate);
        }
    }

    /**
     * Attaches the temperature label to the status bar and starts the periodic updates
     */
    public void startUpdating() {
        attachView();

        if (tempReader != null) {
            future = exec.scheduleAtFixedRate(updateRoutine, 0, updateInterval, TimeUnit.MILLISECONDS);
            updating = true;
        }

        XposedLog.log(TAG, "Attached view; started updating.");
    }

    /**
     * Stops the periodic updates for the temperature label and detaches it from the status bar
     */
    public void stopUpdating() {
        if (future != null) {
            future.cancel(false);
            updating = false;
        }

        detachView();

        XposedLog.log(TAG, "Stopped updating; detached view.");
    }

    /**
     * Shows a Toast with the given error message
     *
     * @param errorMsg Error message to show
     */
    public void handleError(String errorMsg) {
        enabled = false;
        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns the active preferences of the manager as a readable string
     *
     * @return Manager preferences as a readable string
     */
    @Override
    public String toString() {
        return "TempViewManager Preferences -> {"
                + "\n\tenabled=" + enabled
                + ";\n\tupdateInterval=" + updateInterval
                + ";\n\ttempReader=" + tempReader
                + "\n}";
    }

    /**
     * Attaches the temperature label to the status bar
     */
    private void attachView() {
        tempView.attachContainer();
    }

    /**
     * Detaches the temperature label from the status bar
     */
    private void detachView() {
        tempView.detachContainer();
    }

    /**
     * Inner class for update routine
     */
    private final class UpdateRoutineRunnable implements Runnable {

        @Override
        public void run() {
            try {
                // Reading value from file
                final double temp = tempReader.readTemperature(tempView.getTempFile());

                tempView.post(new Runnable() {

                    @Override
                    public void run() {
                        tempView.updateTemp(temp);
                        tempView.colorize(dark, false);
                    }

                });
            } catch (Exception ex) {
                XposedLog.log(TAG, "Could not read temperature file!", ex);

                tempView.post(new Runnable() {

                    @Override
                    public void run() {
                        handleError("Could not read temperature file!");

                        stopUpdating();
                    }

                });
            }
        }

    }

}
