package com.amvfunny.dev.wheelist.presentaition.widget.slider

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.FLOAT_DEFAULT
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppDrawable
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.hideKeyBoard
import com.amvfunny.dev.wheelist.base.common.extention.setGradientMain
import com.amvfunny.dev.wheelist.base.common.extention.show
import kotlin.math.roundToInt


class SpinSliderView constructor(
    private val ctx: Context,
    attributeSet: AttributeSet?
) : ConstraintLayout(ctx, attributeSet) {

    companion object {
        private const val TAG = "SpinSliderView"
    }

    private var clRoot: ConstraintLayout? = null
    private var ivProgress: ImageView? = null

    // view
    private var vBackground: View? = null
    private var vProgress: View? = null

    // left
    private var flLeftBackground: FrameLayout? = null
    private var tvLeftContent: TextView? = null

    // center
    private var flBackground: FrameLayout? = null
    private var tvContent: TextView? = null

    // right
    private var flRightBackground: FrameLayout? = null
    private var tvRightContent: TextView? = null

    private var widthScreen: Float? = null

    private var sliderType = SPIN_SLIDER_TYPE.SPEED

    private var minStep = 1
    private var maxStep = 20
    private var valueStep = 1

    private var isShowTime = false

    var listener: ISpinSliderListener? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.spin_slider_layout, this, true)
        initView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthScreen =
            MeasureSpec.getSize(widthMeasureSpec) - 2 * getAppDimension(R.dimen.dimen_16)
        // 32 là do 20 margin và 12 do padding
    }

    private fun initView() {
        clRoot = findViewById(R.id.clSpinSliderRoot)
        ivProgress = findViewById(R.id.ivSpinSlider)

        vBackground = findViewById(R.id.vSpinSliderBackground)
        vProgress = findViewById(R.id.vSpinSliderProgress)

        // left
        flLeftBackground = findViewById(R.id.flSpinSliderLeftBackground)
        tvLeftContent = findViewById(R.id.tvSpinSliderLeftContent)

        // center
        flBackground = findViewById(R.id.flSpinSliderBackground)
        tvContent = findViewById(R.id.tvSpinSliderContent)

        // right
        flRightBackground = findViewById(R.id.flSpinSliderRightBackground)
        tvRightContent = findViewById(R.id.tvSpinSliderRightContent)

        setEventView()
    }

    private fun setEventView() {
        vBackground?.background = getAppDrawable(R.drawable.shape_bg_orange_divider_corner_4)
        vProgress?.setGradientMain(getAppDimension(R.dimen.dimen_4))
        dragLeft()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun dragLeft() {
        var originMarginLeft = 0
        var coordinateX = 0f
        ivProgress?.setOnTouchListener { v, event ->
            v.hideKeyBoard()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    originMarginLeft = (ivProgress?.layoutParams as MarginLayoutParams).leftMargin
                    coordinateX = event.rawX
                }

                MotionEvent.ACTION_MOVE -> {
                    val newMarginLeft = event.rawX - coordinateX + originMarginLeft
                    updateLeft(newMarginLeft)
                    listener?.onMove()
                }

                MotionEvent.ACTION_UP -> {
                    val leftMargin = (ivProgress?.layoutParams as MarginLayoutParams).leftMargin
                    if (sliderType == SPIN_SLIDER_TYPE.SPEED) {
                        when {
                            leftMargin >= 0 && leftMargin <= 2 * widthScreen!! / 5 -> {
                                updateLeft(0f)
                            }

                            leftMargin > 2 * widthScreen!! / 5 && leftMargin <= 4 * widthScreen!! / 5 -> {
                                updateLeft(widthScreen!! / 2)
                            }

                            leftMargin > 4 * widthScreen!! / 5 && leftMargin <= widthScreen!! -> {
                                updateLeft(widthScreen!!)
                            }
                        }
                    }
                    listener?.onUp()
                    listener?.onCallbackValue(valueStep)
                }
            }
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateLeft(leftMargin: Float, isSetValue: Boolean = false) {
        if (widthScreen == null) return
        val min = FLOAT_DEFAULT
        val newLeft = when {
            leftMargin <= min -> {
                setUiLeft()
                min
            }

            leftMargin > widthScreen!! - getAppDimension(R.dimen.dimen_18) -> {
                setUiRight()
                widthScreen!! - getAppDimension(R.dimen.dimen_18)
            }

            else -> {
                setUiCenter()
                leftMargin
            }
        }

        if (!isSetValue) {
            Log.d(
                TAG,
                "updateLeft: ${(((newLeft * (maxStep - minStep)) / widthScreen!!) + minStep)}"
            )
            if (sliderType == SPIN_SLIDER_TYPE.DURATION) {
                valueStep =
                    (((newLeft * (maxStep - minStep)) / widthScreen!!) + minStep).roundToInt()
                tvContent?.text = valueStep.toString() + addSubTime()
                tvLeftContent?.text = valueStep.toString() + addSubTime()
                tvRightContent?.text = valueStep.toString() + addSubTime()
            } else {
                valueStep =
                    (((newLeft * (maxStep - minStep)) / widthScreen!!) + minStep).roundToInt()
                tvContent?.text = getStringValue(valueStep)
                tvLeftContent?.text = getStringValue(valueStep)
                tvRightContent?.text = getStringValue(valueStep)
            }
        }

        val newParam = ivProgress?.layoutParams as MarginLayoutParams
        newParam.leftMargin = newLeft.toInt()
        ivProgress?.layoutParams = newParam
    }

    private fun setUiLeft() {
        flLeftBackground?.show()
        flBackground?.gone()
        flRightBackground?.gone()
        if (sliderType == SPIN_SLIDER_TYPE.DURATION) {
            flLeftBackground?.background = getAppDrawable(R.drawable.bg_slider_duration_left)
        } else {
            flLeftBackground?.background = getAppDrawable(R.drawable.bg_slider_speed_left)
        }
    }

    private fun setUiRight() {
        flRightBackground?.show()
        flBackground?.gone()
        flLeftBackground?.gone()
        if (sliderType == SPIN_SLIDER_TYPE.DURATION) {
            flRightBackground?.background = getAppDrawable(R.drawable.bg_slider_duration_right)
        } else {
            flRightBackground?.background = getAppDrawable(R.drawable.bg_slider_speed_right)
        }
    }

    private fun setUiCenter() {
        flBackground?.show()
        flLeftBackground?.gone()
        flRightBackground?.gone()
        if (sliderType == SPIN_SLIDER_TYPE.DURATION) {
            flBackground?.background = getAppDrawable(R.drawable.bg_slider_duration_center)
        } else {
            flBackground?.background = getAppDrawable(R.drawable.bg_slider_speed_center)
        }
    }

    private fun getStringValue(value: Int): String {
        return when (value) {
            1 -> getAppString(R.string.slow_title, ctx)
            2 -> getAppString(R.string.normal_title, ctx)
            3 -> getAppString(R.string.fast_title, ctx)
            else -> getAppString(R.string.normal_title, ctx)
        }
    }

    private fun addSubTime(): String {
        return if (isShowTime) "s" else ""
    }

    fun hideUi() {
        flLeftBackground?.gone()
        flBackground?.gone()
        flRightBackground?.gone()
    }

    fun setStep(min: Int, max: Int) {
        minStep = min
        maxStep = max
    }

    fun setType(type: SPIN_SLIDER_TYPE) {
        sliderType = type
    }

    fun setValue(value: Int, isShowTime: Boolean = false) {
        post {
            this@SpinSliderView.isShowTime = isShowTime
            valueStep = value
            tvContent?.text = valueStep.toString()
            tvLeftContent?.text = valueStep.toString()
            tvRightContent?.text = valueStep.toString()
            val newLeft = when (value) {
                minStep -> 0f
                maxStep -> widthScreen!!
                else -> ((value - minStep) * widthScreen!!) / (maxStep - minStep)
            }
            if (sliderType == SPIN_SLIDER_TYPE.DURATION) {
                updateLeft(newLeft, false)
            } else {
                when {
                    newLeft >= 0 && newLeft <= 2 * widthScreen!! / 5 -> {
                        updateLeft(0f)
                    }

                    newLeft > 2 * widthScreen!! / 5 && newLeft <= 4 * widthScreen!! / 5 -> {
                        updateLeft(widthScreen!! / 2)
                    }

                    newLeft > 4 * widthScreen!! / 5 && newLeft <= widthScreen!! -> {
                        updateLeft(widthScreen!!)
                    }
                }
            }
        }
    }

    fun setShowTime(isShow: Boolean) {
        isShowTime = isShow
    }

    fun getValueStep() = valueStep

    interface ISpinSliderListener {
        fun onCallbackValue(value: Int) {}
        fun onUp() {}
        fun onMove() {}
    }
}