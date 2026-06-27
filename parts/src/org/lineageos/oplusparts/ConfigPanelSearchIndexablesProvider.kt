/*
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts

import android.database.Cursor
import android.database.MatrixCursor
import android.provider.SearchIndexablesProvider
import android.provider.SearchIndexablesContract.INDEXABLES_RAW_COLUMNS
import android.provider.SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS
import android.provider.SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS
import org.lineageos.oplusparts.gesture.ScreenOffFeaturesActivity
import org.lineageos.oplusparts.gesture.ScreenOffGesturesActivity
import org.lineageos.oplusparts.search.SearchIndexRows

/**
 * Feeds preference XML entries into system Settings search
 * and the "Extra settings" index, so the pages are reachable from Settings.
 */
class ConfigPanelSearchIndexablesProvider : SearchIndexablesProvider() {
    override fun onCreate(): Boolean = true

    override fun queryXmlResources(projection: Array<String?>?): Cursor {
        val cursor = MatrixCursor(INDEXABLES_XML_RES_COLUMNS)
        SearchIndexRows.addXmlResource(cursor, 1, R.xml.oplus_parts, OplusPartsActivity::class.java, R.drawable.ic_extension)
        SearchIndexRows.addXmlResource(cursor, 2, R.xml.screen_off_features, ScreenOffFeaturesActivity::class.java, R.drawable.ic_extension)
        SearchIndexRows.addXmlResource(cursor, 3, R.xml.screen_off_gestures, ScreenOffGesturesActivity::class.java, R.drawable.ic_extension)
        return cursor
    }

    override fun queryRawData(projection: Array<String?>?): Cursor =
        MatrixCursor(INDEXABLES_RAW_COLUMNS)

    override fun queryNonIndexableKeys(projection: Array<String?>?): Cursor =
        MatrixCursor(NON_INDEXABLES_KEYS_COLUMNS)
}
