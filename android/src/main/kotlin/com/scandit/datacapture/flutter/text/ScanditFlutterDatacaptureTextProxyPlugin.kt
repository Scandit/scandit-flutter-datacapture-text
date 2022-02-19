/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

package com.scandit.datacapture.flutter.text

import androidx.annotation.NonNull
import com.scandit.datacapture.flutter.core.utils.EventHandler
import com.scandit.datacapture.flutter.text.listeners.ScanditFlutterTextCaptureListener
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** ScanditFlutterDatacaptureTextPlugin */
class ScanditFlutterDatacaptureTextProxyPlugin : FlutterPlugin, MethodCallHandler {
    private var scanditFlutterDataCaptureTextHandler:
        ScanditFlutterDataCaptureTextHandler? = null

    override fun onAttachedToEngine(
        @NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding
    ) {
        scanditFlutterDataCaptureTextHandler = ScanditFlutterDataCaptureTextHandler(
            provideScanditFlutterTextCaptureListener(flutterPluginBinding.binaryMessenger)
        )
        scanditFlutterDataCaptureTextHandler?.onAttachedToEngine(flutterPluginBinding)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        result.notImplemented()
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        scanditFlutterDataCaptureTextHandler?.onDetachedFromEngine(binding)
        scanditFlutterDataCaptureTextHandler = null
    }

    private fun provideScanditFlutterTextCaptureListener(binaryMessenger: BinaryMessenger) =
        ScanditFlutterTextCaptureListener(
            EventHandler(
                EventChannel(
                    binaryMessenger,
                    ScanditFlutterTextCaptureListener.CHANNEL_NAME
                )
            )
        )
}
