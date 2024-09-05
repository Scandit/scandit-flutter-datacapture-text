/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2024- Scandit AG. All rights reserved.
 */
package com.scandit.datacapture.flutter.text;

import androidx.annotation.NonNull;

import com.scandit.datacapture.flutter.core.extensions.MethodChannelExtensions;
import com.scandit.datacapture.flutter.core.utils.FlutterEmitter;
import com.scandit.datacapture.frameworks.core.FrameworkModule;
import com.scandit.datacapture.frameworks.core.locator.DefaultServiceLocator;
import com.scandit.datacapture.frameworks.core.locator.ServiceLocator;
import com.scandit.datacapture.frameworks.text.TextCaptureModule;
import com.scandit.datacapture.frameworks.text.listeners.FrameworksTextCaptureListener;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodChannel;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantLock;

public class ScanditFlutterDatacaptureTextProxyPlugin implements FlutterPlugin, ActivityAware {
    private static final ReentrantLock lock = new ReentrantLock();

    private static final FlutterEmitter textEmitter = new FlutterEmitter(TextCaptureMethodHandler.EVENT_CHANNEL_NAME);

    private final ServiceLocator<FrameworkModule> serviceLocator = DefaultServiceLocator.getInstance();

    private MethodChannel textCaptureMethodChannel;
    private WeakReference<FlutterPluginBinding> flutterPluginBinding = new WeakReference<>(null);

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        flutterPluginBinding = new WeakReference<>(binding);
        setupModules(binding);
        setupMethodChannels(binding);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        flutterPluginBinding = new WeakReference<>(null);
        disposeMethodChannels();
        disposeModules();
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        setupEventChannels();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        // NOOP
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        // NOOP
    }

    @Override
    public void onDetachedFromActivity() {
        disposeEventChannels();
    }

    private void setupEventChannels() {
        FlutterPluginBinding binding = flutterPluginBinding.get();
        if (binding != null) {
            textEmitter.addChannel(binding.getBinaryMessenger());
        }
    }

    private void disposeEventChannels() {
        FlutterPluginBinding binding = flutterPluginBinding.get();
        if (binding != null) {
            textEmitter.removeChannel(binding.getBinaryMessenger());
        }
    }

    private void setupModules(@NonNull FlutterPluginBinding binding) {
        lock.lock();
        try {
            TextCaptureModule textCaptureModule = (TextCaptureModule) serviceLocator.remove(TextCaptureModule.class.getName());
            if (textCaptureModule != null) return;

            textCaptureModule = TextCaptureModule.create(
                    FrameworksTextCaptureListener.create(textEmitter)
            );
            textCaptureModule.onCreate(binding.getApplicationContext());

            serviceLocator.register(textCaptureModule);
        } finally {
            lock.unlock();
        }
    }

    private void disposeModules() {
        lock.lock();
        try {
            TextCaptureModule textCaptureModule = (TextCaptureModule) serviceLocator.remove(TextCaptureModule.class.getName());
            if (textCaptureModule != null) {
                textCaptureModule.onDestroy();
            }
        } finally {
            lock.unlock();
        }
    }

    private void setupMethodChannels(@NonNull FlutterPluginBinding binding) {
        textCaptureMethodChannel = MethodChannelExtensions.getMethodChannel(
                binding,
                TextCaptureMethodHandler.METHOD_CHANNEL_NAME
        );
        textCaptureMethodChannel.setMethodCallHandler(new TextCaptureMethodHandler(serviceLocator));
    }

    private void disposeMethodChannels() {
        if (textCaptureMethodChannel != null) {
            textCaptureMethodChannel.setMethodCallHandler(null);
            textCaptureMethodChannel = null;
        }
    }
}
