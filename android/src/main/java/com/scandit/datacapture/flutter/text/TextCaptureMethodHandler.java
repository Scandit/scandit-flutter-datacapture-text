/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2024- Scandit AG. All rights reserved.
 */
package com.scandit.datacapture.flutter.text;

import androidx.annotation.NonNull;

import com.scandit.datacapture.flutter.core.utils.FlutterResult;
import com.scandit.datacapture.flutter.core.utils.ResultUtils;
import com.scandit.datacapture.frameworks.core.FrameworkModule;
import com.scandit.datacapture.frameworks.core.errors.FrameDataNullError;
import com.scandit.datacapture.frameworks.core.locator.ServiceLocator;
import com.scandit.datacapture.frameworks.core.utils.DefaultLastFrameData;
import com.scandit.datacapture.frameworks.core.utils.LastFrameData;
import com.scandit.datacapture.frameworks.text.TextCaptureModule;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import org.json.JSONObject;

public class TextCaptureMethodHandler implements MethodChannel.MethodCallHandler {

    public static final String METHOD_CHANNEL_NAME = "com.scandit.datacapture.text.capture/method_channel";
    public static final String EVENT_CHANNEL_NAME = "com.scandit.datacapture.text.capture/event_channel";

    private final ServiceLocator<FrameworkModule> serviceLocator;
    private final LastFrameData lastFrameData;

    public TextCaptureMethodHandler(ServiceLocator<FrameworkModule> serviceLocator, LastFrameData lastFrameData) {
        this.serviceLocator = serviceLocator;
        this.lastFrameData = lastFrameData != null ? lastFrameData : DefaultLastFrameData.getInstance();
    }

    public TextCaptureMethodHandler(ServiceLocator<FrameworkModule> serviceLocator) {
        this(serviceLocator, DefaultLastFrameData.getInstance());
    }

    @Override
    public void onMethodCall(MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "getDefaults":
                result.success(new JSONObject(getSharedModule().getDefaults()).toString());
                break;
            case "addTextCaptureListener":
                getSharedModule().addListener(new FlutterResult(result));
                break;
            case "removeTextCaptureListener":
                getSharedModule().removeListener(new FlutterResult(result));
                break;
            case "textCaptureFinishDidCapture":
                getSharedModule().finishDidCapture(
                        Boolean.TRUE.equals(call.arguments()),
                        new FlutterResult(result)
                );
                break;
            case "setModeEnabledState":
                getSharedModule().setModeEnabled(
                        Boolean.TRUE.equals(call.arguments()),
                        new FlutterResult(result)
                );
                break;
            case "updateTextCaptureMode":
                assert call.arguments() != null;
                getSharedModule().updateModeFromJson(call.arguments(), new FlutterResult(result));
                break;
            case "applyTextCaptureModeSettings":
                assert call.arguments() != null;
                getSharedModule().applyModeSettings(call.arguments(), new FlutterResult(result));
                break;
            case "updateTextCaptureOverlay":
                assert call.arguments() != null;
                getSharedModule().updateOverlay(call.arguments(), new FlutterResult(result));
                break;
            case "getLastFrameData":
                lastFrameData.getLastFrameDataBytes(bytes -> {
                    if (bytes == null) {
                        ResultUtils.rejectKotlinError(result, new FrameDataNullError());
                    } else {
                        result.success(bytes);
                    }
                    return null;
                });
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private volatile TextCaptureModule sharedModuleInstance;

    private TextCaptureModule getSharedModule() {
        if (sharedModuleInstance == null) {
            synchronized (this) {
                if (sharedModuleInstance == null) {
                    sharedModuleInstance = (TextCaptureModule) this.serviceLocator.resolve(TextCaptureModule.class.getName());
                }
            }
        }
        return sharedModuleInstance;
    }
}
