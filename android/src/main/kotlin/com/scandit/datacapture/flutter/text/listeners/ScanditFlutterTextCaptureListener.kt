/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

package com.scandit.datacapture.flutter.text.listeners

import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.flutter.core.utils.EventSinkWithResult
import com.scandit.datacapture.flutter.core.utils.FlutterEmitter
import com.scandit.datacapture.frameworks.core.utils.DefaultLastFrameData
import com.scandit.datacapture.frameworks.core.utils.LastFrameData
import com.scandit.datacapture.text.capture.TextCapture
import com.scandit.datacapture.text.capture.TextCaptureListener
import com.scandit.datacapture.text.capture.TextCaptureSession
import org.json.JSONObject

@Suppress("unused")
class ScanditFlutterTextCaptureListener(
    private val flutterEmitter: FlutterEmitter,
    private val onTextCaptured: EventSinkWithResult<Boolean> =
        EventSinkWithResult(ON_TEXT_CAPTURED),
    private val lastFrameData: LastFrameData = DefaultLastFrameData.getInstance()
) : TextCaptureListener {

    fun enableListener() {
        flutterEmitter.enableListener()
    }

    fun disableListener() {
        flutterEmitter.disableListener()
        onTextCaptured.onCancel()
    }

    override fun onTextCaptured(mode: TextCapture, session: TextCaptureSession, data: FrameData) {
        lastFrameData.frameData.set(data)
        flutterEmitter.getCurrentEventSink()?.let {
            val params = JSONObject(
                mapOf(
                    FIELD_EVENT to ON_TEXT_CAPTURED,
                    FIELD_SESSION to session.toJson()
                )
            ).toString()
            mode.isEnabled =
                onTextCaptured.emitForResult(it, params, mode.isEnabled)
        }
        lastFrameData.frameData.set(null)
    }

    fun finishDidCaptureText(enabled: Boolean) {
        onTextCaptured.onResult(enabled)
    }

    companion object {
        const val CHANNEL_NAME: String =
            "com.scandit.datacapture.text.capture.event/text_capture_listener"
        private const val ON_TEXT_CAPTURED = "textCaptureListener-didCapture"

        private const val FIELD_EVENT = "event"
        private const val FIELD_SESSION = "session"
    }
}
