/*
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import org.lineageos.oplusparts.KeyHandler

/**
 * Re-arms the firmware after boot according to the persisted master toggle.
 * Belt-and-suspenders alongside [KeyHandler]'s own restore on load (which may
 * run before the Settings provider is ready).
 */
class GestureBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val enabled = try {
            Settings.Secure.getInt(
                context.contentResolver,
                KeyHandler.SETTINGS_GESTURES_ENABLED, 0
            ) != 0
        } catch (e: Exception) {
            Log.w(TAG, "Cannot read gesture toggle at boot", e)
            return
        }
        Utils.writeValue(
            Utils.PROC_DOUBLE_TAP_ENABLE, if (enabled) "1" else "0"
        )
    }

    companion object {
        private const val TAG = "OPlusParts.BootReceiver"
    }
}
