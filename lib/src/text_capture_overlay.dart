/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:scandit_flutter_datacapture_core/scandit_flutter_datacapture_core.dart';

import 'text_capture.dart';
import 'text_capture_defaults.dart';
import 'text_capture_function_names.dart';

class TextCaptureOverlay extends DataCaptureOverlay {
  // ignore: unused_field
  final TextCapture _textCapture;
  late _TextCaptureOverlayController _controller;

  Brush _brush = defaultBrush;

  @override
  DataCaptureView? view;

  TextCaptureOverlay._(this._textCapture, this.view) : super('textCapture') {
    view?.addOverlay(this);
    _controller = _TextCaptureOverlayController(this);
  }

  TextCaptureOverlay.withTextCapture(TextCapture textCapture) : this.withTextCaptureForView(textCapture, null);

  TextCaptureOverlay.withTextCaptureForView(TextCapture textCapture, DataCaptureView? view) : this._(textCapture, view);

  static Brush get defaultBrush {
    return TextCaptureDefaults.textCaptureOverlayDefaults.defaultBrush;
  }

  Brush get brush => _brush;

  set brush(Brush newValue) {
    _brush = newValue;
    _controller.update();
  }

  bool _shouldShowScanAreaGuides = false;

  bool get shouldShowScanAreaGuides => _shouldShowScanAreaGuides;

  set shouldShowScanAreaGuides(bool newValue) {
    _shouldShowScanAreaGuides = newValue;
    _controller.update();
  }

  Viewfinder? _viewfinder;

  Viewfinder? get viewfinder => _viewfinder;

  set viewfinder(Viewfinder? newValue) {
    _viewfinder = newValue;
    _controller.update();
  }

  @override
  Map<String, dynamic> toMap() {
    var json = super.toMap();
    json.addAll({
      'brush': _brush.toMap(),
      'shouldShowScanAreaGuides': _shouldShowScanAreaGuides,
      'viewfinder': _viewfinder == null ? {'type': 'none'} : _viewfinder?.toMap()
    });
    return json;
  }
}

class _TextCaptureOverlayController {
  late final MethodChannel _methodChannel = _getChannel();

  TextCaptureOverlay _overlay;

  _TextCaptureOverlayController(this._overlay);

  Future<void> update() {
    return _methodChannel.invokeMethod(TextCaptureFunctionNames.updateTextCaptureOverlay, jsonEncode(_overlay.toMap()));
  }

  MethodChannel _getChannel() {
    return MethodChannel(TextCaptureFunctionNames.methodsChannelName);
  }
}
