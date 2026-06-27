/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import org.lineageos.oplusparts.R
import org.lineageos.oplusparts.settings.SecureSettings
import org.lineageos.oplusparts.settings.SettingsKeys

/**
 * "Screen-off features" landing page.
 *
 * - Master toggle ([SettingsKeys.GESTURES_ENABLED]): arms the touch firmware
 *   via [GestureManager] and gates both the gestures sub-page behaviour
 *   (KeyHandler ignores gestures when off) and the screen-off fingerprint toggle.
 * - Gestures: launches the gestures sub-page via an `<intent>`.
 * - Screen-off fingerprint ([SettingsKeys.SCREEN_OFF_UDFPS_ENABLED]): inline
 *   toggle, hidden while the master toggle is off (the master toggle is the
 *   single point of control).
 */
class ScreenOffFeaturesFragment : PreferenceFragmentCompat() {

    private var udfpsSwitch: SwitchPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.screen_off_features, rootKey)
        setupMasterSwitch()
        setupUdfpsSwitch()
        applyMasterState(GestureManager.isEnabled(requireContext()))
    }

    private fun setupMasterSwitch() {
        val sw = findPreference<SwitchPreference>(SettingsKeys.GESTURES_ENABLED) ?: return
        sw.isPersistent = false
        sw.isChecked = GestureManager.isEnabled(requireContext())
        sw.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            GestureManager.setEnabled(requireContext(), enabled)
            applyMasterState(enabled)
            true
        }
    }

    private fun setupUdfpsSwitch() {
        val sw = findPreference<SwitchPreference>(SettingsKeys.SCREEN_OFF_UDFPS_ENABLED) ?: return
        sw.isPersistent = false
        val secure = SecureSettings.from(requireContext())
        sw.isChecked = secure.getBoolean(SettingsKeys.SCREEN_OFF_UDFPS_ENABLED, false)
        sw.setOnPreferenceChangeListener { _, newValue ->
            secure.putBoolean(SettingsKeys.SCREEN_OFF_UDFPS_ENABLED, newValue as Boolean)
            true
        }
        udfpsSwitch = sw
    }

    /** Reflect the master toggle: the gestures entry and UDFPS toggle are
     *  hidden while off (the master toggle is the single point of control). */
    private fun applyMasterState(enabled: Boolean) {
        findPreference<Preference>(KEY_GESTURES_ENTRY)?.isVisible = enabled
        udfpsSwitch?.apply {
            isVisible = enabled
            if (!enabled) {
                // Turning the master off also disables fingerprint at runtime.
                isChecked = false
                SecureSettings.from(requireContext())
                    .putBoolean(SettingsKeys.SCREEN_OFF_UDFPS_ENABLED, false)
            }
        }
    }

    companion object {
        private const val KEY_GESTURES_ENTRY = "screen_off_gestures_entry"
    }
}
