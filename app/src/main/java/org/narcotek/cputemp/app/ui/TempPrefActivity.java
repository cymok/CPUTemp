package org.narcotek.cputemp.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pavelsikun.vintagechroma.ChromaPreference;

import org.narcotek.cputemp.R;
import org.narcotek.cputemp.common.helper.PrefBroadcastHelper;

/**
 * Standard preference activity
 */
public final class TempPrefActivity extends AppCompatActivity {

    private static final String ABOUT_DIALOG_TAG = "about_dialog";
    private static final String LICENSES_DIALOG_TAG = "licenses_dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CpuTempPrefFragment fragment = new CpuTempPrefFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info_about:
                (new AboutDialog()).show(getFragmentManager(), ABOUT_DIALOG_TAG);
                return true;
            case R.id.menu_info_licenses:
                (new LicensesDialog()).show(getFragmentManager(), LICENSES_DIALOG_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static final class CpuTempPrefFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);

            // Temporary fix for VintageChroma issue #17
            ChromaPreference cpFontColor = (ChromaPreference) findPreference(getString(R.string.pref_key_font_color));
            cpFontColor.setColor(cpFontColor.getColor());

            ChromaPreference cpDarkFontColor = (ChromaPreference) findPreference(getString(R.string.pref_key_dark_font_color));
            cpDarkFontColor.setColor(cpDarkFontColor.getColor());

            ChromaPreference cpUpperLimitFontColor = (ChromaPreference) findPreference(getString(R.string.pref_key_upper_limit_font_color));
            cpUpperLimitFontColor.setColor(cpUpperLimitFontColor.getColor());

            ChromaPreference cpLowerLimitFontColor = (ChromaPreference) findPreference(getString(R.string.pref_key_lower_limit_font_color));
            cpLowerLimitFontColor.setColor(cpLowerLimitFontColor.getColor());

            ChromaPreference cpUpperLimitDarkFontColor = (ChromaPreference) findPreference(getString(R.string.pref_key_upper_limit_dark_font_color));
            cpUpperLimitDarkFontColor.setColor(cpUpperLimitDarkFontColor.getColor());

            ChromaPreference cpLowerLimitDarkFontColor = (ChromaPreference) findPreference(getString(R.string.pref_key_lower_limit_dark_font_color));
            cpLowerLimitDarkFontColor.setColor(cpLowerLimitDarkFontColor.getColor());

            // Dark colored icons (for light status bar) is only available for 6.0+
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // Hiding dark font color preferences
                getPreferenceScreen().removePreference(findPreference(getString(R.string.pref_cat_key_dark_colors)));
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            PrefBroadcastHelper.broadcastPreferences(this.getActivity());
        }

    }

}
