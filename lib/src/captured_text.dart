/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

import 'package:scandit_flutter_datacapture_core/scandit_flutter_datacapture_core.dart';

@Deprecated("Text Capture mode is deprecated.")
class CapturedText {
  final String _value;

  @Deprecated("Text Capture mode is deprecated.")
  String get value {
    return _value;
  }

  final Quadrilateral _location;

  @Deprecated("Text Capture mode is deprecated.")
  Quadrilateral get location {
    return _location;
  }

  CapturedText._(this._location, this._value);

  CapturedText.fromJSON(Map<String, dynamic> json)
      : this._(Quadrilateral.fromJSON(json['location']), json['value'] as String);
}
