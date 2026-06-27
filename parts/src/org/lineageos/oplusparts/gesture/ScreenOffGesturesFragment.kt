/*
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture

import android.os.Bundle
import android.provider.Settings
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragment
import androidx.preference.SwitchPreference
import org.lineageos.oplusparts.KeyHandler
import org.lineageos.oplusparts.R

/**
 * Settings page for screen-off gestures.
 *
 * - Master switch is persisted in Settings.Secure (read by KeyHandler) and
 *   mirrored to /proc/touchpanel/double_tap_enable (the firmware arm switch).
 * - Each gesture's action is persisted in Settings.Secure under its
 *   [GestureType.secureKey]; preferences are non-persistent (managed manually)
 *   because the backing store is Settings.Secure, not SharedPreferences.
 *
 * Uses the platform FragmentManager (PreferenceFragment in androidx-preference
 * 1.3 derives from android.app.Fragment), matching CollapsingToolbarBaseActivity.
 */
class ScreenOffGesturesFragment : PreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.screen_off_gestures)

        setupMasterSwitch()
        setupScreenOffUdfpsSwitch()
        GestureType.entries.forEach(::bindGesturePreference)
    }

    private fun setupMasterSwitch() {
        val sw = findPreference<SwitchPreference>(KeyHandler.SETTINGS_GESTURES_ENABLED)
            ?: return
        sw.isPersistent = false
        sw.isChecked = isMasterEnabled()
        sw.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            putSecure(
                KeyHandler.SETTINGS_GESTURES_ENABLED,
                if (enabled) "1" else "0"
            )
            Utils.writeValue(
                Utils.PROC_DOUBLE_TAP_ENABLE,
                if (enabled) "1" else "0"
            )
            true
        }
    }

    private fun setupScreenOffUdfpsSwitch() {
        val sw = findPreference<SwitchPreference>(SETTINGS_SCREEN_OFF_UDFPS_ENABLED)
            ?: return
        sw.isPersistent = false
        sw.isChecked = isScreenOffUdfpsEnabled()
        sw.setOnPreferenceChangeListener { _, newValue ->
            putSecure(
                SETTINGS_SCREEN_OFF_UDFPS_ENABLED,
                if (newValue as Boolean) "1" else "0"
            )
            true
        }
    }

    private fun bindGesturePreference(g: GestureType) {
        val pref = findPreference<ListPreference>(g.secureKey) ?: return
        pref.isPersistent = false
        pref.title = getString(g.titleRes)
        val value = Settings.Secure.getString(
            activity.contentResolver, g.secureKey
        ) ?: g.defaultAction
        pref.value = value
        pref.summary = pref.entryFor(value)
        pref.setOnPreferenceChangeListener { p, newValue ->
            val v = newValue as String
            putSecure(g.secureKey, v)
            p.summary = pref.entryFor(v)
            true
        }
    }

    private fun isMasterEnabled(): Boolean = try {
        Settings.Secure.getInt(
            activity.contentResolver,
            KeyHandler.SETTINGS_GESTURES_ENABLED, 0
        ) != 0
    } catch (e: Exception) {
        false
    }

    private fun isScreenOffUdfpsEnabled(): Boolean = try {
        Settings.Secure.getInt(
            activity.contentResolver,
            SETTINGS_SCREEN_OFF_UDFPS_ENABLED, 0
        ) != 0
    } catch (e: Exception) {
        false
    }

    private fun putSecure(key: String, value: String) {
        try {
            Settings.Secure.putString(activity.contentResolver, key, value)
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun ListPreference.entryFor(value: String): String {
        val idx = findIndexOfValue(value)
        return if (idx >= 0) entries[idx].toString() else value
    }

    companion object {
        const val SETTINGS_SCREEN_OFF_UDFPS_ENABLED = "screen_off_udfps_enabled"
    }
}
