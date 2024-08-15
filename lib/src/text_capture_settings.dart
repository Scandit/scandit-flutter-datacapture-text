/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

import 'package:scandit_flutter_datacapture_core/scandit_flutter_datacapture_core.dart';

import 'text_capture_defaults.dart';

@Deprecated("Text Capture mode is deprecated.")
class TextCaptureSettings implements Serializable {
  final Map<String, dynamic> _properties = {};
  final Map<String, dynamic> _textRecognizerSettings = {};

  Direction _recognitionDirection = TextCaptureDefaults.textCaptureSettingsDefaults.recognitionDirection;

  @Deprecated("Text Capture mode is deprecated.")
  Direction get recognitionDirection => _recognitionDirection;

  @Deprecated("Text Capture mode is deprecated.")
  set recognitionDirection(Direction newValue) {
    _recognitionDirection = newValue;
  }

  Duration _duplicateFilter = Duration(milliseconds: TextCaptureDefaults.textCaptureSettingsDefaults.duplicateFilter);

  @Deprecated("Text Capture mode is deprecated.")
  Duration get duplicateFilter => _duplicateFilter;

  @Deprecated("Text Capture mode is deprecated.")
  set duplicateFilter(Duration newValue) {
    _duplicateFilter = newValue;
  }

  LocationSelection? locationSelection;

  @Deprecated("Text Capture mode is deprecated.")
  void setProperty<T>(String name, T value) {
    _properties[name] = value;
  }

  @Deprecated("Text Capture mode is deprecated.")
  T getProperty<T>(String name) {
    return _properties[name] as T;
  }

  @Deprecated("Text Capture mode is deprecated.")
  static TextCaptureSettings fromJSON(Map<String, dynamic> json) {
    var settings = TextCaptureSettings();

    for (var entry in json.entries) {
      settings._textRecognizerSettings[entry.key] = entry.value;
    }

    return settings;
  }

  @override
  Map<String, dynamic> toMap() {
    var json = {
      'duplicateFilter': duplicateFilter.inMilliseconds,
      'recognitionDirection': recognitionDirection.toString(),
      'locationSelection': locationSelection == null ? {'type': 'none'} : locationSelection?.toMap(),
      'properties': _properties
    };
    json.addAll(_textRecognizerSettings);
    return json;
  }
}
