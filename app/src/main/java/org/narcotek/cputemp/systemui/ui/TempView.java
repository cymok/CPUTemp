package org.narcotek.cputemp.systemui.ui;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.narcotek.cputemp.systemui.helper.StringUtils;
import org.narcotek.cputemp.systemui.log.XposedLog;

import java.io.File;
import java.util.HashMap;

/**
 * A modified TextView; instances are inserted to the status bar to display the temperature value
 * read from a file.
 */
final class TempView extends TextView {

    private static final String TAG = TempView.class.getSimpleName();

    // Container object for the TextView (needed for margin)
    private final LinearLayout container;

    // References to the ViewGroup objects for positioning
    private final ViewGroup leftArea;
    private final ViewGroup rightArea;

    // Preference dependent values
    private String tempFilePath;

    private Position position;
    private int leftMargin;
    private int rightMargin;

    private int fontSize;
    private int fontColor;

    private boolean limitEnabled;
    private double upperLimit;
    private int upperLimitFontColor;
    private double lowerLimit;
    private int lowerLimitFontColor;

    private boolean darkEnabled;
    private int darkFontColor;
    private int upperLimitDarkFontColor;
    private int lowerLimitDarkFontColor;

    private String formatString; // A format string for temperature
    private int precision;

    private double divisor;
    private boolean useFahrenheit;

    // Temperature file reference
    private File tempFile;

    private double currentTemp;

    /**
     * Default constructor setting general fields needed for the temperature label
     *
     * @param context   Any Context of the SystemUI app
     * @param leftArea  Reference to a ViewGroup on the left side of the status bar
     * @param rightArea Reference to a ViewGroup on the right side of the status bar
     */
    public TempView(Context context, ViewGroup leftArea, ViewGroup rightArea) {
        super(context);

        // Setting the left and right area references
        this.leftArea = leftArea;
        this.rightArea = rightArea;

        // Container for CPU temp label; easier to format this way
        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        container.setGravity(Gravity.CENTER_VERTICAL);
        container.addView(this);

        this.setSingleLine(true);
    }

    /**
     * Returns the temperature file for this label
     *
     * @return File object for the temperature file
     */
    public File getTempFile() {
        return tempFile;
    }

    /**
     * Sets the preference fields with the values of the HashMap
     *
     * @param prefs HashMap of preferences
     */
    public void setPreferences(HashMap prefs) {
        tempFilePath = prefs.get("temp_file").toString();
        tempFile = new File(tempFilePath);

        // Converting dp to px
        leftMargin = (int) (Integer.parseInt(prefs.get("left_margin").toString()) * getResources().getDisplayMetrics().density + 0.5f);
        rightMargin = (int) (Integer.parseInt(prefs.get("right_margin").toString()) * getResources().getDisplayMetrics().density + 0.5f);

        fontSize = Integer.parseInt(prefs.get("font_size").toString());
        fontColor = Integer.parseInt(prefs.get("font_color").toString());

        limitEnabled = (Boolean) prefs.get("enable_limit");
        upperLimit = Double.parseDouble(prefs.get("upper_limit").toString());
        upperLimitFontColor = Integer.parseInt(prefs.get("upper_limit_font_color").toString());
        lowerLimit = Double.parseDouble(prefs.get("lower_limit").toString());
        lowerLimitFontColor = Integer.parseInt(prefs.get("lower_limit_font_color").toString());

        darkEnabled = (Boolean) prefs.get("enable_dark");
        darkFontColor = Integer.parseInt(prefs.get("dark_font_color").toString());
        upperLimitDarkFontColor = Integer.parseInt(prefs.get("upper_limit_dark_font_color").toString());
        lowerLimitDarkFontColor = Integer.parseInt(prefs.get("lower_limit_dark_font_color").toString());

        formatString = prefs.get("format_string").toString();
        divisor = Double.parseDouble(prefs.get("divisor").toString());
        useFahrenheit = Boolean.parseBoolean(prefs.get("use_fahrenheit").toString());
        precision = StringUtils.findPrecision(formatString);

        String pos = prefs.get("position").toString();
        if (pos.equals("0")) {
            position = Position.ABSOLUTE_RIGHT;
        } else if (pos.equals("1")) {
            position = Position.RIGHT;
        } else {
            position = Position.LEFT;
        }
    }

    /**
     * Sets the label's style and attaches it to the status bar
     */
    public void attachContainer() {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        this.setTextColor(fontColor);
        this.setPadding(leftMargin, 0, rightMargin, 0);

        switch (position) {
            case ABSOLUTE_RIGHT:
                rightArea.addView(container);
                break;
            case RIGHT:
                rightArea.addView(container, 0);
                break;
            case LEFT:
                leftArea.addView(container, 0);
                break;
        }
    }

    /**
     * Detaches the label from the status bar
     */
    public void detachContainer() {
        leftArea.removeView(container);
        rightArea.removeView(container);
    }

    /**
     * Returns the active preferences of this label as a readable string
     *
     * @return Temperature view preferences as readable string
     */
    @Override
    public String toString() {
        return "TempView Preferences -> {"
                + "\n\ttempFilePath=" + tempFilePath
                + ";\n\tposition=" + position
                + ";\n\tleftMargin=" + leftMargin
                + ";\n\trightMargin=" + rightMargin
                + ";\n\tfontSize=" + fontSize
                + ";\n\tfontColor=" + fontColor
                + ";\n\tlimitEnabed=" + limitEnabled
                + ";\n\tupperLimit=" + upperLimit
                + ";\n\tupperLimitFontColor=" + upperLimitFontColor
                + ";\n\tlowerLimit=" + lowerLimit
                + ";\n\tlowerLimitFontColor=" + lowerLimitFontColor
                + ";\n\tdarkEnabled=" + darkEnabled
                + ";\n\tdarkFontColor=" + darkFontColor
                + ";\n\tupperLimitDarkFontColor=" + upperLimitDarkFontColor
                + ";\n\tlowerLimitDarkFontColor=" + lowerLimitDarkFontColor
                + ";\n\tformatString=" + formatString
                + ";\n\tdivisor=" + divisor
                + ";\n\tuseFahrenheit=" + useFahrenheit
                + ";\n\tprecision=" + precision
                + ";\n}";
    }

    /**
     * Colors the temperature view depending on the temperature limit preferences and the first
     * parameter (dark)
     *
     * @param dark    If true contrasting (dark) colors are used
     * @param animate If true a color animation is shown
     */
    public void colorize(boolean dark, boolean animate) {
        int newColor;

        if (limitEnabled) {
            if (currentTemp > upperLimit) {
                newColor = darkEnabled && dark ? upperLimitDarkFontColor : upperLimitFontColor;
            } else if (currentTemp < lowerLimit) {
                newColor = darkEnabled && dark ? lowerLimitDarkFontColor : lowerLimitFontColor;
            } else {
                newColor = darkEnabled && dark ? darkFontColor : fontColor;
            }
        } else {
            newColor = darkEnabled && dark ? darkFontColor : fontColor;
        }

        if (animate) {
            // TODO animate color change (oldColor --> newColor)

            setTextColor(newColor);
        } else {
            setTextColor(newColor);
        }
    }

    /**
     * Calculates the to-be-shown-temperature using the saved preferences and sets it as the label's
     * text
     *
     * @param temp Temperature read from file
     */
    public void updateTemp(double temp) {
        currentTemp = temp;

        XposedLog.log(TAG, "Read temperature: " + currentTemp);

        // Calculation
        currentTemp /= divisor;
        if (useFahrenheit) {
            currentTemp = currentTemp * 1.8 + 32.0;
        }

        XposedLog.log(TAG, "Calculated temperature: " + currentTemp);

        // Setting text
        TempView.this.setText(formatString.replaceFirst("%\\d*T",
                String.format("%." + precision + "f", currentTemp)).replaceFirst("%U", useFahrenheit ? "°F" : "°C"));
    }

}
