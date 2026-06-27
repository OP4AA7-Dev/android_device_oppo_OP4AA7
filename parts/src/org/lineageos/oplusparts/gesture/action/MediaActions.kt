/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture.action

import android.content.Context
import android.media.session.MediaSessionLegacyHelper
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent

/**
 * Dispatch media button events (previous / next / play-pause) to the active
 * media session via [MediaSessionLegacyHelper].
 */
object MediaActions : GestureAction {
    private const val TAG = "OPlusParts.MediaActions"

    override fun tokens() = setOf(
        ActionTokens.MEDIA_PREVIOUS,
        ActionTokens.MEDIA_NEXT,
        ActionTokens.MEDIA_PLAY_PAUSE,
    )

    override fun execute(context: Context, token: String): Boolean {
        val keycode = when (token) {
            ActionTokens.MEDIA_PREVIOUS -> KeyEvent.KEYCODE_MEDIA_PREVIOUS
            ActionTokens.MEDIA_NEXT -> KeyEvent.KEYCODE_MEDIA_NEXT
            ActionTokens.MEDIA_PLAY_PAUSE -> KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
            else -> return false
        }
        return try {
            dispatchMediaKey(context, keycode)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Media key dispatch failed", e)
            false
        }
    }

    private fun dispatchMediaKey(context: Context, keycode: Int) {
        val now = SystemClock.uptimeMillis()
        val helper = MediaSessionLegacyHelper.getHelper(context)
        var event = KeyEvent(now, now, KeyEvent.ACTION_DOWN, keycode, 0)
        helper.sendMediaButtonEvent(event, true)
        event = KeyEvent.changeAction(event, KeyEvent.ACTION_UP)
        helper.sendMediaButtonEvent(event, true)
    }
}
