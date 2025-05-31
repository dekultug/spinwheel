package com.amvfunny.dev.wheelist.presentaition.languague

enum class LANGUAGE_TYPE(val value: String) {
    ENGLISH("en"),
    SPANISH("es"),
    FRENCH("fr"),
    HINDI("hi"),
    PORTUGUESE("pt");

    companion object {
        fun getType(value: String?): LANGUAGE_TYPE {
            return entries.find { it.value == value } ?: ENGLISH
        }
    }
}