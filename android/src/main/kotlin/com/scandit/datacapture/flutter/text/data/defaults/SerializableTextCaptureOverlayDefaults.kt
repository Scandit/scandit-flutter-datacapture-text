/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

package com.scandit.datacapture.flutter.text.data.defaults

import com.scandit.datacapture.core.ui.style.Brush
import com.scandit.datacapture.frameworks.core.data.SerializableData
import com.scandit.datacapture.frameworks.core.data.defaults.BrushDefaults

class SerializableTextCaptureOverlayDefaults(
    private val defaultBrush: Brush
) : SerializableData {

    override fun toMap(): Map<String, Any?> =
        mapOf(
            FIELD_DEFAULT_TEXT_BRUSH to BrushDefaults.get(defaultBrush)
        )

    companion object {
        private const val FIELD_DEFAULT_TEXT_BRUSH = "Brush"
    }
}
