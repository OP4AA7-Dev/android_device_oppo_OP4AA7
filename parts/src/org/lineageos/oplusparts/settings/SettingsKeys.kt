/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.settings

/**
 * Single source of truth for every [android.provider.Settings.Secure] key
 * used by OPlusParts. Keep the Kotlin side referencing these constants only;
 * static preference XML still has to spell out the matching string literal
 * for `android:key` (Android XML cannot reference a Kotlin `const val`).
 */
object SettingsKeys {
    /** DC dimming master toggle. */
    const val DC_DIMMING_ENABLED = "dc_dimming_enabled"
}
