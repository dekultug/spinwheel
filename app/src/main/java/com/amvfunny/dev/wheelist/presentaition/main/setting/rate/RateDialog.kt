package com.amvfunny.dev.wheelist.presentaition.main.setting.rate

import android.widget.Toast
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.DialogScreen
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppDrawable
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.loadImage
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.setUpGradient
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelDialog
import com.amvfunny.dev.wheelist.databinding.RateDlgBinding
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import kotlin.math.roundToInt

class RateDialog : SpinWheelDialog<RateDlgBinding>(R.layout.rate_dlg) {

    private var rateType = RATE_TYPE.RATE_5

    var listener: IRateCallBack? = null

    override fun getBackgroundId() = R.id.flRateDlgRoot

    override fun screen(): DialogScreen {
        return DialogScreen(
            isFullHeight = true,
            isFullWidth = true,
            isDismissByOnBackPressed = false,
            isDismissByTouchOutSide = false,
        )
    }

    override fun onInitView() {
        binding.rbRate.setOnStarChangeListener { ratingBar, star ->
            rateType = RATE_TYPE.getType(star.roundToInt())
            setUpContent()
        }

        binding.tvRateExit.setOnSafeClick {
            listener?.onDismiss()
            dismiss()
        }
        binding.tvRate.setOnSafeClick {
            if (rateType.value > RATE_TYPE.RATE_3.value) {
                listener?.onRate()
            } else {
                Toast.makeText(
                    requireContext(),
                    getAppString(R.string.rated_content, requireContext()),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }
        binding.tvRate.background = setUpGradient(
            intArrayOf(
                getAppColor(R.color.orange_background),
                getAppColor(R.color.orange_gradient_second),
            ),
            getAppDimension(R.dimen.dimen_12)
        )
        setUpContent()
    }

    private fun setUpContent() {
        when (rateType) {
            RATE_TYPE.DEFAULT -> {
                binding.tvRateTitle.text =
                    getAppString(R.string.rate_default_title, requireContext())
                binding.tvRateContent.text =
                    getAppString(R.string.rate_default_content, requireContext())
                binding.ivRateTop.loadImage(getAppDrawable(R.drawable.bg_rate_default))
            }

            RATE_TYPE.RATE_1 -> {
                binding.tvRateTitle.text = getAppString(R.string.rate_1_title, requireContext())
                binding.tvRateContent.text = getAppString(R.string.rate_1_content, requireContext())
                binding.ivRateTop.loadImage(getAppDrawable(R.drawable.bg_rate_1))
            }

            RATE_TYPE.RATE_2 -> {
                binding.tvRateTitle.text = getAppString(R.string.rate_2_title, requireContext())
                binding.tvRateContent.text = getAppString(R.string.rate_2_content, requireContext())
                binding.ivRateTop.loadImage(getAppDrawable(R.drawable.bg_rate_2))
            }

            RATE_TYPE.RATE_3 -> {
                binding.tvRateTitle.text = getAppString(R.string.rate_3_title, requireContext())
                binding.tvRateContent.text = getAppString(R.string.rate_3_content, requireContext())
                binding.ivRateTop.loadImage(getAppDrawable(R.drawable.bg_rate_3))
            }

            RATE_TYPE.RATE_4 -> {
                binding.tvRateTitle.text = getAppString(R.string.rate_4_title, requireContext())
                binding.tvRateContent.text = getAppString(R.string.rate_4_content, requireContext())
                binding.ivRateTop.loadImage(getAppDrawable(R.drawable.bg_rate_4))
            }

            RATE_TYPE.RATE_5 -> {
                binding.tvRateTitle.text = getAppString(R.string.rate_5_title, requireContext())
                binding.tvRateContent.text = getAppString(R.string.rate_5_content, requireContext())
                binding.ivRateTop.loadImage(getAppDrawable(R.drawable.bg_rate_5))
            }
        }
    }

    interface IRateCallBack {
        fun onRate()
        fun onDismiss()
    }
}