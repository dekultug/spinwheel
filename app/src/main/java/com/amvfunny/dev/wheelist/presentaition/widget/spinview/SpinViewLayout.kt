package com.amvfunny.dev.wheelist.presentaition.widget.spinview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.disable
import com.amvfunny.dev.wheelist.base.common.extention.enable
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppDrawable
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.loadImage
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.extention.setGradientMain
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.show

class SpinViewLayout constructor(
    private val ctx: Context,
    attributeSet: AttributeSet?
) : ConstraintLayout(ctx, attributeSet) {

    companion object {
        const val TIME_DELAY = 5000L
    }

    private var root: ConstraintLayout? = null
    private var lottieView: LottieAnimationView? = null
    private var spinView: SpinView? = null

    private var llGuide: ConstraintLayout? = null
    private var ivCloseGuide: ImageView? = null
    private var tvGuideContent: TextView? = null
    private var ivGuide: ImageView? = null

    private var spinType: SPIN_TYPE? = null
    private var canToast: Boolean? = null

    private var isCallback = false

    var listener: ISpinViewCallback? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.spin_view_layout, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        root = findViewById(R.id.clSpinViewLayoutRoot)
        lottieView = findViewById(R.id.lavSpinViewAnimation)
        spinView = findViewById(R.id.spinView)
        llGuide = findViewById(R.id.llSpinViewGuide)
        ivCloseGuide = findViewById(R.id.ivSpinViewGuideClose)
        tvGuideContent = findViewById(R.id.tvSpinViewGuideContent)
        ivGuide = findViewById(R.id.ivSpinViewGuide)

        setEventView()
    }

    private fun setEventView() {

        Handler(Looper.getMainLooper()).postDelayed({
            if (llGuide?.isVisible == true) {
                llGuide?.gone()
                listener?.onStart()
            }
        }, TIME_DELAY)

        ivCloseGuide?.setOnSafeClick {
            llGuide?.gone()
            listener?.onStart()
        }

        llGuide?.setGradientButton(getAppDimension(R.dimen.dimen_16))

        spinView?.listener = object : SpinView.ISpinListener {
            override fun onComplete() {
                spinView?.disable()
                lottieView?.show()
                if (!isCallback) {
                    listener?.onComplete(true, spinType)
                    isCallback = true
                }
            }

            override fun onToastAskMore(canToast: Boolean) {
                if (this@SpinViewLayout.canToast == null) {
                    this@SpinViewLayout.canToast = canToast
                }
                if (this@SpinViewLayout.canToast == true) {
                    Toast.makeText(
                        ctx,
                        getAppString(R.string.ask_more_point, ctx),
                        Toast.LENGTH_SHORT
                    ).show()
                    this@SpinViewLayout.canToast = false
                }
            }
        }
    }

    private fun setUpGuide() {
        when {
            spinType == SPIN_TYPE.RANK -> {
                ivGuide?.loadImage(getAppDrawable(R.drawable.ic_rank_play_spin))
                tvGuideContent?.text = getAppString(R.string.rank_guide_title, ctx)
            }

            spinType == SPIN_TYPE.CHOOSE -> {
                val param = ivGuide?.layoutParams as ViewGroup.MarginLayoutParams
                param.topMargin = getAppDimension(R.dimen.dimen_10).toInt()
                param.leftMargin = getAppDimension(R.dimen.dimen_10).toInt()
                ivGuide?.layoutParams = param
                ivGuide?.loadImage(getAppDrawable(R.drawable.ic_choose_play_spin))
                tvGuideContent?.text = getAppString(R.string.choose_guide_title, ctx)
            }

            spinType == SPIN_TYPE.COUPLE -> {
                ivGuide?.loadImage(getAppDrawable(R.drawable.ic_homo_play_spin))
                tvGuideContent?.text = getAppString(R.string.homo_guide_title, ctx)
            }
        }
    }

    fun setSpinType(type: SPIN_TYPE) {
        spinType = type
        spinView?.setType(type)
        setUpGuide()
    }

    fun setSizeSpin(size: Int) {
        spinView?.setSize(size)
    }

    fun resetCanShow() {
        canToast = null
    }

    fun playAgain() {
        spinView?.again()
        spinView?.enable()
        lottieView?.gone()
        isCallback = false
        this@SpinViewLayout.canToast = null
        listener?.onComplete(false, spinType)
    }

    interface ISpinViewCallback {
        fun onComplete(isComplete: Boolean, type: SPIN_TYPE?)
        fun onStart()
    }
}