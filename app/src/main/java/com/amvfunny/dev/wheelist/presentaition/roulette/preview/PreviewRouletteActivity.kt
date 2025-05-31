package com.amvfunny.dev.wheelist.presentaition.roulette.preview

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.eventbus.ReloadNativeAllAddEdit
import com.amvfunny.dev.wheelist.base.common.eventbus.UpdateWheelRouletteEvent
import com.amvfunny.dev.wheelist.base.common.extention.IStateData
import com.amvfunny.dev.wheelist.base.common.extention.coroutinesLaunch
import com.amvfunny.dev.wheelist.base.common.extention.disable
import com.amvfunny.dev.wheelist.base.common.extention.enable
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.handleStateData
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.extention.setGradientMain
import com.amvfunny.dev.wheelist.base.common.extention.setGradientPurple
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.show
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.data.model.WHEEL_TYPE
import com.amvfunny.dev.wheelist.databinding.PreviewRouletteActivityBinding
import com.amvfunny.dev.wheelist.presentaition.roulette.RouletteActivity
import dagger.hilt.android.AndroidEntryPoint
import rubikstudio.library.LuckyWheelView

@AndroidEntryPoint
class PreviewRouletteActivity : SpinWheelActivity<PreviewRouletteActivityBinding>() {

    companion object {
        const val DATA_SPIN_KEY = "DATA_SPIN_KEY"
        const val REPEAT_COUNT_KEY = "REPEAT_COUNT_KEY"
        const val TITLE_KEY = "TITLE_KEY"
        const val TYPE_WHEEL_KEY = "TYPE_WHEEL_KEY"

        const val WHEEL_PREVIEW_KEY = "WHEEL_PREVIEW_KEY"
    }

    private val viewModel by viewModels<PreviewViewModel>()

    override fun setBinding(layoutInflater: LayoutInflater): PreviewRouletteActivityBinding {
        return PreviewRouletteActivityBinding.inflate(layoutInflater)
    }

    override fun setUpView() {
        super.setUpView()

        binding.clPreviewRouletteHeader.setGradientMain()

        binding.tvPreviewRouletteWinnerTitle.text = when {
            viewModel.type == WHEEL_TYPE.EAT -> {
                getAppString(R.string.wheel_init_1, this)
            }

            viewModel.type == WHEEL_TYPE.EATOPTION -> {
                getAppString(R.string.wheel_init_1, this)
            }

            viewModel.type == WHEEL_TYPE.WINNER -> {
                getAppString(R.string.wheel_init_2, this)
            }

            viewModel.type == WHEEL_TYPE.YESNO -> {
                getAppString(R.string.wheel_init_3, this)
            }

            else -> {
                viewModel.title
            }
        }

        binding.ivPreviewRouletteClose.setOnSafeClick {
            if (viewModel.isSaveAndSpin) {
                viewModel.addWheel()
            } else {
                finish()
            }
        }

        binding.tvPreviewRouletteEdit.setGradientPurple(getAppDimension(R.dimen.dimen_12))
        binding.tvPreviewRouletteSaveSpin.setGradientButton(getAppDimension(R.dimen.dimen_12))

        binding.lwvPreviewRoulette.setLuckyRoundItemSelectedListener(object :
            LuckyWheelView.LuckyRoundItemSelectedListener {
            @SuppressLint("SetTextI18n", "SuspiciousIndentation")
            override fun LuckyRoundItemSelected(index: Int) {
                val listData = viewModel.listLuckyState.value.data
                if (!listData.isNullOrEmpty()) {
                    if (index in listData.indices) {
                        binding.tvPreviewRouletteWinner.text = "${listData[index].secondaryText}"
                        binding.tvPreviewRouletteWinner.setTextColor(listData[index].color)
                    } else {
                        binding.tvPreviewRouletteWinner.text = "${listData[0].secondaryText}"
                        binding.tvPreviewRouletteWinner.setTextColor(listData[0].color)
                    }
                    binding.tvPreviewRouletteWinner.show()
                    binding.lavPreviewRouletteAnimation.show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.lavPreviewRouletteAnimation.gone()
                    }, 10000)
                    viewModel.setStateStart(false)
                }
            }
        })
        binding.lwvPreviewRoulette.disable()

        binding.tvPreviewRouletteSaveSpin.setOnSafeClick {
            if (!viewModel.listLuckyState.value.data.isNullOrEmpty()) {
                viewModel.setStateStart(true)
            }
        }

        binding.tvPreviewRouletteEdit.setOnSafeClick {
            finish()
        }

        loadNative()
    }

    override fun observerData() {
        super.observerData()
        coroutinesLaunch(viewModel.listLuckyState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    binding.lwvPreviewRoulette.setData(it.data)
                    binding.lwvPreviewRoulette.setLuckyWheelTextColor(getAppColor(R.color.white))
                }
            })
        }

        coroutinesLaunch(viewModel.startState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    if (it.data == true) {
                        binding.lwvPreviewRoulette.startLuckyWheelWithTargetIndex(viewModel.getRandomIndex())
                        binding.tvPreviewRouletteEdit.disable()
                        binding.ivPreviewRouletteClose.disable()
                        binding.tvPreviewRouletteSaveSpin.disable()
                    } else {
                        binding.tvPreviewRouletteEdit.enable()
                        binding.ivPreviewRouletteClose.enable()
                        binding.tvPreviewRouletteSaveSpin.enable()
                    }
                }
            })
        }

        coroutinesLaunch(viewModel.saveAndSpinState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    EventBusManager.instance?.postPending(UpdateWheelRouletteEvent(viewModel.wheelPreview?.copy()))
                    if (it.data == true) {
                        navigateTo(RouletteActivity::class.java, isClearTop = true, isBack = true)
                        finish()
                    }
                }
            })
        }
    }

    private fun loadNative() {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (!viewModel.isSaveAndSpin) {
            EventBusManager.instance?.postPending(ReloadNativeAllAddEdit())
        }
    }
}