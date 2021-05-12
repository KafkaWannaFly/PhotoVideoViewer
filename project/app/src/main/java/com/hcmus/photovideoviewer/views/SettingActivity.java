package com.hcmus.photovideoviewer.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.hcmus.photovideoviewer.MainActivity;
import com.hcmus.photovideoviewer.R;

import java.util.Objects;


public class SettingActivity extends AppCompatActivity {
    SettingFragment fragment = null;
    boolean switchPref = true;
    private int mThemeId = 0;
    static SharedPreferences prefs = null;
    public SettingActivity(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final AppCompatDelegate delegate = getDelegate();
        delegate.installViewFactory();
        delegate.onCreate(savedInstanceState);
        if (delegate.applyDayNight() && mThemeId != 0) {
            if (Build.VERSION.SDK_INT >= 23) {
                onApplyThemeResource(getTheme(), mThemeId, false);
            } else {
                setTheme(mThemeId);
            }
        }
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //getSupportActionBar().setTitle("Settings");
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        getSupportActionBar().setTitle("Settings");
        if (findViewById(R.id.idFrameLayout) != null) {
            if (savedInstanceState != null) {
                return;
            }
            // below line is to inflate our fragment.
            fragment = new SettingFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.idFrameLayout, fragment).commit();
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
        }
    }
    @Override
    public void setTheme(@StyleRes final int resid) {
        super.setTheme(resid);
        // Keep hold of the theme id so that we can re-set it later if needed
        mThemeId = resid;
    }
    public static class SettingFragment extends PreferenceFragmentCompat {
        public SettingFragment(){}
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.main_preference_screen, rootKey);
        }
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Preference preference = (Preference) findPreference("setTheme");
            assert preference != null;
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    boolean switchPref = prefs.getBoolean("setTheme", false);
                    if(switchPref){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        requireActivity().recreate();
                    }else{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        requireActivity().recreate();
                    }
                    Log.d("TAG", "onCreatePreferences: " + switchPref);
                    return false;
                }
            });
        }
    }
}
