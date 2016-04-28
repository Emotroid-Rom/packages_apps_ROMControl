/*
 * Copyright (C) 2015 The Android Open Kang Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aokp.romcontrol.fragments.general;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.aokp.romcontrol.R;
import com.aokp.romcontrol.util.Helpers;

import java.io.File;
import java.io.IOException;
import java.io.DataOutputStream;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class GeneralSettingsFragment extends Fragment {

    public GeneralSettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_general_settings_main, container, false);

        Resources res = getResources();
        super.onCreate(savedInstanceState);

        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.general_settings_main, new GeneralSettingsPreferenceFragment())
                .commit();
        return v;
    }

    public static class GeneralSettingsPreferenceFragment extends PreferenceFragment 
            implements OnPreferenceChangeListener {

        public GeneralSettingsPreferenceFragment() {

        }

        private static final String TAG = "GeneralSettingsPreferenceFragment";
        private static final String KEY_LOCKCLOCK = "lock_clock";
        // Package name of the cLock app
        public static final String LOCKCLOCK_PACKAGE_NAME = "com.cyanogenmod.lockclock";
        private static final String DOZE_POWERSAVE_PROPERTY = "persist.sys.doze_powersave";
        private static final String DOZE_POWERSAVE_KEY = "doze_powersave";
        private final ArrayList<Preference> mAllPrefs = new ArrayList<Preference>();
        private final ArrayList<SwitchPreference> mResetSwitchPrefs  = new ArrayList<SwitchPreference>();

        private static final String KEY_OTA = "update_settings";
        public static final String OTA_PACKAGE_NAME = "com.emotion.ota";

        private static final String PREF_FLOATING_WINDOWS = "floating_windows";

        private Context mContext;
        private Preference mLockClock;
        private SwitchPreference mDozePowersave;
        private boolean mDontPokeProperties;
        private Preference mOtapackage;
        private Preference mStatsEmotion;
        private Preference mFloatingWindows;

        private static final String PREF_STATS_EMOTION = "emotion_stats";

        public static final String STATS_PACKAGE_NAME = "com.aokp.romcontrol";
        public static Intent INTENT_STATS = new Intent(Intent.ACTION_MAIN)
                .setClassName(STATS_PACKAGE_NAME, STATS_PACKAGE_NAME + ".romstats.AnonymousStats");


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            createCustomView();
        }

        private PreferenceScreen createCustomView() {
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragment_general_settings);
            PreferenceScreen prefSet = getPreferenceScreen();
            mContext = getActivity().getApplicationContext();
            PackageManager pm = getActivity().getPackageManager();

            // cLock app check
            mLockClock = (Preference)
                    prefSet.findPreference(KEY_LOCKCLOCK);
            if (!Helpers.isPackageInstalled(LOCKCLOCK_PACKAGE_NAME, pm)) {
                prefSet.removePreference(mLockClock);
            }

            // Ota app check
            mOtapackage = (Preference)
                    prefSet.findPreference(KEY_OTA);
            if (!Helpers.isPackageInstalled(OTA_PACKAGE_NAME, pm)) {
                prefSet.removePreference(mOtapackage);
            }

            mStatsEmotion = prefSet.findPreference(PREF_STATS_EMOTION);

            mDozePowersave = findAndInitSwitchPref(DOZE_POWERSAVE_KEY);
            updateDozePowersaveOptions();

            mFloatingWindows = prefSet.findPreference(PREF_FLOATING_WINDOWS);
            return prefSet;
        }

        private SwitchPreference findAndInitSwitchPref(String key) {
            SwitchPreference pref = (SwitchPreference) findPreference(key);
            if (pref == null) {
                throw new IllegalArgumentException("Cannot find preference with key = " + key);
            }
            mAllPrefs.add(pref);
            mResetSwitchPrefs.add(pref);
            return pref;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object objValue) {
            return false;
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference == mDozePowersave) {
                writeDozePowersaveOptions();
            } else if (preference == mStatsEmotion) {
                startActivity(INTENT_STATS);
            } else if (preference == mFloatingWindows) {
                Intent intent = new Intent(getActivity(), FloatingWindows.class);
                getActivity().startActivity(intent);
            } else {
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            }
            return false;
        }

        void pokeSystemProperties() {
            if (!mDontPokeProperties) {
                //noinspection unchecked
                (new SystemPropPoker()).execute();
            }
        }
     
        private void updateDozePowersaveOptions() {
            updateSwitchPreference(mDozePowersave, SystemProperties.getBoolean(DOZE_POWERSAVE_PROPERTY, false));
        }
        
        void updateSwitchPreference(SwitchPreference switchPreference, boolean value) {
            switchPreference.setChecked(value);
        }

         private void writeDozePowersaveOptions() {
            SystemProperties.set(DOZE_POWERSAVE_PROPERTY, mDozePowersave.isChecked() ? "true" : "false");
            pokeSystemProperties();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        static class SystemPropPoker extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                String[] services;
                try {
                    services = ServiceManager.listServices();
                } catch (RemoteException e) {
                    return null;
                }
                for (String service : services) {
                    IBinder obj = ServiceManager.checkService(service);
                    if (obj != null) {
                        Parcel data = Parcel.obtain();
                        try {
                            obj.transact(IBinder.SYSPROPS_TRANSACTION, data, null, 0);
                        } catch (RemoteException e) {
                        } catch (Exception e) {
                            Log.i(TAG, "Someone wrote a bad service '" + service
                                    + "' that doesn't like to be poked: " + e);
                        }
                        data.recycle();
                    }
                }
                return null;
            }
        }
    }
}