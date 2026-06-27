/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.settings

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import android.util.Log

/**
 * Thin wrapper around [Settings.Secure] that absorbs the repeated try/catch
 * and default-value handling scattered across KeyHandler, the preference
 * fragments and the boot receiver.
 *
 * Reads never throw: on any failure the supplied default is returned and a
 * warning is logged (important during Direct Boot / early system_server load
 * where the settings provider may not be ready yet).
 */
class SecureSettings private constructor(
    private val resolver: ContentResolver,
) {
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean = try {
        Settings.Secure.getInt(resolver, key, if (defaultValue) 1 else 0) != 0
    } catch (e: Exception) {
        Log.w(TAG, "Could not read boolean $key", e)
        defaultValue
    }

    fun putBoolean(key: String, value: Boolean): Boolean = try {
        Settings.Secure.putInt(resolver, key, if (value) 1 else 0)
    } catch (e: Exception) {
        Log.w(TAG, "Could not write boolean $key=$value", e)
        false
    }

    fun getString(key: String, defaultValue: String? = null): String? = try {
        Settings.Secure.getString(resolver, key) ?: defaultValue
    } catch (e: Exception) {
        Log.w(TAG, "Could not read string $key", e)
        defaultValue
    }

    fun putString(key: String, value: String): Boolean = try {
        Settings.Secure.putString(resolver, key, value)
    } catch (e: Exception) {
        Log.w(TAG, "Could not write string $key=$value", e)
        false
    }

    companion object {
        private const val TAG = "OPlusParts.SecureSettings"

        fun from(context: Context): SecureSettings = SecureSettings(context.contentResolver)
    }
}
