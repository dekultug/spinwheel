package com.amvfunny.dev.wheelist.base

import android.view.WindowManager
import androidx.annotation.ColorRes

class DialogScreen(
    var mode: DIALOG_MODE = DIALOG_MODE.NORMAL,
    var isFullWidth: Boolean = true,
    var isFullHeight: Boolean = true,
    var isDismissByTouchOutSide: Boolean = true,
    var isDismissByOnBackPressed: Boolean = true
) {
    enum class DIALOG_MODE {
        SCALE,
        NORMAL,
        BOTTOM
    }
}

const val TIME_DELAY_NEXT_SCREEN = 2000L
const val TIME_SAFE_ACTION = 350L
const val TIME_SCREEN_SHOT = 1000L
const val TIME_DELAY_GET_RESULT = 1500L
