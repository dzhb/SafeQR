package com.dzhb.safeqr;

import android.graphics.Bitmap;

/**
 * 用于检测二维码水印处
 */

public interface CheckWatermarkImpl {
    void check(Bitmap bitmap);
}
