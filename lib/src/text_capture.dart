/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:scandit_flutter_datacapture_core/scandit_flutter_datacapture_core.dart';
import 'package:scandit_flutter_datacapture_text/src/text_capture_function_names.dart';

import 'text_capture_defaults.dart';
import 'text_capture_feedback.dart';
import 'text_capture_session.dart';
import 'text_capture_settings.dart';

@Deprecated("Text Capture mode is deprecated.")
abstract class TextCaptureListener {
  static const String _didCaptureEventName = "TextCaptureListener.didCaptureText";

  @Deprecated("Text Capture mode is deprecated.")
  void didCaptureText(TextCapture textCapture, TextCaptureSession session);
}

@Deprecated("Text Capture mode is deprecated.")
abstract class TextCaptureAdvancedListener {
  @Deprecated("Text Capture mode is deprecated.")
  void didCaptureText(TextCapture textCapture, TextCaptureSession session, Future<FrameData> getFrameData());
}

@Deprecated("Text Capture mode is deprecated.")
class TextCapture extends DataCaptureMode {
  bool _enabled = true;

  TextCaptureSettings _settings;

  final List<TextCaptureListener> _listeners = [];

  final List<TextCaptureAdvancedListener> _advancedListeners = [];

  TextCaptureFeedback _feedback = TextCaptureFeedback.defaultFeedback;

  late _TextCaptureListenerController _controller;

  TextCapture._(DataCaptureContext? context, this._settings) {
    _controller = _TextCaptureListenerController.forTextCapture(this);

    context?.addMode(this);
  }

  @Deprecated("Text Capture mode is deprecated.")
  TextCapture.forContext(DataCaptureContext? context, TextCaptureSettings settings) : this._(context, settings);

  @Deprecated("Text Capture mode is deprecated.")
  static CameraSettings get recommendedCameraSettings => _recommendedCameraSettings();

  static CameraSettings _recommendedCameraSettings() {
    var defaults = TextCaptureDefaults.cameraSettingsDefaults;
    return CameraSettings(defaults.preferredResolution, defaults.zoomFactor, defaults.focusRange,
        defaults.focusGestureStrategy, defaults.zoomGestureZoomFactor,
        properties: defaults.properties, shouldPreferSmoothAutoFocus: defaults.shouldPreferSmoothAutoFocus);
  }

  @override
  DataCaptureContext? get context => super.context;

  @Deprecated("Text Capture mode is deprecated.")
  @override
  bool get isEnabled => _enabled;

  @Deprecated("Text Capture mode is deprecated.")
  @override
  set isEnabled(bool newValue) {
    _enabled = newValue;
    _controller.setModeEnabledState(newValue);
  }

  @Deprecated("Text Capture mode is deprecated.")
  TextCaptureFeedback get feedback => _feedback;

  @Deprecated("Text Capture mode is deprecated.")
  set feedback(TextCaptureFeedback newValue) {
    _feedback = newValue;
    _controller.updateMode();
  }

  @Deprecated("Text Capture mode is deprecated.")
  void addListener(TextCaptureListener listener) {
    _checkAndSubscribeListeners();
    if (_listeners.contains(listener)) {
      return;
    }
    _listeners.add(listener);
  }

  @Deprecated("Text Capture mode is deprecated.")
  void addAdvancedListener(TextCaptureAdvancedListener listener) {
    _checkAndSubscribeListeners();
    if (_advancedListeners.contains(listener)) {
      return;
    }
    _advancedListeners.add(listener);
  }

  void _checkAndSubscribeListeners() {
    if (_listeners.isEmpty && _advancedListeners.isEmpty) {
      _controller.subscribeListeners();
    }
  }

  @Deprecated("Text Capture mode is deprecated.")
  void removeListener(TextCaptureListener listener) {
    _listeners.remove(listener);
    _checkAndUnsubscribeListeners();
  }

  @Deprecated("Text Capture mode is deprecated.")
  void removeAdvancedListener(TextCaptureAdvancedListener listener) {
    _advancedListeners.remove(listener);
    _checkAndUnsubscribeListeners();
  }

  @Deprecated("Text Capture mode is deprecated.")
  void _checkAndUnsubscribeListeners() {
    if (_listeners.isEmpty && _advancedListeners.isEmpty) {
      _controller.unsubscribeListeners();
    }
  }

  @Deprecated("Text Capture mode is deprecated.")
  Future<void> applySettings(TextCaptureSettings settings) {
    _settings = settings;
    return _controller.applySettings(settings);
  }

  @override
  Map<String, dynamic> toMap() {
    return {'type': 'textCapture', 'feedback': _feedback.toMap(), 'settings': _settings.toMap()};
  }
}

class _TextCaptureListenerController {
  final EventChannel _eventChannel = const EventChannel(TextCaptureFunctionNames.eventsChannelName);
  final MethodChannel _methodChannel = MethodChannel(TextCaptureFunctionNames.methodsChannelName);
  final TextCapture _textCapture;
  StreamSubscription<dynamic>? _textCaptureSubscription;

  _TextCaptureListenerController.forTextCapture(this._textCapture);

  void subscribeListeners() {
    _methodChannel
        .invokeMethod(TextCaptureFunctionNames.addTextCaptureListenerName)
        .then((value) => _setupTextCaptureSubscription(), onError: _onError);
  }

  void _setupTextCaptureSubscription() {
    _textCaptureSubscription = _eventChannel.receiveBroadcastStream().listen((event) {
      if (_textCapture._listeners.isEmpty && _textCapture._advancedListeners.isEmpty) return;

      var eventJSON = jsonDecode(event);
      var session = TextCaptureSession.fromJSON(jsonDecode(eventJSON['session']));
      var eventName = eventJSON['event'] as String;
      if (eventName == TextCaptureListener._didCaptureEventName) {
        _notifyListenersOfDidCaptureText(session);
        _methodChannel
            .invokeMethod(TextCaptureFunctionNames.textCaptureFinishDidCaptureName, _textCapture.isEnabled)
            // ignore: unnecessary_lambdas
            .then((value) => null, onError: (error) => print(error));
      }
    });
  }

  void _notifyListenersOfDidCaptureText(TextCaptureSession session) {
    for (var listener in _textCapture._listeners) {
      listener.didCaptureText(_textCapture, session);
    }
    for (var listener in _textCapture._advancedListeners) {
      listener.didCaptureText(_textCapture, session, _getLastFrameData);
    }
  }

  Future<FrameData> _getLastFrameData() {
    return _methodChannel
        .invokeMethod(TextCaptureFunctionNames.getLastFrameDataName)
        .then((value) => DefaultFrameData.fromJSON(Map<String, dynamic>.from(value as Map)), onError: _onError);
  }

  Future<void> setModeEnabledState(bool newValue) {
    return _methodChannel
        .invokeMethod(TextCaptureFunctionNames.setModeEnabledState, newValue)
        .then((value) => null, onError: _onError);
  }

  Future<void> updateMode() {
    var encoded = jsonEncode(_textCapture.toMap());
    return _methodChannel
        .invokeMethod(TextCaptureFunctionNames.updateTextCaptureMode, encoded)
        .then((value) => null, onError: _onError);
  }

  Future<void> applySettings(TextCaptureSettings settings) {
    var encoded = jsonEncode(settings.toMap());
    return _methodChannel
        .invokeMethod(TextCaptureFunctionNames.applyTextCaptureModeSettings, encoded)
        .then((value) => null, onError: _onError);
  }

  void unsubscribeListeners() {
    _textCaptureSubscription?.cancel();
    _methodChannel
        .invokeMethod(TextCaptureFunctionNames.removeTextCaptureListenerName)
        .then((value) => null, onError: _onError);
  }

  void _onError(Object? error, StackTrace? stackTrace) {
    if (error == null) return;
    print(error);

    if (stackTrace != null) {
      print(stackTrace);
    }

    throw error;
  }
}
