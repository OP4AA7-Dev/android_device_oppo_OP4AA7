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
    /** Master toggle gating both screen-off gestures and screen-off fingerprint. */
    const val GESTURES_ENABLED = "oppo_gestures_enabled"

    /** Screen-off fingerprint (UDFPS) sub-toggle. */
    const val SCREEN_OFF_UDFPS_ENABLED = "screen_off_udfps_enabled"

    /** DC dimming master toggle. */
    const val DC_DIMMING_ENABLED = "dc_dimming_enabled"

    /** Per-gesture action assignment keys (value is an [org.lineageos.oplusparts.gesture.action.ActionTokens] string). */
    object GestureAction {
        const val DOUBLE_TAP = "oppo_gesture_double_tap"
        const val UP_VEE = "oppo_gesture_up_vee"
        const val DOWN_VEE = "oppo_gesture_down_vee"
        const val LEFT_VEE = "oppo_gesture_left_vee"
        const val RIGHT_VEE = "oppo_gesture_right_vee"
        const val CIRCLE = "oppo_gesture_circle"
        const val TWO_SWIPE = "oppo_gesture_two_swipe"
        const val SWIPE_LEFT_TO_RIGHT = "oppo_gesture_swipe_lr"
        const val SWIPE_RIGHT_TO_LEFT = "oppo_gesture_swipe_rl"
        const val SWIPE_UP_TO_DOWN = "oppo_gesture_swipe_ud"
        const val SWIPE_DOWN_TO_UP = "oppo_gesture_swipe_du"
        const val GESTURE_M = "oppo_gesture_m"
        const val GESTURE_W = "oppo_gesture_w"
    }
}
