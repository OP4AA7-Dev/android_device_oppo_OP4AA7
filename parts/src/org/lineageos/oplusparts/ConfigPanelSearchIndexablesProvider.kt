/*
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts

import android.database.Cursor
import android.database.MatrixCursor
import android.provider.SearchIndexablesProvider
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_CLASS_NAME
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_ICON_RESID
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_ACTION
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_RANK
import android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_RESID
import android.provider.SearchIndexablesContract.INDEXABLES_RAW_COLUMNS
import android.provider.SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS
import android.provider.SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS
import org.lineageos.oplusparts.gesture.ScreenOffGesturesActivity

/**
 * Feeds the screen-off gestures preference XML into system Settings search
 * and the "Extra settings" index, so the page is reachable from Settings.
 */
class ConfigPanelSearchIndexablesProvider : SearchIndexablesProvider() {
    override fun onCreate(): Boolean = true

    override fun queryXmlResources(projection: Array<String?>?): Cursor {
        val cursor = MatrixCursor(INDEXABLES_XML_RES_COLUMNS)
        val ref = arrayOfNulls<Any>(7)
        ref[COLUMN_INDEX_XML_RES_RANK] = 1
        ref[COLUMN_INDEX_XML_RES_RESID] = R.xml.screen_off_gestures
        ref[COLUMN_INDEX_XML_RES_CLASS_NAME] = null
        ref[COLUMN_INDEX_XML_RES_ICON_RESID] = R.drawable.ic_extension
        ref[COLUMN_INDEX_XML_RES_INTENT_ACTION] = "com.android.settings.action.EXTRA_SETTINGS"
        ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE] = "org.lineageos.oplusparts"
        ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS] = ScreenOffGesturesActivity::class.java.name
        cursor.addRow(ref)
        return cursor
    }

    override fun queryRawData(projection: Array<String?>?): Cursor =
        MatrixCursor(INDEXABLES_RAW_COLUMNS)

    override fun queryNonIndexableKeys(projection: Array<String?>?): Cursor =
        MatrixCursor(NON_INDEXABLES_KEYS_COLUMNS)
}
