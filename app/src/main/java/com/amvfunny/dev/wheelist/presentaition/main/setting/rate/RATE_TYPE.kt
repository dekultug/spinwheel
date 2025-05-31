package com.amvfunny.dev.wheelist.presentaition.main.setting.rate

enum class RATE_TYPE(val value: Int) {
    DEFAULT(0),
    RATE_1(1),
    RATE_2(2),
    RATE_3(3),
    RATE_4(4),
    RATE_5(5);

    companion object {
        fun getType(value: Int): RATE_TYPE = values().find { it.value == value } ?: DEFAULT
    }
}