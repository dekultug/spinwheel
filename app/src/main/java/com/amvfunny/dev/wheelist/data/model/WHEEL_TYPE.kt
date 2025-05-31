package com.amvfunny.dev.wheelist.data.model

import androidx.room.Entity


enum class WHEEL_TYPE(val value: Int) {
    EAT(1),
    WINNER(2),
    YESNO(3),
    EATOPTION(4),
    CUSTOM(-1),
    EXAMPLE(-999);

    companion object {
        fun getType(value: Int?) = entries.find { it.value == value } ?: CUSTOM
    }
}