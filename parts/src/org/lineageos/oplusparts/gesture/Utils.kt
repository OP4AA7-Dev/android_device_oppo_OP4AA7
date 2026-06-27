/*
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture

import org.lineageos.oplusparts.util.FileUtils

/**
 * Touchpanel gesture node paths and gesture-specific helpers. The generic
 * sysfs/proc IO lives in [org.lineageos.oplusparts.util.FileUtils].
 */
object Utils {
    const val PROC_DOUBLE_TAP_ENABLE = "/proc/touchpanel/double_tap_enable"
    const val PROC_COORDINATE = "/proc/touchpanel/coordinate"

    fun writeValue(path: String, value: String): Boolean = FileUtils.writeValue(path, value)

    /**
     * Read the gesture_type (first comma-separated field) from the coordinate node.
     * Returns -1 when the node can't be read or parsed.
     */
    fun readGestureType(): Int {
        val line = FileUtils.readLine(PROC_COORDINATE) ?: return -1
        val first = line.substringBefore(',')
        return first.toIntOrNull() ?: -1
    }
}
