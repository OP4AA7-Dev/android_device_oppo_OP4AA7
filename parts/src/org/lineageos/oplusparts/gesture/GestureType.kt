/*
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture

import org.lineageos.oplusparts.R
import org.lineageos.oplusparts.gesture.action.ActionTokens
import org.lineageos.oplusparts.settings.SettingsKeys

/**
 * Mapping between the kernel gesture_type (first field of
 * /proc/touchpanel/coordinate, see touchpanel_common.h) and a per-gesture
 * Settings.Secure key plus its default action.
 *
 * Kernel values 1..13 are reported by the S3706 firmware for the gestures
 * armed by the 0xef mask. Single-tap (16) / heart (17) are not armed on this
 * device and intentionally omitted.
 *
 * This enum is the single source of truth for the gesture list; the gestures
 * preference page builds its entries dynamically from [entries] (see
 * [org.lineageos.oplusparts.gesture.ScreenOffGesturesFragment]).
 */
enum class GestureType(
    val gestureCode: Int,
    val secureKey: String,
    val defaultAction: String,
    val titleRes: Int,
) {
    DOUBLE_TAP(1, SettingsKeys.GestureAction.DOUBLE_TAP, ActionTokens.WAKE_DEVICE, R.string.gesture_double_tap),
    UP_VEE(2, SettingsKeys.GestureAction.UP_VEE, ActionTokens.NONE, R.string.gesture_up_vee),
    DOWN_VEE(3, SettingsKeys.GestureAction.DOWN_VEE, ActionTokens.NONE, R.string.gesture_down_vee),
    LEFT_VEE(4, SettingsKeys.GestureAction.LEFT_VEE, ActionTokens.NONE, R.string.gesture_left_vee),
    RIGHT_VEE(5, SettingsKeys.GestureAction.RIGHT_VEE, ActionTokens.NONE, R.string.gesture_right_vee),
    CIRCLE(6, SettingsKeys.GestureAction.CIRCLE, ActionTokens.TORCH, R.string.gesture_circle),
    TWO_SWIPE(7, SettingsKeys.GestureAction.TWO_SWIPE, ActionTokens.NONE, R.string.gesture_two_swipe),
    SWIPE_LEFT_TO_RIGHT(8, SettingsKeys.GestureAction.SWIPE_LEFT_TO_RIGHT, ActionTokens.MEDIA_NEXT, R.string.gesture_swipe_left_to_right),
    SWIPE_RIGHT_TO_LEFT(9, SettingsKeys.GestureAction.SWIPE_RIGHT_TO_LEFT, ActionTokens.MEDIA_PREVIOUS, R.string.gesture_swipe_right_to_left),
    SWIPE_UP_TO_DOWN(10, SettingsKeys.GestureAction.SWIPE_UP_TO_DOWN, ActionTokens.NONE, R.string.gesture_swipe_up_to_down),
    SWIPE_DOWN_TO_UP(11, SettingsKeys.GestureAction.SWIPE_DOWN_TO_UP, ActionTokens.NONE, R.string.gesture_swipe_down_to_up),
    GESTURE_M(12, SettingsKeys.GestureAction.GESTURE_M, ActionTokens.MEDIA_PLAY_PAUSE, R.string.gesture_m),
    GESTURE_W(13, SettingsKeys.GestureAction.GESTURE_W, ActionTokens.MEDIA_NEXT, R.string.gesture_w);

    companion object {
        fun fromCode(code: Int): GestureType? = entries.firstOrNull { it.gestureCode == code }
    }
}
