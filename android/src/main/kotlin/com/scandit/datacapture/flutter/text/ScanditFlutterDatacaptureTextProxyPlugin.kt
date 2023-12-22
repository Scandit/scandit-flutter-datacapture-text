/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

package com.scandit.datacapture.flutter.text

import com.scandit.datacapture.flutter.core.utils.FlutterEmitter
import com.scandit.datacapture.flutter.text.listeners.ScanditFlutterTextCaptureListener
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import java.lang.ref.WeakReference
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/** ScanditFlutterDatacaptureTextPlugin */
class ScanditFlutterDatacaptureTextProxyPlugin : FlutterPlugin, ActivityAware {
    companion object {
        @JvmStatic
        private val lock = ReentrantLock()

        @JvmStatic
        private var isPluginAttached = false
    }

    private var scanditFlutterDataCaptureTextHandler:
        ScanditFlutterDataCaptureTextHandler? = null

    private var flutterPluginBinding: WeakReference<FlutterPluginBinding?> = WeakReference(null)

    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        flutterPluginBinding = WeakReference(binding)
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        flutterPluginBinding = WeakReference(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        onAttached()
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetached()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttached()
    }

    override fun onDetachedFromActivity() {
        onDetached()
    }

    private fun onAttached() {
        lock.withLock {
            if (isPluginAttached) return
            val flutterBinding = flutterPluginBinding.get() ?: return
            setupModule(flutterBinding)
            isPluginAttached = true
        }
    }

    private fun onDetached() {
        lock.withLock {
            val flutterBinding = flutterPluginBinding.get() ?: return
            disposeModule(flutterBinding)
            isPluginAttached = false
        }
    }

    private fun setupModule(binding: FlutterPluginBinding) {
        scanditFlutterDataCaptureTextHandler = ScanditFlutterDataCaptureTextHandler(
            provideScanditFlutterTextCaptureListener(binding.binaryMessenger)
        )
        scanditFlutterDataCaptureTextHandler?.onAttachedToEngine(binding)
    }

    private fun disposeModule(binding: FlutterPluginBinding) {
        scanditFlutterDataCaptureTextHandler?.onDetachedFromEngine(binding)
        scanditFlutterDataCaptureTextHandler = null
    }

    private fun provideScanditFlutterTextCaptureListener(binaryMessenger: BinaryMessenger) =
        ScanditFlutterTextCaptureListener(
            FlutterEmitter(
                EventChannel(
                    binaryMessenger,
                    ScanditFlutterTextCaptureListener.CHANNEL_NAME
                )
            )
        )
}
