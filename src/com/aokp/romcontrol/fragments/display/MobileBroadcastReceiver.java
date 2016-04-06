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

import android.content.SharedPreferences;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import java.io.DataOutputStream;
import android.content.BroadcastReceiver;

public class MobileBroadcastReceiver extends BroadcastReceiver
{
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
    
    public void onReceive(final Context context, final Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            final String[] array = { "echo Y > /sys/module/msm_thermal/parameters/enabled" };
            Log.e("MDNIETuner", "Enabled intellithermal !");
            this.RunAsRoot(array);
            final SharedPreferences sharedPreferences = context.getSharedPreferences("com.emotion.romcontrol", 0);
            if (sharedPreferences.getInt("SetAtBoot", -1) != 1) {
                Log.e("MDNIETuner", "NOT Setting at boot !");
                return;
            }
            Log.e("MDNIETuner", "Setting at boot is selected !");
            final int int1 = sharedPreferences.getInt("Mode", -1);
            final String[] array2 = { "echo 4 > /sys/class/mdnie/mdnie/mode" };
            if (int1 == 0) {
                array2[0] = "echo 0 > /sys/class/mdnie/mdnie/mode";
                Log.e("MDNIETuner", "Set mode to 0/Dynamic mode !");
            }
            if (int1 == 1) {
                array2[0] = "echo 1 > /sys/class/mdnie/mdnie/mode";
                Log.e("MDNIETuner", "Set mode to 1/Standard mode !");
            }
            if (int1 == 2) {
                array2[0] = "echo 2 > /sys/class/mdnie/mdnie/mode";
                Log.e("MDNIETuner", "Set mode to 2/Natural mode !");
            }
            if (int1 == 3) {
                array2[0] = "echo 3 > /sys/class/mdnie/mdnie/mode";
                Log.e("MDNIETuner", "Set mode to 3/Movie mode !");
            }
            if (int1 == 4) {
                array2[0] = "echo 4 > /sys/class/mdnie/mdnie/mode";
                Log.e("MDNIETuner", "Set mode to 4/Auto mode !");
            }
            this.RunAsRoot(array2);
        }
    }
}
