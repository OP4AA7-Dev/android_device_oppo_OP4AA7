/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.search

import android.database.MatrixCursor
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_CLASS_NAME
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_ICON_RESID
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_ACTION
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_RANK
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_RESID

/**
 * Builds the 7-column XML-resource index rows consumed by Settings search,
 * deduplicating the repeated `arrayOfNulls<Any>(7)` boilerplate.
 */
object SearchIndexRows {
    private const val TARGET_PACKAGE = "org.lineageos.oplusparts"
    private const val INTENT_ACTION = "com.android.settings.action.EXTRA_SETTINGS"

    fun addXmlResource(
        cursor: MatrixCursor,
        rank: Int,
        xmlResId: Int,
        targetClass: Class<*>,
        iconResId: Int,
    ) {
        val ref = arrayOfNulls<Any>(7)
        ref[COLUMN_INDEX_XML_RES_RANK] = rank
        ref[COLUMN_INDEX_XML_RES_RESID] = xmlResId
        ref[COLUMN_INDEX_XML_RES_CLASS_NAME] = null
        ref[COLUMN_INDEX_XML_RES_ICON_RESID] = iconResId
        ref[COLUMN_INDEX_XML_RES_INTENT_ACTION] = INTENT_ACTION
        ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE] = TARGET_PACKAGE
        ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS] = targetClass.name
        cursor.addRow(ref)
    }
}
