/*
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import org.lineageos.oplusparts.R
import org.lineageos.oplusparts.gesture.action.ActionTokens
import org.lineageos.oplusparts.settings.SecureSettings

/**
 * Settings page for screen-off gesture assignments.
 *
 * The master toggle lives on the parent "screen-off features" page
 * ([org.lineageos.oplusparts.settings.SettingsKeys.GESTURES_ENABLED]); this
 * page only exposes the per-gesture action assignments. Each gesture's action
 * is persisted in Settings.Secure under its [GestureType.secureKey];
 * preferences are non-persistent (managed manually) because the backing store
 * is Settings.Secure, not SharedPreferences.
 *
 * The gesture [ListPreference]s are built dynamically from [GestureType.entries]
 * (the single source of truth for the gesture list) using the action choices
 * from res/values/arrays.xml. The gesture list is hidden when the master
 * toggle is off, since the assignments have no effect then.
 */
class ScreenOffGesturesFragment : PreferenceFragmentCompat() {

    private lateinit var actionEntries: Array<CharSequence>
    private lateinit var actionValues: Array<CharSequence>

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.screen_off_gestures, rootKey)

        // getTextArray yields Array<CharSequence>, which matches ListPreference's
        // entries/entryValues setter types (arrays are invariant in Kotlin, so a
        // String[] from getStringArray would not assign directly).
        actionEntries = resources.getTextArray(R.array.gesture_action_entries)
        actionValues = resources.getTextArray(R.array.gesture_action_values)

        val category = findPreference<PreferenceCategory>("gestures_category") ?: return
        category.removeAll()
        GestureType.entries.forEach { category.addPreference(createGesturePreference(it)) }

        applyGesturesVisible(GestureManager.isEnabled(requireContext()))
    }

    private fun createGesturePreference(gesture: GestureType): ListPreference {
        val secure = SecureSettings.from(requireContext())
        return ListPreference(requireContext()).apply {
            key = gesture.secureKey
            isPersistent = false
            title = getString(gesture.titleRes)
            entries = actionEntries
            entryValues = actionValues
            val value = loadGestureAction(secure, gesture)
            this.value = value
            summary = entryFor(value)
            setOnPreferenceChangeListener { pref, newValue ->
                val v = newValue as String
                if (v in actionValues) {
                    secure.putString(gesture.secureKey, v)
                    pref.summary = entryFor(v)
                }
                true
            }
        }
    }

    /**
     * Read the persisted action for [gesture], falling back to its default
     * when unset or when the stored value is no longer a supported token
     * (stale value from an older build). The stale value is not rewritten,
     * so no implicit data mutation happens just by opening the page.
     */
    private fun loadGestureAction(secure: SecureSettings, gesture: GestureType): String {
        val stored = secure.getString(gesture.secureKey, null)
        return if (stored != null && stored in ActionTokens.SUPPORTED) stored else gesture.defaultAction
    }

    /** Show / hide the gesture assignment list based on the master toggle. */
    private fun applyGesturesVisible(visible: Boolean) {
        findPreference<PreferenceCategory>("gestures_category")?.isVisible = visible
    }

    private fun ListPreference.entryFor(value: String): String {
        val idx = findIndexOfValue(value)
        return if (idx >= 0) entries[idx].toString() else value
    }
}
