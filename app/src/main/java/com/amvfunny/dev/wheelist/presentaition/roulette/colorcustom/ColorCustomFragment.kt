package com.amvfunny.dev.wheelist.presentaition.roulette.colorcustom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.DialogScreen
import com.amvfunny.dev.wheelist.base.common.eventbus.CustomColorEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelDialog
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelFragment
import com.amvfunny.dev.wheelist.databinding.ColorCustomFragmentBinding

class ColorCustomFragment: SpinWheelDialog<ColorCustomFragmentBinding>(R.layout.color_custom_fragment) {

    companion object{
        private const val COLOR_KEY = "COLOR_KEY"
        fun getInstance(color: Int?): ColorCustomFragment{
            val fra = ColorCustomFragment()
            fra.arguments = bundleOf(COLOR_KEY to color)
            return fra
        }
    }

    private var color: Int? = null

    override fun getBackgroundId() = R.id.flColorCustomDlgAddRoot

    override fun screen(): DialogScreen {
        return DialogScreen(
            isDismissByTouchOutSide = true,
            isDismissByOnBackPressed = true
        )
    }

    override fun onInitView() {
        super.onInitView()
        color = arguments?.getInt(COLOR_KEY)

        binding.kcpColorCustom.alphaSliderView = binding.kasColorCustom
        binding.kcpColorCustom.hueSliderView = binding.khsColorCustom

        binding.kcpColorCustom.setOnColorChangedListener {
            color = it
        }

        binding.flColorCustomDlgAdd.setGradientButton(getAppDimension(R.dimen.dimen_12))

        binding.cvColorCustomDlgCancel.setOnSafeClick {
            dismiss()
        }

        binding.cvColorCustomDlgAdd.setOnSafeClick {
            EventBusManager.instance?.postPending(CustomColorEvent(color))
            dismiss()
        }
    }
}