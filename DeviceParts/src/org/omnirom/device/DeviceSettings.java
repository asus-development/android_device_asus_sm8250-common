/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.preference.PreferenceFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import androidx.preference.SwitchPreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String KEY_SETTINGS_PREFIX = "device_setting_";
    public static final String KEY_GLOVE_SWITCH = "glove";

    private static final String KEY_CATEGORY_GAMING = "category_gaming";
    private static final String KEY_CATEGORY_SCREEN = "screen";
    public static final String KEY_GAME_GENIE = "game_toolbar_app";

    private static final boolean sIsRog3 = android.os.Build.MODEL.equals("ASUS_I003D");
    private static final String ACTION_AIR_TRIGGER_OFF = "com.asus.airtriggers.SYSTEMUI_AIR_TRIGGER_OFF";
    private static final String ACTION_AIR_TRIGGER_ON = "com.asus.airtriggers.SYSTEMUI_AIR_TRIGGER_ON";
    private static final String AIRTRIGGER_PACKAGE_NAME = "com.asus.airtriggers";
    private static final String FIELD_AIR_TRIGGER_ENABLE = "air_trigger_enable";
    public static final String KEY_AIRTRIGGER = "grip_sensor_apk";
    public static final String KEY_AIRTRIGGER_PREF = "grip_sensor_pref";
    private static final String TAG = "AirTriggerApkPreferenceController";

    private Airtrigger mAirtrigger;
    private static TwoStatePreference mGloveModeSwitch;
    private static Preference mAirtriggerPref;
    private static Preference mGameCategory;
    private static Preference mGameGenie;
    private static SwitchPreference mGripSensorPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main, rootKey);

        mGloveModeSwitch = (TwoStatePreference) findPreference(KEY_GLOVE_SWITCH);
        mGloveModeSwitch.setEnabled(GloveModeSwitch.isSupported());
        mGloveModeSwitch.setChecked(GloveModeSwitch.isCurrentlyEnabled(this.getContext()));
        mGloveModeSwitch.setOnPreferenceChangeListener(new GloveModeSwitch(getContext()));

        mAirtriggerPref = findPreference(KEY_AIRTRIGGER_PREF);
        mGameCategory = findPreference(KEY_CATEGORY_GAMING);
        
        mGripSensorPreference = (SwitchPreference) findPreference(KEY_AIRTRIGGER);
        mGripSensorPreference.setChecked(Settings.Global.getInt(getContext().getContentResolver(),
        FIELD_AIR_TRIGGER_ENABLE, 0) == 1);
        mGripSensorPreference.setOnPreferenceChangeListener(this);
        if (!sIsRog3) {
            getPreferenceScreen().removePreference(mGameCategory);
        }

        mGameGenie = findPreference(KEY_GAME_GENIE);
        mGameGenie.setEnabled(GameGenie.isGameGenieExist(this.getContext()));

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mAirtriggerPref) {
            mAirtrigger.startAirTriggerSettings(this.getContext());
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mGripSensorPreference) {
            notifySwitchState(((Boolean) newValue).booleanValue());
        }
        return true;
    }

    private void notifySwitchState(boolean z) {
        Log.d(TAG, "notifySwitchState enabled=" + z);
        Intent intent = new Intent();
        intent.setAction(z ? ACTION_AIR_TRIGGER_ON : ACTION_AIR_TRIGGER_OFF);
        intent.setPackage(AIRTRIGGER_PACKAGE_NAME);
        getContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }
}
