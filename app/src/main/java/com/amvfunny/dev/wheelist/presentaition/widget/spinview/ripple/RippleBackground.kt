package com.amvfunny.dev.wheelist.presentaition.widget.spinview.ripple

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import java.util.Random
import kotlin.math.min

open class RippleBackground : RelativeLayout {
    companion object {
        const val DEFAULT_RIPPLE_COUNT = 3
        private const val DEFAULT_DURATION_TIME = 1000
        private const val DEFAULT_SCALE = 1.5f
        private const val DEFAULT_FILL_TYPE = 0
        const val OFF_LIMIT_REMOVE_VIEW = 10
    }

    val SIZE_CIRCLE = getAppDimension(R.dimen.dimen_80).toInt()

    protected var rippleStrokeWidth = 0f
    private var rippleRadius = 0f
    protected var rippleDurationTime = 0
    protected var rippleAmount = 0
    protected var rippleDelay = 0
    protected var rippleScale = 0f
    private var rippleType = 0

    constructor(context: Context?) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    @SuppressLint("ResourceType")
    private fun init(context: Context, attrs: AttributeSet?) {
        if (isInEditMode) return

        requireNotNull(attrs) { "Attributes should be provided to this view," }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleBackground)

        rippleStrokeWidth = typedArray.getDimension(
            R.styleable.RippleBackground_rb_strokeWidth,
            resources.getDimension(R.dimen.rippleStrokeWidth)
        )
        rippleRadius = typedArray.getDimension(
            R.styleable.RippleBackground_rb_radius,
            resources.getDimension(R.dimen.rippleRadius)
        )
        rippleDurationTime =
            typedArray.getInt(R.styleable.RippleBackground_rb_duration, DEFAULT_DURATION_TIME)
        rippleAmount =
            typedArray.getInt(R.styleable.RippleBackground_rb_rippleAmount, DEFAULT_RIPPLE_COUNT)
        rippleScale = typedArray.getFloat(R.styleable.RippleBackground_rb_scale, DEFAULT_SCALE)
        rippleType = typedArray.getInt(R.styleable.RippleBackground_rb_type, DEFAULT_FILL_TYPE)
        typedArray.recycle()

        rippleDelay = rippleDurationTime / rippleAmount
    }

    inner class RippleView(context: Context?) : View(context) {
        private var paint: Paint = Paint()

        init {
            this.visibility = INVISIBLE
            paint.isAntiAlias = true

            if (rippleType == DEFAULT_FILL_TYPE) {
                rippleStrokeWidth = 0f
                paint.style = Paint.Style.FILL
            } else paint.style = Paint.Style.STROKE
        }

        override fun onDraw(canvas: Canvas) {
            val radius =
                (min(width.toDouble(), height.toDouble()) / 2).toInt()
            canvas.drawCircle(
                radius.toFloat(), radius.toFloat(), radius - rippleStrokeWidth,
                paint
            )
        }

        @SuppressLint("ResourceAsColor")
        fun setColor(color: Int?) {
            Log.d("TAG", "setColor: $color")
            if (color == null) {
                paint.setColor(android.R.color.transparent)
            } else {
                paint.setColor(color)
            }
            invalidate()
        }

        fun getColor() = paint.color
    }
}