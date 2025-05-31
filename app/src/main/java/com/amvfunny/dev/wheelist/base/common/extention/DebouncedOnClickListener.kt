package com.amvfunny.dev.wheelist.base.common.extention

import android.view.View
import java.lang.System.currentTimeMillis

abstract class DebouncedOnClickListener(
    private val delayBetweenClicks: Long = DEFAULT_DEBOUNCE_INTERVAL
) : View.OnClickListener {
    private var lastClickTimestamp = -1L

    abstract fun onDebouncedClick(v: View)

    override fun onClick(v: View) {
        val now = currentTimeMillis()
        if (lastClickTimestamp == -1L || now >= (lastClickTimestamp + delayBetweenClicks)) {
            onDebouncedClick(v)
        }
        lastClickTimestamp = now
    }
}

private var lastClickTimestampGlobal = -1L

abstract class GlobalDebouncedOnClickListener(
    private val delayBetweenClicks: Long = DEFAULT_DEBOUNCE_INTERVAL
) : View.OnClickListener {

    abstract fun onDebouncedClick(v: View)

    override fun onClick(v: View) {
        val now = currentTimeMillis()
        if (lastClickTimestampGlobal == -1L || now >= (lastClickTimestampGlobal + delayBetweenClicks)) {
            onDebouncedClick(v)
        }
        lastClickTimestampGlobal = now
    }
}

