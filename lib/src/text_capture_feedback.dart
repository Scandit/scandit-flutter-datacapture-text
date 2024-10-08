/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2021- Scandit AG. All rights reserved.
 */

import 'package:scandit_flutter_datacapture_core/scandit_flutter_datacapture_core.dart';

@Deprecated("Text Capture mode is deprecated.")
class TextCaptureFeedback implements Serializable {
  Feedback success = Feedback.defaultFeedback;

  @Deprecated("Text Capture mode is deprecated.")
  static TextCaptureFeedback get defaultFeedback {
    return TextCaptureFeedback();
  }

  @override
  Map<String, dynamic> toMap() {
    return {'success': success.toMap()};
  }
}
