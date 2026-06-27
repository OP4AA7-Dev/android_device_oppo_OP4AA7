/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.util

import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

/**
 * Shared sysfs/proc helpers used by every feature module (gestures, DC
 * dimming, ...).  Keeps a single implementation of the read/write primitives
 * instead of one copy per package.
 */
object FileUtils {
    private const val TAG = "OPlusParts.FileUtils"

    /** Read the first line of [path], null on failure. */
    fun readLine(path: String): String? = try {
        BufferedReader(FileReader(path), 256).use { it.readLine() }
    } catch (e: IOException) {
        Log.w(TAG, "Could not read $path", e)
        null
    }

    /** Write [value] to [path]. Returns true on success. */
    fun writeValue(path: String, value: String): Boolean = try {
        FileOutputStream(File(path)).use { fos ->
            fos.write(value.toByteArray(Charsets.UTF_8))
            fos.flush()
        }
        true
    } catch (e: IOException) {
        Log.w(TAG, "Could not write $value to $path", e)
        false
    }
}
