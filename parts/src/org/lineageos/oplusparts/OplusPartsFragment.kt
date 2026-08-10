/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import org.lineageos.oplusparts.dc.DCDimmingManager
import org.lineageos.oplusparts.settings.SettingsKeys

/**
 * Top-level "OPPO features" landing page.
 *
 * - DC dimming: an inline master toggle (no sub-page) that writes the
 *   `dimlayer_bl_en` sysfs node via [DCDimmingManager] and persists its state
 *   in Settings.Secure. Re-applied at boot by [org.lineageos.oplusparts.BootReceiver].
 */
class OplusPartsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.oplus_parts, rootKey)
        setupDcDimmingSwitch()
    }

    private fun setupDcDimmingSwitch() {
        val sw = findPreference<SwitchPreference>(SettingsKeys.DC_DIMMING_ENABLED) ?: return
        sw.isPersistent = false
        sw.isChecked = DCDimmingManager.isEnabled(requireContext())
        sw.setOnPreferenceChangeListener { _, newValue ->
            DCDimmingManager.setEnabled(requireContext(), newValue as Boolean)
            true
        }
    }
}
