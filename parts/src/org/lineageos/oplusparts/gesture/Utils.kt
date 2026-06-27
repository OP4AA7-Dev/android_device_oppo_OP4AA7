/*
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture

import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

/**
 * Tiny sysfs/proc helpers for the touchpanel gesture nodes.
 */
object Utils {
    private const val TAG = "OPlusParts.Utils"

    const val PROC_DOUBLE_TAP_ENABLE = "/proc/touchpanel/double_tap_enable"
    const val PROC_COORDINATE = "/proc/touchpanel/coordinate"

    /** Read the first line of [path], null on failure. */
    fun readLine(path: String): String? {
        var line: String? = null
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader(path), 1024)
            line = reader.readLine()
        } catch (e: IOException) {
            Log.w(TAG, "Could not read $path", e)
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    // ignore
                }
            }
        }
        return line
    }

    /** Write [value] to [path]. Returns true on success. */
    fun writeValue(path: String, value: String): Boolean {
        return try {
            FileOutputStream(File(path)).use { fos ->
                fos.write(value.toByteArray())
                fos.flush()
            }
            true
        } catch (e: IOException) {
            Log.w(TAG, "Could not write $value to $path", e)
            false
        }
    }

    fun getFileValueAsBoolean(path: String, defValue: Boolean): Boolean {
        val v = readLine(path) ?: return defValue
        return v != "0"
    }

    /**
     * Read the gesture_type (first comma-separated field) from the coordinate node.
     * Returns -1 when the node can't be read or parsed.
     */
    fun readGestureType(): Int {
        val line = readLine(PROC_COORDINATE) ?: return -1
        val first = line.substringBefore(',')
        return first.toIntOrNull() ?: -1
    }
}
