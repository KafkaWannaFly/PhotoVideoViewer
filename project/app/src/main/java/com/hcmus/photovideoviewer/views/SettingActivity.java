package com.hcmus.photovideoviewer.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.hcmus.photovideoviewer.MainActivity;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.services.LocaleHelper;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class SettingActivity extends AppCompatActivity {
    SettingFragment fragment = null;
    boolean switchPref = true;
    private int mThemeId = 0;
    static SharedPreferences prefsSetTheme = null;
    static SharedPreferences prefsSetLanguage = null;
    static SharedPreferences prefsSetPass = null;
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
            prefsSetTheme = PreferenceManager.getDefaultSharedPreferences(this);
            prefsSetLanguage = PreferenceManager.getDefaultSharedPreferences(this);
            prefsSetPass = PreferenceManager.getDefaultSharedPreferences(this);
        }
    }
    @Override
    public void setTheme(@StyleRes final int resid) {
        super.setTheme(resid);
        // Keep hold of the theme id so that we can re-set it later if needed
        mThemeId = resid;
    }
    public static class SettingFragment extends PreferenceFragmentCompat {
        public SettingFragment(){
        }
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.main_preference_screen, rootKey);
        }
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Preference prefSetTheme = (Preference) findPreference("setTheme");
            Preference prefSetLanguage = (Preference) findPreference("setLanguage");
//            Preference prefChangePass = (Preference) findPreference("changePassword");

            assert prefSetTheme != null;
            prefSetTheme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    boolean switchPref = prefsSetTheme.getBoolean("setTheme", false);
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
            prefSetLanguage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    boolean switchPref = prefsSetLanguage.getBoolean("setLanguage", false);
                    Context context = (SettingActivity)getContext();
                    if(!switchPref){
                        LocaleHelper.setLocale(context, "vi");
                        getActivity().recreate();
//                        Locale locale = new Locale("vi_VN");
//                        Locale.setDefault(locale);
//                        Configuration config = new Configuration();
//                        config.locale = locale;
//                        context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());
//                        getActivity().recreate();
                    }else{
                        LocaleHelper.setLocale(context, "");
                        getActivity().recreate();
                    }
                    return false;
                }
            });
//            ((EditTextPreference)prefChangePass).setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
//                @Override
//                public void onBindEditText(@NonNull EditText editText) {
//                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                }
//            });
        }
    }
}
