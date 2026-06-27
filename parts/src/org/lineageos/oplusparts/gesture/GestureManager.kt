/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture

import android.content.Context
import org.lineageos.oplusparts.settings.SecureSettings
import org.lineageos.oplusparts.settings.SettingsKeys

/**
 * Owns the screen-off gesture master toggle: its persistence in
 * [Settings.Secure] ([SettingsKeys.GESTURES_ENABLED]) and the firmware arm
 * switch at [Utils.PROC_DOUBLE_TAP_ENABLE].
 *
 * Centralising this here means [org.lineageos.oplusparts.KeyHandler],
 * [ScreenOffFeaturesFragment] and [org.lineageos.oplusparts.BootReceiver]
 * no longer each hand-write the same read/write/restore logic.
 */
object GestureManager {

    fun isEnabled(context: Context): Boolean =
        SecureSettings.from(context).getBoolean(SettingsKeys.GESTURES_ENABLED, false)

    /** Persist [enabled] and apply it to the firmware. */
    fun setEnabled(context: Context, enabled: Boolean) {
        SecureSettings.from(context).putBoolean(SettingsKeys.GESTURES_ENABLED, enabled)
        applyEnabled(enabled)
    }

    /** Apply [enabled] to the firmware arm switch only (no persistence). */
    fun applyEnabled(enabled: Boolean): Boolean =
        Utils.writeValue(Utils.PROC_DOUBLE_TAP_ENABLE, if (enabled) "1" else "0")

    /** Re-apply the persisted state to the firmware (e.g. after boot). */
    fun restore(context: Context) {
        applyEnabled(isEnabled(context))
    }
}
