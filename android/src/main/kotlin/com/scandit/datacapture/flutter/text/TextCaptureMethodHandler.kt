/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

package com.scandit.datacapture.flutter.text

import com.scandit.datacapture.flutter.core.utils.FlutterResult
import com.scandit.datacapture.flutter.core.utils.rejectKotlinError
import com.scandit.datacapture.frameworks.core.errors.FrameDataNullError
import com.scandit.datacapture.frameworks.core.utils.DefaultLastFrameData
import com.scandit.datacapture.frameworks.core.utils.LastFrameData
import com.scandit.datacapture.frameworks.text.TextCaptureModule
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject

class TextCaptureMethodHandler(
    private val textCaptureModule: TextCaptureModule,
    private val lastFrameData: LastFrameData = DefaultLastFrameData.getInstance()
) : MethodChannel.MethodCallHandler {

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            METHOD_GET_DEFAULTS -> result.success(
                JSONObject(textCaptureModule.getDefaults()).toString()
            )

            METHOD_ADD_LISTENER ->
                textCaptureModule.addListener(FlutterResult(result))

            METHOD_REMOVE_LISTENER ->
                textCaptureModule.removeListener(FlutterResult(result))

            METHOD_FINISH_DID_CAPTURE ->
                textCaptureModule.finishDidCapture(call.arguments as Boolean, FlutterResult(result))

            METHOD_SET_MODE_ENABLED_STATE ->
                textCaptureModule.setModeEnabled(call.arguments as Boolean, FlutterResult(result))

            METHOD_UPDATE_MODE ->
                textCaptureModule.updateModeFromJson(
                    call.arguments as String,
                    FlutterResult(result)
                )

            METHOD_APPLY_SETTINGS ->
                textCaptureModule.applyModeSettings(call.arguments as String, FlutterResult(result))

            METHOD_UPDATE_OVERLAY ->
                textCaptureModule.updateOverlay(call.arguments as String, FlutterResult(result))

            METHOD_GET_LAST_FRAME -> lastFrameData.getLastFrameDataBytes {
                if (it == null) {
                    result.rejectKotlinError(FrameDataNullError())
                    return@getLastFrameDataBytes
                }
                result.success(it)
            }
        }
    }

    companion object {
        const val METHOD_CHANNEL_NAME = "com.scandit.datacapture.text.capture/method_channel"
        const val EVENT_CHANNEL_NAME: String =
            "com.scandit.datacapture.text.capture/event_channel"

        private const val METHOD_GET_DEFAULTS = "getDefaults"
        private const val METHOD_ADD_LISTENER = "addTextCaptureListener"
        private const val METHOD_REMOVE_LISTENER = "removeTextCaptureListener"
        private const val METHOD_FINISH_DID_CAPTURE = "textCaptureFinishDidCapture"
        private const val METHOD_GET_LAST_FRAME = "getLastFrameData"
        private const val METHOD_SET_MODE_ENABLED_STATE = "setModeEnabledState"
        private const val METHOD_UPDATE_MODE = "updateTextCaptureMode"
        private const val METHOD_APPLY_SETTINGS = "applyTextCaptureModeSettings"
        private const val METHOD_UPDATE_OVERLAY = "updateTextCaptureOverlay"
    }
}
