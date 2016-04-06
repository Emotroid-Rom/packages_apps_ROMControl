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
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.os.Bundle;
import android.util.Log;
import java.io.DataOutputStream;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.CheckBox;

import com.aokp.romcontrol.R;

public class DisplayMDNIEActivity extends Activity {

    public static final String TAG = "DisplayMDNIEActivity";
    private CheckBox checkBox;
    private RadioGroup radioGroup;
    
    public void AutoMode(final View view) {
        this.RunAsRoot(new String[] { "echo 4 > /sys/class/mdnie/mdnie/mode" });
        final SharedPreferences.Editor edit = this.getSharedPreferences("com.emotion.romcontrol", 0).edit();
        edit.putInt("Mode", 4);
        edit.commit();
    }
    
    public void DynamicMode(final View view) {
        this.RunAsRoot(new String[] { "echo 0 > /sys/class/mdnie/mdnie/mode" });
        final SharedPreferences.Editor edit = this.getSharedPreferences("com.emotion.romcontrol", 0).edit();
        edit.putInt("Mode", 0);
        edit.commit();
    }
    
    public void FlipHBMoff(final View view) {
        this.RunAsRoot(new String[] { "echo 0 > /sys/class/backlight/panel/auto_brightness" });
    }
    
    public void FlipHBMon(final View view) {
        this.RunAsRoot(new String[] { "echo 6 > /sys/class/backlight/panel/auto_brightness" });
    }
    
    public void MovieMode(final View view) {
        this.RunAsRoot(new String[] { "echo 3 > /sys/class/mdnie/mdnie/mode" });
        final SharedPreferences.Editor edit = this.getSharedPreferences("com.emotion.romcontrol", 0).edit();
        edit.putInt("Mode", 3);
        edit.commit();
    }
    
    public void NaturalMode(final View view) {
        this.RunAsRoot(new String[] { "echo 2 > /sys/class/mdnie/mdnie/mode" });
        final SharedPreferences.Editor edit = this.getSharedPreferences("com.emotion.romcontrol", 0).edit();
        edit.putInt("Mode", 2);
        edit.commit();
    }
    
    public void RunAsRoot(final String[] array) {
        try {
            final DataOutputStream dataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
            for (int length = array.length, i = 0; i < length; ++i) {
                dataOutputStream.writeBytes(array[i] + "\n");
            }
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
        }
        catch (Exception ex) {
            Log.e("ROOT", "Error executing root action", (Throwable)ex);
        }
    }
    
    public void SetAtBootClick(final View view) {
        if (((CheckBox)this.findViewById(R.id.checkBox)).isChecked()) {
            final SharedPreferences.Editor edit = this.getSharedPreferences("com.emotion.romcontrol", 0).edit();
            edit.putInt("SetAtBoot", 1);
            edit.commit();
            return;
        }
        final SharedPreferences.Editor edit2 = this.getSharedPreferences("com.emotion.romcontrol", 0).edit();
        edit2.putInt("SetAtBoot", 0);
        edit2.commit();
    }
    
    public void StandardMode(final View view) {
        this.RunAsRoot(new String[] { "echo 1 > /sys/class/mdnie/mdnie/mode" });
        final SharedPreferences.Editor edit = this.getSharedPreferences("com.emotion.romcontrol", 0).edit();
        edit.putInt("Mode", 1);
        edit.commit();
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.display_mdnie);
        final SharedPreferences sharedPreferences = this.getSharedPreferences("com.emotion.romcontrol", 0);
        final int int1 = sharedPreferences.getInt("Mode", -1);
        final int int2 = sharedPreferences.getInt("SetAtBoot", -1);
        checkBox = (CheckBox) this.findViewById(R.id.checkBox);
        if (int2 == 1) {
            this.checkBox.setChecked(true);
        }
        radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup);
        if (int1 == 0) {
            this.radioGroup.check(R.id.radioButton);
        }
        if (int1 == 1 || int1 == -1) {
            this.radioGroup.check(R.id.radioButton2);
        }
        if (int1 == 2) {
            this.radioGroup.check(R.id.radioButton3);
        }
        if (int1 == 3) {
            this.radioGroup.check(R.id.radioButton4);
        }
        if (int1 == 4) {
            this.radioGroup.check(R.id.radioButton5);
        }
    }
}

