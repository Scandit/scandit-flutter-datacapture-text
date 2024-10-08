/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2023- Scandit AG. All rights reserved.
 */

import Flutter
import scandit_flutter_datacapture_core
import ScanditFrameworksText
import ScanditFrameworksCore

class TextCaptureMethodHandler {
    private enum FunctionNames {
        static let getDefaults = "getDefaults"
        static let addListener = "addTextCaptureListener"
        static let removeListener = "removeTextCaptureListener"
        static let finishDidCaptureText = "textCaptureFinishDidCapture"
        static let getLastFrameData = "getLastFrameData"
        static let setModeEnabledState = "setModeEnabledState"
        static let updateTextCaptureMode = "updateTextCaptureMode"
        static let applyTextCaptureModeSettings = "applyTextCaptureModeSettings"
        static let updateTextCaptureOverlay = "updateTextCaptureOverlay"
    }

    private let textModule: TextCaptureModule

    init(textModule: TextCaptureModule) {
        self.textModule = textModule
    }

    @objc
    func handleMethodCall(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case FunctionNames.getDefaults:
            defaults(result)
        case FunctionNames.addListener:
            textModule.addListener()
            result(nil)
        case FunctionNames.removeListener:
            textModule.removeListener()
            result(nil)
        case FunctionNames.finishDidCaptureText:
            textModule.finishDidCaptureText(enabled: call.arguments as? Bool ?? false)
            result(nil)
        case FunctionNames.getLastFrameData:
            LastFrameData.shared.getLastFrameDataBytes {
                result($0)
            }
        case FunctionNames.setModeEnabledState:
            textModule.setModeEnabled(enabled: call.arguments as? Bool ?? true)
            result(nil)
        case FunctionNames.updateTextCaptureMode:
            textModule.updateModeFromJson(modeJson: call.arguments as! String, result: FlutterFrameworkResult(reply: result))
        case FunctionNames.applyTextCaptureModeSettings:
            textModule.applyModeSettings(modeSettingsJson: call.arguments as! String, result: FlutterFrameworkResult(reply: result))
        case FunctionNames.updateTextCaptureOverlay:
            textModule.updateOverlay(overlayJson: call.arguments as! String, result: FlutterFrameworkResult(reply: result))
        default:
            result(FlutterMethodNotImplemented)
        }
    }

    private func defaults(_ result: @escaping FlutterResult) {
        do {
            let defaultsData = try JSONSerialization.data(withJSONObject: textModule.defaults.toEncodable(),
                                                          options: [])
            let jsonString = String(data: defaultsData, encoding: .utf8)
            result(jsonString)
        } catch {
            result(FlutterError(code: "-1", message: "Unable to load the defaults. \(error)", details: nil))
        }
    }
}
