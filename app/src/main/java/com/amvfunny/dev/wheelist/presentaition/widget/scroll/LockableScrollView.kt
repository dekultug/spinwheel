package com.amvfunny.dev.wheelist.presentaition.widget.scroll

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class LockableScrollView : ScrollView {
    // true if we can scroll (not locked)
    // false if we cannot scroll (locked)
    var isScrollable: Boolean = true
        private set

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setScrollingEnabled(enabled: Boolean) {
        isScrollable = enabled
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) { // if we can scroll pass the event to the superclass
            return isScrollable && super.onTouchEvent(ev)
        }
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        return isScrollable && super.onInterceptTouchEvent(ev)
    }
}