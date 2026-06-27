/*
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import com.android.internal.os.DeviceKeyHandler
import org.lineageos.oplusparts.gesture.GestureManager
import org.lineageos.oplusparts.gesture.GestureType
import org.lineageos.oplusparts.gesture.Utils
import org.lineageos.oplusparts.gesture.action.Action
import org.lineageos.oplusparts.settings.SecureSettings

/**
 * DeviceKeyHandler loaded by system_server (PhoneWindowManager).
 *
 * The S3706 touch firmware reports *every* screen-off gesture as a single
 * key event: scancode 62 (KEY_F4, mapped via touchpanel.kl to KEYCODE_F4).
 * The concrete gesture is read from the first field of
 * /proc/touchpanel/coordinate (see touchpanel_common.h gesture_type enum).
 *
 * On load we re-arm the firmware according to the persisted master toggle so
 * the device wakes from gestures immediately after boot, before the OPlusParts
 * app process is ever started.
 *
 * NOTE: this class is reflectively instantiated by system_server. Its fully
 * qualified name, the `com.android.internal.os.DeviceKeyHandler` interface
 * and the single-arg `Context` constructor must remain stable, and the
 * proguard keep rule in proguard.flags must be preserved.
 */
class KeyHandler(private val context: Context) : DeviceKeyHandler {

    init {
        restoreArmedState()
    }

    override fun handleKeyEvent(event: KeyEvent): KeyEvent? {
        // Touchpanel gestures arrive as KEY_F4 (scancode 62). Only act on UP.
        if (event.keyCode != KeyEvent.KEYCODE_F4 ||
            event.action != KeyEvent.ACTION_UP
        ) {
            return event
        }

        if (!GestureManager.isEnabled(context)) return event

        val gestureType = GestureType.fromCode(Utils.readGestureType())
        if (gestureType == null) {
            Log.w(TAG, "Unknown gesture_type=$gestureType, ignoring")
            return null
        }

        val action = SecureSettings.from(context)
            .getString(gestureType.secureKey, gestureType.defaultAction)
            ?: gestureType.defaultAction

        try {
            Action.processAction(context, action)
        } catch (e: Exception) {
            Log.e(TAG, "Action '$action' failed for $gestureType", e)
        }
        return null // consume the synthetic gesture key
    }

    private fun restoreArmedState() {
        try {
            GestureManager.restore(context)
        } catch (e: Exception) {
            // Settings provider may not be ready this early; the OPlusParts
            // boot receiver will retry. Non-fatal.
            Log.w(TAG, "Could not restore gesture armed state", e)
        }
    }

    companion object {
        private const val TAG = "OPlusParts.KeyHandler"
    }
}
