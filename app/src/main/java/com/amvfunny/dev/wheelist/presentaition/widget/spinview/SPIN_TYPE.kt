package com.amvfunny.dev.wheelist.presentaition.widget.spinview

enum class SPIN_TYPE(val value: Int) {
    CHOOSE(0),
    COUPLE(1),
    RANK(2);

    companion object {
        fun getType(value: Int?): SPIN_TYPE = entries.find { it.value == value } ?: CHOOSE
    }
}