package com.amvfunny.dev.wheelist.presentaition.roulette.settingspinwheel

import android.view.LayoutInflater
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.eventbus.ReloadNativeAllSpinRoulette
import com.amvfunny.dev.wheelist.base.common.eventbus.SettingRouletteEvent
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.extention.setGradientMain
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.base.common.util.AdsConfig
import com.amvfunny.dev.wheelist.databinding.SettingRouletteActivityBinding
import com.amvfunny.dev.wheelist.presentaition.widget.slider.SPIN_SLIDER_TYPE

class SettingRouletteActivity : SpinWheelActivity<SettingRouletteActivityBinding>() {

    companion object {
        const val VALUE_TIME_KEY = "VALUE_TIME_KEY"
        const val VALUE_SPEED_KEY = "VALUE_SPEED_KEY"
        const val VALUE_STOP_KEY = "VALUE_STOP_KEY"
    }

    private var valueTime: Int? = null
    private var valueSpeed: Int? = null
    private var valueStop: Boolean = false

    override fun setBinding(layoutInflater: LayoutInflater): SettingRouletteActivityBinding {
        return SettingRouletteActivityBinding.inflate(layoutInflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusManager.instance?.postPending(ReloadNativeAllSpinRoulette())
    }

    override fun setUpView() {
        super.setUpView()

        valueTime = intent?.getIntExtra(VALUE_TIME_KEY,3)
        valueSpeed = intent?.getIntExtra(VALUE_SPEED_KEY,2)
        valueStop = intent?.getBooleanExtra(VALUE_STOP_KEY,false) ?: false

        binding.clSettingRouletteHeader.setGradientMain()
        binding.tvSettingRouletteSave.setGradientButton(getAppDimension(R.dimen.dimen_12))

        binding.ssvSettingRouletteTime.apply {
            setType(SPIN_SLIDER_TYPE.DURATION)
            setStep(3, 10)
            setShowTime(true)
            setValue(valueTime?: 3, true)
        }

        binding.ssvSettingRouletteSpeed.apply {
            setType(SPIN_SLIDER_TYPE.SPEED)
            setStep(1, 3)
            setValue(valueSpeed?: 2)
        }

        binding.swSettingRouletteStop.isChecked = valueStop

        binding.tvSettingRouletteSave.setOnSafeClick {
            EventBusManager.instance?.postPending(
                SettingRouletteEvent(
                    valueTime = binding.ssvSettingRouletteTime.getValueStep(),
                    valueSpeed = binding.ssvSettingRouletteSpeed.getValueStep(),
                    valueStop = binding.swSettingRouletteStop.isChecked
                )
            )
           finish()
        }

        binding.ivSettingRouletteClose.setOnSafeClick {
            finish()
        }
    }
}