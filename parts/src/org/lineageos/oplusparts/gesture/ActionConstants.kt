/*
 * Copyright (C) 2014 SlimRoms Project
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture

/**
 * Opaque action tokens stored in Settings.Secure per gesture.
 * "Custom" app shortcuts are stored as their Intent URI (not starting with **).
 */
object ActionConstants {
    const val ACTION_NULL = "**null**"
    const val ACTION_WAKE_DEVICE = "**wake_device**"
    const val ACTION_TORCH = "**torch**"
    const val ACTION_CAMERA = "**camera**"
    const val ACTION_MEDIA_PREVIOUS = "**media_previous**"
    const val ACTION_MEDIA_NEXT = "**media_next**"
    const val ACTION_MEDIA_PLAY_PAUSE = "**media_play_pause**"
    const val ACTION_VIB_SILENT = "**ring_vib_silent**"
    const val ACTION_VOLUME_UP = "**vol_up**"
    const val ACTION_VOLUME_DOWN = "**vol_down**"
    const val ACTION_ASSIST = "**assist**"
    const val ACTION_AMBIENT_DISPLAY = "**ambient**"
}
