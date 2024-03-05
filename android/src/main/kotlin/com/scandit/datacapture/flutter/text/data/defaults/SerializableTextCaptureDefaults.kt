/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

package com.scandit.datacapture.flutter.text.data.defaults

import com.scandit.datacapture.frameworks.core.data.SerializableData
import com.scandit.datacapture.frameworks.core.data.defaults.CameraSettingsDefaults
import com.scandit.datacapture.text.capture.TextCapture
import com.scandit.datacapture.text.capture.TextCaptureSettings
import com.scandit.datacapture.text.ui.TextCaptureOverlay
import org.json.JSONObject

class SerializableTextCaptureDefaults(
    private val recommendedCameraSettings: CameraSettingsDefaults,
    private val textCaptureOverlayDefaults: SerializableTextCaptureOverlayDefaults,
    private val textCaptureSettingsDefaults: SerializableTextCaptureSettingsDefaults
) : SerializableData {

    override fun toMap(): Map<String, Any?> =
        mapOf(
            FIELD_RECOMMENDED_CAMERA_SETTINGS to recommendedCameraSettings.toMap(),
            FIELD_TEXT_CAPTURE_OVERLAY to textCaptureOverlayDefaults.toMap(),
            FIELD_TEXT_CAPTURE_SETTINGS to textCaptureSettingsDefaults.toMap()
        )

    companion object {
        private const val FIELD_RECOMMENDED_CAMERA_SETTINGS = "RecommendedCameraSettings"
        private const val FIELD_TEXT_CAPTURE_OVERLAY = "TextCaptureOverlay"
        private const val FIELD_TEXT_CAPTURE_SETTINGS = "TextCaptureSettings"

        @JvmStatic
        fun createDefaults(): String {
            return JSONObject(
                SerializableTextCaptureDefaults(
                    CameraSettingsDefaults.create(TextCapture.createRecommendedCameraSettings()),
                    SerializableTextCaptureOverlayDefaults(TextCaptureOverlay.defaultBrush()),
                    SerializableTextCaptureSettingsDefaults(TextCaptureSettings.fromJson("{}"))
                ).toMap()
            ).toString()
        }
    }
}
