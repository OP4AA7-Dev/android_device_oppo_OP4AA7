/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture.action

/**
 * Action tokens persisted under each gesture's Settings.Secure key.
 *
 * Only the eight actions exposed in the picker (res/values/arrays.xml
 * `gesture_action_values`) are supported. Stale tokens from older builds
 * (vol_up / vol_down / assist / ambient) are no longer executed.
 */
object ActionTokens {
    const val NONE = "**null**"
    const val WAKE_DEVICE = "**wake_device**"
    const val TORCH = "**torch**"
    const val CAMERA = "**camera**"
    const val MEDIA_PREVIOUS = "**media_previous**"
    const val MEDIA_NEXT = "**media_next**"
    const val MEDIA_PLAY_PAUSE = "**media_play_pause**"
    const val RING_VIB_SILENT = "**ring_vib_silent**"

    val SUPPORTED: Set<String> = setOf(
        NONE, WAKE_DEVICE, TORCH, CAMERA,
        MEDIA_PREVIOUS, MEDIA_NEXT, MEDIA_PLAY_PAUSE,
        RING_VIB_SILENT,
    )
}
