/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.dc

import android.content.Context
import org.lineageos.oplusparts.settings.SecureSettings
import org.lineageos.oplusparts.settings.SettingsKeys
import org.lineageos.oplusparts.util.FileUtils

/**
 * Sysfs helpers for Oppo DC dimming (data-dimming).
 *
 * DC dimming keeps the panel backlight at a fixed high level (no PWM flicker)
 * while the kernel dynamically scales panel SEED (colour calibration) DSI
 * commands to reduce perceived brightness. The result is a flicker-free
 * display at lower brightness levels.
 *
 * Only the master toggle is writable from userspace here:
 * - dimlayer_bl_en : R/W, master toggle ("1"/"0"). Granted to system via
 *                    init.qcom.rc (chmod 0666).
 *
 * The dim_alpha / dimlayer_set_bl / dim_dc_alpha nodes are NOT used: the
 * device's init scripts never grant write permission on dim_alpha /
 * dimlayer_set_bl (kernel default 0644, root only), so they are not writable
 * by the system-uid OPlusParts app and any write would silently fail. The
 * kernel's built-in brightness→alpha LUT is therefore left to drive dimming
 * strength automatically.
 *
 * Node reference (see android_kernel_oppo_sdm710 /
 * drivers/gpu/drm/msm/dsi-staging/oppo_display_private_api.c).
 */
object DCDimmingManager {
    /** Master DC dimming toggle. "1" = on, "0" = off. */
    const val NODE_BL_EN = "/sys/kernel/oppo_display/dimlayer_bl_en"

    fun isEnabled(context: Context): Boolean =
        SecureSettings.from(context).getBoolean(SettingsKeys.DC_DIMMING_ENABLED, false)

    /** Persist [enabled] and apply it to the kernel node. */
    fun setEnabled(context: Context, enabled: Boolean) {
        SecureSettings.from(context).putBoolean(SettingsKeys.DC_DIMMING_ENABLED, enabled)
        applyEnabled(enabled)
    }

    /** Apply [enabled] to the kernel node only (no persistence). */
    fun applyEnabled(enabled: Boolean): Boolean =
        FileUtils.writeValue(NODE_BL_EN, if (enabled) "1" else "0")

    /** Re-apply the persisted state to the kernel node (e.g. after boot). */
    fun restore(context: Context) {
        applyEnabled(isEnabled(context))
    }
}
