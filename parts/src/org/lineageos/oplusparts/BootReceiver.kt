/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import org.lineageos.oplusparts.dc.DCDimmingManager
import org.lineageos.oplusparts.gesture.GestureManager

/**
 * Single boot-restore entry point for all OPlusParts features.
 *
 * Re-applies the persisted hardware state that does not survive a reboot:
 * the touchpanel gesture firmware arm switch and the DC dimming kernel node.
 * This is the belt-and-suspenders retry path for [KeyHandler]'s own early
 * restore, which may run before the Settings provider is ready.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        try {
            GestureManager.restore(context)
        } catch (e: Exception) {
            Log.w(TAG, "Gesture restore at boot failed", e)
        }
        try {
            DCDimmingManager.restore(context)
        } catch (e: Exception) {
            Log.w(TAG, "DC dimming restore at boot failed", e)
        }
    }

    companion object {
        private const val TAG = "OPlusParts.BootReceiver"
    }
}
