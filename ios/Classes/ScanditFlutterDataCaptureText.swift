/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

import Flutter
import scandit_flutter_datacapture_core
import ScanditFrameworksText

@objc
public class ScanditFlutterDataCaptureText: NSObject, FlutterPlugin {
    private let textModule: TextCaptureModule
    private let methodChannel: FlutterMethodChannel

    init(textModule: TextCaptureModule, textCaptureMethodChannel: FlutterMethodChannel) {
        self.textModule = textModule
        self.methodChannel = textCaptureMethodChannel
    }

    public static func register(with registrar: FlutterPluginRegistrar) {
        let prefix = "com.scandit.datacapture.text.capture"
        let methodChannel = FlutterMethodChannel(name: "\(prefix)/method_channel",
                                                 binaryMessenger: registrar.messenger())
        let eventChannel = FlutterEventChannel(name: "\(prefix)/event_channel",
                                               binaryMessenger: registrar.messenger())
        let emitter = FlutterEventEmitter(eventChannel: eventChannel)
        let textCaptureListener = FrameworksTextCaptureListener(emitter: emitter)
        let textModule = TextCaptureModule(textCaptureListener: textCaptureListener)
        let methodCallHandler = TextCaptureMethodHandler(textModule: textModule)
        textModule.didStart()

        let plugin = ScanditFlutterDataCaptureText(textModule: textModule,
                                                   textCaptureMethodChannel: methodChannel)
        methodChannel.setMethodCallHandler(methodCallHandler.handleMethodCall(_:result:))
        registrar.publish(plugin)
    }

    public func detachFromEngine(for registrar: FlutterPluginRegistrar) {
        textModule.didStop()
        methodChannel.setMethodCallHandler(nil)
    }
}
