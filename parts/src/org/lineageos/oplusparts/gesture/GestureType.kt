/*
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture

import org.lineageos.oplusparts.R

/**
 * Mapping between the kernel gesture_type (first field of
 * /proc/touchpanel/coordinate, see touchpanel_common.h) and a per-gesture
 * Settings.Secure key plus its default action.
 *
 * Kernel values 1..13 are reported by the S3706 firmware for the gestures
 * armed by the 0xef mask. Single-tap (16) / heart (17) are not armed on this
 * device and intentionally omitted.
 */
enum class GestureType(
    val gestureCode: Int,
    val secureKey: String,
    val defaultAction: String,
    val titleRes: Int,
) {
    DOUBLE_TAP(1, "oppo_gesture_double_tap", ActionConstants.ACTION_WAKE_DEVICE, R.string.gesture_double_tap),
    UP_VEE(2, "oppo_gesture_up_vee", ActionConstants.ACTION_NULL, R.string.gesture_up_vee),
    DOWN_VEE(3, "oppo_gesture_down_vee", ActionConstants.ACTION_NULL, R.string.gesture_down_vee),
    LEFT_VEE(4, "oppo_gesture_left_vee", ActionConstants.ACTION_NULL, R.string.gesture_left_vee),
    RIGHT_VEE(5, "oppo_gesture_right_vee", ActionConstants.ACTION_NULL, R.string.gesture_right_vee),
    CIRCLE(6, "oppo_gesture_circle", ActionConstants.ACTION_TORCH, R.string.gesture_circle),
    TWO_SWIPE(7, "oppo_gesture_two_swipe", ActionConstants.ACTION_NULL, R.string.gesture_two_swipe),
    SWIPE_LEFT_TO_RIGHT(8, "oppo_gesture_swipe_lr", ActionConstants.ACTION_MEDIA_NEXT, R.string.gesture_swipe_left_to_right),
    SWIPE_RIGHT_TO_LEFT(9, "oppo_gesture_swipe_rl", ActionConstants.ACTION_MEDIA_PREVIOUS, R.string.gesture_swipe_right_to_left),
    SWIPE_UP_TO_DOWN(10, "oppo_gesture_swipe_ud", ActionConstants.ACTION_NULL, R.string.gesture_swipe_up_to_down),
    SWIPE_DOWN_TO_UP(11, "oppo_gesture_swipe_du", ActionConstants.ACTION_NULL, R.string.gesture_swipe_down_to_up),
    GESTURE_M(12, "oppo_gesture_m", ActionConstants.ACTION_MEDIA_PLAY_PAUSE, R.string.gesture_m),
    GESTURE_W(13, "oppo_gesture_w", ActionConstants.ACTION_MEDIA_NEXT, R.string.gesture_w);

    companion object {
        fun fromCode(code: Int): GestureType? = entries.firstOrNull { it.gestureCode == code }
    }
}
