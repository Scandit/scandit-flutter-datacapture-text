/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

package com.scandit.datacapture.flutter.text

import com.scandit.datacapture.flutter.core.extensions.getMethodChannel
import com.scandit.datacapture.flutter.core.utils.FlutterEmitter
import com.scandit.datacapture.frameworks.core.locator.DefaultServiceLocator
import com.scandit.datacapture.frameworks.text.TextCaptureModule
import com.scandit.datacapture.frameworks.text.listeners.FrameworksTextCaptureListener
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
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

    private val serviceLocator = DefaultServiceLocator.getInstance()

    private var textCaptureMethodChannel: MethodChannel? = null

    private var flutterPluginBinding: WeakReference<FlutterPluginBinding?> = WeakReference(null)

    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        flutterPluginBinding = WeakReference(binding)
        onAttached()
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        flutterPluginBinding = WeakReference(null)
        onDetached()
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
            if (isPluginAttached) {
                disposeModule()
            }
            val flutterBinding = flutterPluginBinding.get() ?: return
            setupModule(flutterBinding)
            isPluginAttached = true
        }
    }

    private fun onDetached() {
        lock.withLock {
            disposeModule()
            isPluginAttached = false
        }
    }

    private fun setupModule(binding: FlutterPluginBinding) {
        val eventEmitter = FlutterEmitter(
            EventChannel(
                binding.binaryMessenger,
                TextCaptureMethodHandler.EVENT_CHANNEL_NAME
            )
        )

        val textCaptureModule = TextCaptureModule(
            FrameworksTextCaptureListener(
                eventEmitter
            )
        ).also { module ->
            module.onCreate(binding.applicationContext)

            textCaptureMethodChannel = binding.getMethodChannel(
                TextCaptureMethodHandler.METHOD_CHANNEL_NAME
            ).also {
                it.setMethodCallHandler(TextCaptureMethodHandler(module))
            }
        }
        serviceLocator.register(textCaptureModule)
    }

    private fun disposeModule() {
        serviceLocator.remove(TextCaptureModule::class.java.name)?.onDestroy()
        textCaptureMethodChannel?.setMethodCallHandler(null)
    }
}
