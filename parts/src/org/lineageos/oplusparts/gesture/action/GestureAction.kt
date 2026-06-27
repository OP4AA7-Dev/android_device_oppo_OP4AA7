/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture.action

import android.content.Context

/**
 * A single gesture action executor. One executor may handle multiple tokens
 * (e.g. the three media keys share one implementation).
 */
interface GestureAction {
    /** Tokens this executor handles. */
    fun tokens(): Set<String>

    /** Perform the action. Return false to signal failure (logged by the facade). */
    fun execute(context: Context, token: String): Boolean
}
