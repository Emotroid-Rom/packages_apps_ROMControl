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

package com.aokp.romcontrol.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import cyanogenmod.providers.CMSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aokp.romcontrol.R;
import com.aokp.romcontrol.widgets.SeekBarPreferenceCham;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class NotificationsDrawerFragment extends Fragment {

    public NotificationsDrawerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.container, new NotificationsDrawerSettingsPreferenceFragment())
                .commit();
    }

    public static class NotificationsDrawerSettingsPreferenceFragment extends PreferenceFragment
            implements OnPreferenceChangeListener {

        public NotificationsDrawerSettingsPreferenceFragment() {

        }

        private static final String TAG = "NotificationsDrawer";

        private static final String STATUS_BAR_QUICK_QS_PULLDOWN = "qs_quick_pulldown";

        private static final String PREF_QS_TRANSPARENT_SHADE = "qs_transparent_shade";

        private static final String PREF_QS_TRANSPARENT_HEADER = "qs_transparent_header";

        private static final String PREF_CLEAR_ALL_ICON_COLOR =
                "notification_drawer_clear_all_icon_color";

        private static final int WHITE = 0xffffffff;
        private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

        private static final int MENU_RESET = Menu.FIRST;
        private static final int DLG_RESET = 0;

        private ListPreference mNumColumns;

        private ListPreference mNumRows;

        private ListPreference mQuickPulldown;

        private SeekBarPreferenceCham mQSShadeAlpha;

        private SeekBarPreferenceCham mQSHeaderAlpha;

        private ColorPickerPreference mClearAllIconColor;

        private ContentResolver mResolver;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            createCustomView();
        }

        private PreferenceScreen createCustomView() {
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragment_notificationsdrawer_settings);
            mResolver = getActivity().getContentResolver();
            PreferenceScreen prefSet = getPreferenceScreen();

            // Number of QS Columns 3,4,5
            mNumColumns = (ListPreference) findPreference("sysui_qs_num_columns");
            int numColumns = Settings.System.getIntForUser(mResolver,
                    Settings.System.QS_NUM_TILE_COLUMNS, getDefaultNumColumns(),
                    UserHandle.USER_CURRENT);
            mNumColumns.setValue(String.valueOf(numColumns));
            updateNumColumnsSummary(numColumns);
            mNumColumns.setOnPreferenceChangeListener(this);

            // Number of QS Rows 3,4
            mNumRows = (ListPreference) findPreference("sysui_qs_num_rows");
            int numRows = Settings.System.getIntForUser(mResolver,
                    Settings.System.QS_NUM_TILE_ROWS, getDefaultNumRows(),
                    UserHandle.USER_CURRENT);
            mNumRows.setValue(String.valueOf(numRows));
            updateNumRowsSummary(numRows);
            mNumRows.setOnPreferenceChangeListener(this);

            // QS shade alpha
            mQSShadeAlpha =
                    (SeekBarPreferenceCham) prefSet.findPreference(PREF_QS_TRANSPARENT_SHADE);
            int qSShadeAlpha = Settings.System.getInt(mResolver,
                    Settings.System.QS_TRANSPARENT_SHADE, 255);
            mQSShadeAlpha.setValue(qSShadeAlpha / 1);
            mQSShadeAlpha.setOnPreferenceChangeListener(this);

            // QS header alpha
            mQSHeaderAlpha =
                    (SeekBarPreferenceCham) prefSet.findPreference(PREF_QS_TRANSPARENT_HEADER);
            int qSHeaderAlpha = Settings.System.getInt(mResolver,
                    Settings.System.QS_TRANSPARENT_HEADER, 255);
            mQSHeaderAlpha.setValue(qSHeaderAlpha / 1);
            mQSHeaderAlpha.setOnPreferenceChangeListener(this);

            mQuickPulldown = (ListPreference) findPreference(STATUS_BAR_QUICK_QS_PULLDOWN);

            int quickPulldown = CMSettings.System.getInt(mResolver,
                    CMSettings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 1);
            mQuickPulldown.setValue(String.valueOf(quickPulldown));
            updatePulldownSummary(quickPulldown);
            mQuickPulldown.setOnPreferenceChangeListener(this);

            int intColor;
            String hexColor;

            mClearAllIconColor =
                    (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_ICON_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.NOTIFICATION_DRAWER_CLEAR_ALL_ICON_COLOR, WHITE);
            mClearAllIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mClearAllIconColor.setSummary(hexColor);
            mClearAllIconColor.setOnPreferenceChangeListener(this);

            setHasOptionsMenu(true);
            return prefSet;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            menu.add(0, MENU_RESET, 0, R.string.reset)
                    .setIcon(R.drawable.ic_settings_reset)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case MENU_RESET:
                    showDialogInner(DLG_RESET);
                    return true;
                 default:
                    return super.onContextItemSelected(item);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean value;
            String hex;
            int intHex;

            if (preference == mQuickPulldown) {
                int quickPulldown = Integer.valueOf((String) newValue);
                CMSettings.System.putInt(
                        mResolver, CMSettings.System.STATUS_BAR_QUICK_QS_PULLDOWN, quickPulldown);
                updatePulldownSummary(quickPulldown);
                return true;
            } else if (preference == mClearAllIconColor) {
                hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
                intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(mResolver,
                    Settings.System.NOTIFICATION_DRAWER_CLEAR_ALL_ICON_COLOR, intHex);
                preference.setSummary(hex);
                return true;
            } else if (preference == mNumColumns) {
                int numColumns = Integer.valueOf((String) newValue);
                Settings.System.putIntForUser(mResolver, Settings.System.QS_NUM_TILE_COLUMNS,
                        numColumns, UserHandle.USER_CURRENT);
                updateNumColumnsSummary(numColumns);
                return true;
            } else if (preference == mNumRows) {
                int numRows = Integer.valueOf((String) newValue);
                Settings.System.putIntForUser(mResolver, Settings.System.QS_NUM_TILE_ROWS,
                        numRows, UserHandle.USER_CURRENT);
                updateNumRowsSummary(numRows);
                return true;
            } else if (preference == mQSShadeAlpha) {
                int alpha = (Integer) newValue;
                Settings.System.putInt(mResolver,
                        Settings.System.QS_TRANSPARENT_SHADE, alpha * 1);
                return true;
            } else if (preference == mQSHeaderAlpha) {
                int alpha = (Integer) newValue;
                Settings.System.putInt(mResolver,
                        Settings.System.QS_TRANSPARENT_HEADER, alpha * 1);
                return true;             
            }
            return false;
        }

        private void showDialogInner(int id) {
            DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
            newFragment.setTargetFragment(this, 0);
            newFragment.show(getFragmentManager(), "dialog " + id);
        }

        public static class MyAlertDialogFragment extends DialogFragment {

            public static MyAlertDialogFragment newInstance(int id) {
                MyAlertDialogFragment frag = new MyAlertDialogFragment();
                    Bundle args = new Bundle();
                args.putInt("id", id);
                frag.setArguments(args);
                return frag;
            }

            NotificationsDrawerFragment.NotificationsDrawerSettingsPreferenceFragment getOwner() {
                return (NotificationsDrawerFragment.NotificationsDrawerSettingsPreferenceFragment) getTargetFragment();
            }

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                int id = getArguments().getInt("id");
                switch (id) {
                    case DLG_RESET:
                        return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.reset)
                        .setMessage(R.string.dlg_reset_values_message)
                        .setNegativeButton(R.string.cancel, null)
                        .setNeutralButton(R.string.dlg_reset_android,
                            new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Settings.System.putInt(getOwner().mResolver,
                                        Settings.System.NOTIFICATION_DRAWER_CLEAR_ALL_ICON_COLOR,
                                        WHITE);
                                getOwner().createCustomView();
                        }
                        })
                        .setPositiveButton(R.string.dlg_reset_aokp,
                            new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Settings.System.putInt(getOwner().mResolver,
                                        Settings.System.NOTIFICATION_DRAWER_CLEAR_ALL_ICON_COLOR,
                                        HOLO_BLUE_LIGHT);
                                getOwner().createCustomView();
                            }
                        })
                        .create();
                }
                throw new IllegalArgumentException("unknown id " + id);
            }

            @Override
            public void onCancel(DialogInterface dialog) {

            }
        }

        private void updatePulldownSummary(int value) {
            Resources res = getResources();

            if (value == 0) {
                // quick pulldown deactivated
                mQuickPulldown.setSummary(res.getString(R.string.status_bar_quick_qs_pulldown_off));
            } else {
                String direction = res.getString(value == 2
                        ? R.string.status_bar_quick_qs_pulldown_summary_left
                        : R.string.status_bar_quick_qs_pulldown_summary_right);
                mQuickPulldown.setSummary(res.getString(R.string.status_bar_quick_qs_pulldown_summary, direction));
            }
        }

        private void updateNumColumnsSummary(int numColumns) {
            String prefix = (String) mNumColumns.getEntries()[mNumColumns.findIndexOfValue(String
                    .valueOf(numColumns))];
            mNumColumns.setSummary(getResources().getString(R.string.qs_num_columns_showing, prefix));
        }

        private void updateNumRowsSummary(int numRows) {
            String prefix = (String) mNumRows.getEntries()[mNumRows.findIndexOfValue(String
                    .valueOf(numRows))];
            mNumRows.setSummary(getResources().getString(R.string.qs_num_rows_showing, prefix));
        }

        private int getDefaultNumColumns() {
            try {
                Resources res = getActivity().getPackageManager()
                        .getResourcesForApplication("com.android.systemui");
                int val = res.getInteger(res.getIdentifier("quick_settings_num_columns", "integer",
                        "com.android.systemui")); // better not be larger than 5, that's as high as the
                                                  // list goes atm
                return Math.max(1, val);
            } catch (Exception e) {
                return 3;
            }
        }

        private int getDefaultNumRows() {
            try {
                Resources res = getActivity().getPackageManager()
                        .getResourcesForApplication("com.android.systemui");
                int val = res.getInteger(res.getIdentifier("quick_settings_num_rows", "integer",
                        "com.android.systemui")); // better not be larger than 4, that's as high as the
                                                  // list goes atm
                return Math.max(1, val);
            } catch (Exception e) {
                return 3;
            }
        }
    }
}
