/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

import Flutter
import Foundation
import ScanditTextCapture
import scandit_flutter_datacapture_core

@objc
public protocol ScanditFlutterDataCaptureTextProtocol {
    func dispose()
    func addListener(_ result: FlutterResult)
    func removeListener(_ result: FlutterResult)
    func finishDidCaptureText(enabled: Bool, result: FlutterResult)
}

@objc
public class ScanditFlutterDataCaptureText: NSObject, ScanditFlutterDataCaptureTextProtocol {
    private enum FunctionNames {
        static let getDefaults = "getDefaults"
        static let addListener = "addTextCaptureListener"
        static let removeListener = "removeTextCaptureListener"
        static let finishDidCaptureText = "textCaptureFinishDidCapture"
    }

    private let defaultsMethodChannel: FlutterMethodChannel
    private let listenerMethodChannel: FlutterMethodChannel
    private let eventChannel: FlutterEventChannel
    var eventSink: FlutterEventSink?
    var hasListeners = false

    private var _textCapture: TextCapture?
    var textCapture: TextCapture? {
        get {
            return _textCapture
        }
        set {
            _textCapture?.removeListener(self)
            _textCapture = newValue
            _textCapture?.addListener(self)
        }
    }
    
    var textCaptureLock = CallbackLock<Bool>(name: ScanditFlutterDataCaptureTextEvent.didCaptureText.rawValue)
    
    @objc
    public init(with messenger: FlutterBinaryMessenger) {
        let prefix = "com.scandit.datacapture.text.capture"
        defaultsMethodChannel = FlutterMethodChannel(name: "\(prefix).method/text_capture_defaults",
                                                     binaryMessenger: messenger)
        listenerMethodChannel = FlutterMethodChannel(name: "\(prefix).method/text_capture_listener",
                                                     binaryMessenger: messenger)
        eventChannel = FlutterEventChannel(name: "\(prefix).event/text_capture_listener",
                                           binaryMessenger: messenger)
        super.init()
        registerDeserializer()
        defaultsMethodChannel.setMethodCallHandler(handleMethodCall(_:result:))
        listenerMethodChannel.setMethodCallHandler(handleMethodCall(_:result:))
        eventChannel.setStreamHandler(self)
    }

    @objc
    func handleMethodCall(_ call: FlutterMethodCall, result: FlutterResult) {
        switch call.method {
        case FunctionNames.getDefaults:
            defaults(result)
        case FunctionNames.addListener:
            addListener(result)
        case FunctionNames.removeListener:
            removeListener(result)
        case FunctionNames.finishDidCaptureText:
            finishDidCaptureText(enabled: call.arguments as? Bool ?? false, result: result)
        default:
            result(FlutterMethodNotImplemented)
        }
    }

    @objc
    public func dispose() {
        defaultsMethodChannel.setMethodCallHandler(nil)
        listenerMethodChannel.setMethodCallHandler(nil)
        eventChannel.setStreamHandler(nil)
        textCaptureLock.reset()
    }

    private func defaults(_ result: FlutterResult) {
        let defaultsData = try! JSONSerialization.data(withJSONObject: defaults, options: [])
        let jsonString = String(data: defaultsData, encoding: .utf8)
        result(jsonString)
    }

    public func addListener(_ result: FlutterResult) {
        hasListeners = true
        result(nil)
    }

    public func removeListener(_ result: FlutterResult) {
        hasListeners = false
        result(nil)
    }

    public func finishDidCaptureText(enabled: Bool, result: FlutterResult) {
        textCaptureLock.unlock(value: enabled)
        result(nil)
    }
}
