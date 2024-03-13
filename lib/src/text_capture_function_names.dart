/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

class TextCaptureFunctionNames {
  static const String _prefix = 'com.scandit.datacapture.text.capture';
  static const String methodsChannelName = '$_prefix/method_channel';
  static const String eventsChannelName = '$_prefix/event_channel';

  static const String updateTextCaptureOverlay = 'updateTextCaptureOverlay';
  static const String addTextCaptureListenerName = 'addTextCaptureListener';
  static const String textCaptureFinishDidCaptureName = 'textCaptureFinishDidCapture';
  static const String getLastFrameDataName = 'getLastFrameData';
  static const String removeTextCaptureListenerName = 'removeTextCaptureListener';
  static const String setModeEnabledState = 'setModeEnabledState';
  static const String updateTextCaptureMode = 'updateTextCaptureMode';
  static const String applyTextCaptureModeSettings = 'applyTextCaptureModeSettings';
}
