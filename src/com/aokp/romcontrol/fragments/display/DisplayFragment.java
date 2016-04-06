/*
* Copyright (C) 2016 Emotroid-Team
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.aokp.romcontrol.fragments.display;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.aokp.romcontrol.R;

public class DisplayFragment extends Fragment {

    public DisplayFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.container, new SettingsPreferenceFragment())
                .commit();
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment {

        private static final String TAG = "Display";

        private ContentResolver mContentResolver;

        private String PREF_DISPLAY_SETTINGS = "display_pref";

        private Preference mDisplaysettings;

        public SettingsPreferenceFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            createCustomView();
        }

        private PreferenceScreen createCustomView() {
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragment_display_settings);
            PreferenceScreen prefSet = getPreferenceScreen();
            Activity activity = getActivity();
            ContentResolver resolver = getActivity().getContentResolver();

            mDisplaysettings = prefSet.findPreference(PREF_DISPLAY_SETTINGS);

            return prefSet;
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                final Preference preference) {
            if (preference == mDisplaysettings) {
                Intent intent = new Intent(getActivity(), DisplayMDNIEActivity.class);
                getActivity().startActivity(intent);
            } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
            }
            return false;
        }

    }
}