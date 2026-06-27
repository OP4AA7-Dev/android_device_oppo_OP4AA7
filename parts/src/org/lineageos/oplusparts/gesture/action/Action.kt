/*
 * Copyright (C) 2014 SlimRoms Project
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture.action

import android.content.Context
import android.util.Log

/**
 * Entry point for executing a gesture action token.
 *
 * Looks up the token among the registered [GestureAction] executors and
 * delegates. Null / empty / [ActionTokens.NONE] tokens are no-ops; tokens not
 * in [ActionTokens.SUPPORTED] (e.g. stale values from older builds) are
 * likewise no-ops rather than being interpreted as Intent URIs — the custom
 * shortcut fallback was removed since no UI path produces a URI.
 *
 * Adapted from the SlimRoms/Omni action framework for Android 14.
 */
object Action {
    private const val TAG = "OPlusParts.Action"

    private val executors: List<GestureAction> = listOf(
        DeviceActions,
        TorchAction,
        MediaActions,
        RingerAction,
    )

    fun processAction(context: Context, action: String?) {
        if (action.isNullOrEmpty() || action == ActionTokens.NONE) return
        if (action !in ActionTokens.SUPPORTED) {
            Log.w(TAG, "Ignoring unsupported action token: $action")
            return
        }
        val executor = executors.firstOrNull { action in it.tokens() } ?: return
        executor.execute(context, action)
    }
}
