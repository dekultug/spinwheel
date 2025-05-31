package com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.eventbus.IEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.ReLoadNativeAllRoulette
import com.amvfunny.dev.wheelist.base.common.eventbus.ReloadNativeAllSpinRoulette
import com.amvfunny.dev.wheelist.base.common.eventbus.SettingRouletteEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.UpdateWheelEvent
import com.amvfunny.dev.wheelist.base.common.extention.IStateData
import com.amvfunny.dev.wheelist.base.common.extention.coroutinesLaunch
import com.amvfunny.dev.wheelist.base.common.extention.disable
import com.amvfunny.dev.wheelist.base.common.extention.enable
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.handleStateData
import com.amvfunny.dev.wheelist.base.common.extention.hide
import com.amvfunny.dev.wheelist.base.common.extention.screenShot
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.extention.setGradientMain
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.shareImage
import com.amvfunny.dev.wheelist.base.common.extention.show
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.data.model.WHEEL_TYPE
import com.amvfunny.dev.wheelist.databinding.SpinRouletteActivityBinding
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.roulette.RouletteActivity
import com.amvfunny.dev.wheelist.presentaition.roulette.RouletteActivity.Companion.SEE_LIST_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditroulette.AddEditRouletteActivity
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditroulette.AddEditRouletteActivity.Companion.IS_EDIT_FROM_SPIN_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditroulette.AddEditRouletteActivity.Companion.WHEEL_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.settingspinwheel.SettingRouletteActivity
import com.amvfunny.dev.wheelist.presentaition.roulette.settingspinwheel.SettingRouletteActivity.Companion.VALUE_SPEED_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.settingspinwheel.SettingRouletteActivity.Companion.VALUE_STOP_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.settingspinwheel.SettingRouletteActivity.Companion.VALUE_TIME_KEY
import dagger.hilt.android.AndroidEntryPoint
import rubikstudio.library.LuckyWheelView

@AndroidEntryPoint
class SpinRouletteActivity: SpinWheelActivity<SpinRouletteActivityBinding>() {

    companion object {
        const val WHEEL_DATA_KEY = "WHEEL_DATA_KEY"
        const val TITLE_KEY = "TITLE_KEY"

        // preview
        const val REPEAT_KEY = "REPEAT_KEY"
        const val PREVIEW_LIST_OPTION_KEY = "PREVIEW_LIST_OPTION_KEY"
    }

    private val viewModel by viewModels<SpinWheelViewModel>()

    private var player: ExoPlayer? = null

    private var duration: Int? = null
    private var isInputStop = false
    private var speed: Int? = null

    override fun setBinding(layoutInflater: LayoutInflater): SpinRouletteActivityBinding {
        return SpinRouletteActivityBinding.inflate(layoutInflater)
    }

    override fun onEvent(event: IEvent) {
        super.onEvent(event)
        when (event) {
            is UpdateWheelEvent -> {
                event.wheel?.let {
                    binding.tvSpinRouletteWinnerTitle.text = event.wheel.title
                    viewModel.updateWheel(wheel = event.wheel)
                }
                EventBusManager.instance?.removeSticky(event)
            }

            is SettingRouletteEvent -> {
                duration = event.valueTime
                isInputStop = event.valueStop
                speed = event.valueSpeed
                EventBusManager.instance?.removeSticky(event)
            }
            is ReloadNativeAllSpinRoulette ->{
                EventBusManager.instance?.removeSticky(event)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBusManager.instance?.register(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        EventBusManager.instance?.unregister(this)
        releasePlayer()
    }

    override fun onDestroy(){
        super.onDestroy()
        EventBusManager.instance?.postPending(ReLoadNativeAllRoulette())
        releasePlayer()
    }

    override fun setUpView() {
        super.setUpView()

        binding.tvSpinRouletteWinnerTitle.text = if (viewModel._wheel == null){
            viewModel.title
        }else{
            when {
                viewModel._wheel!!.getTypeWheel() == WHEEL_TYPE.EAT -> {
                    getAppString(R.string.wheel_init_1, this)
                }

                viewModel._wheel!!.getTypeWheel() == WHEEL_TYPE.EATOPTION -> {
                    getAppString(R.string.wheel_init_1, this)
                }

                viewModel._wheel!!.getTypeWheel()== WHEEL_TYPE.WINNER -> {
                    getAppString(R.string.wheel_init_2, this)
                }

                viewModel._wheel!!.getTypeWheel() == WHEEL_TYPE.YESNO -> {
                    getAppString(R.string.wheel_init_3, this)
                }

                else -> {
                    viewModel.title
                }
            }
        }

        binding.ivSpinRouletteClose.setOnSafeClick {
            onBackPressed()
        }
        binding.lwvSpinRoulette.setLuckyRoundItemSelectedListener(object :
            LuckyWheelView.LuckyRoundItemSelectedListener {
            @SuppressLint("SetTextI18n", "SuspiciousIndentation")
            override fun LuckyRoundItemSelected(index: Int) {
                val listData = viewModel.listLuckyState.value.data
                if (!listData.isNullOrEmpty()) {

                    if (index in listData.indices) {
                        binding.tvSpinRouletteWinner.text = listData[index].secondaryText
                        binding.tvSpinRouletteWinner.setTextColor(listData[index].color)
                    } else {
                        binding.tvSpinRouletteWinner.text = listData[0].secondaryText
                        binding.tvSpinRouletteWinner.setTextColor(listData[0].color)
                    }

                    binding.tvSpinRouletteWinner.show()
                    binding.lavSpinRouletteAnimation.show()
                    binding.llSpinRouletteShare.show()
                    binding.tvSpinRouletteSpin.isSelected = false
                    binding.tvSpinRouletteSpin.text =
                        getAppString(R.string.spin_title, this@SpinRouletteActivity)

                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.lavSpinRouletteAnimation.gone()
                    }, 10000)

                    releasePlayer()
                    if (SpinWheelPreferences.isTurnOnResultSound == true) {
                        initializePlayer(R.raw.result_wheel)
                    }

                    viewModel.setStateStart(false)
                }
            }
        })
        binding.lwvSpinRoulette.disable()

        binding.tvSpinRouletteSpin.setOnSafeClick {
            if (viewModel.listLuckyState.value.data?.isEmpty() == false) {
                if (isInputStop) {
                    binding.tvSpinRouletteSpin.isSelected = !binding.tvSpinRouletteSpin.isSelected
                    if (!binding.tvSpinRouletteSpin.isSelected) {
                        binding.tvSpinRouletteSpin.text =
                            getAppString(R.string.spin_title, this@SpinRouletteActivity)
                        binding.lwvSpinRoulette.stopAnimation()
                    } else {
                        binding.tvSpinRouletteSpin.text = getAppString(R.string.stop_title, this@SpinRouletteActivity)
                        viewModel.setStateStart(true)
                        releasePlayer()
                        if (SpinWheelPreferences.isTurnOnBackGroundMusic == true) {
                            initializePlayer(R.raw.background_wheel)
                        }
                    }
                } else {
                    releasePlayer()
                    viewModel.setStateStart(true)
                    if (SpinWheelPreferences.isTurnOnBackGroundMusic == true) {
                        initializePlayer(R.raw.background_wheel)
                    }
                }
                binding.llSpinRouletteShare.hide()
                binding.lavSpinRouletteAnimation.gone()
            } else {
                Toast.makeText(
                    this@SpinRouletteActivity,
                    getAppString(R.string.ask_has_option, this@SpinRouletteActivity),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.ivSpinRouletteEdit.setOnSafeClick {
            viewModel._wheel?.let {
                navigateTo(AddEditRouletteActivity::class.java, bundleOf(WHEEL_KEY to it, IS_EDIT_FROM_SPIN_KEY to true))
            }
        }

        binding.clSpinRouletteHeader.setGradientMain()
        binding.tvSpinRouletteSpin.setGradientButton(getAppDimension(R.dimen.dimen_12))

        binding.ivSpinRouletteSetting.setOnSafeClick {
            navigateTo(SettingRouletteActivity::class.java, bundleOf(
                VALUE_TIME_KEY to duration,
                VALUE_SPEED_KEY to speed,
                VALUE_STOP_KEY to isInputStop
            ))
        }

        binding.llSpinRouletteShare.setOnSafeClick {
            shareImage(screenShot(binding.clSpinRoultteMain));
        }

        binding.ivSpinRouletteList.setOnSafeClick {
            navigateTo(RouletteActivity::class.java, bundleOf(SEE_LIST_KEY to true))
        }
    }

    override fun onBackPressed() {
        navigateTo(RouletteActivity::class.java, isClearTop = true, isBack = true)
        finish()
    }

    override fun observerData() {
        super.observerData()
        coroutinesLaunch(viewModel.listLuckyState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    binding.lwvSpinRoulette.setData(it.data)
                    binding.lwvSpinRoulette.setLuckyWheelTextColor(getAppColor(R.color.white))
                    if (duration == null) {
                        duration = binding.lwvSpinRoulette.getDefaultDuration().toInt() / 1000
                    }
                }
            })
        }

        coroutinesLaunch(viewModel.startState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    if (it.data == true) {
                        binding.lwvSpinRoulette.startLuckyWheelWithTargetIndex(
                            viewModel.getRandomIndex(),
                            duration,
                            speed
                        )
                        if (!isInputStop) binding.tvSpinRouletteSpin.disable()
                        binding.ivSpinRouletteClose.disable()
                        binding.ivSpinRouletteEdit.disable()
                        binding.ivSpinRouletteList.disable()
                        binding.ivSpinRouletteSetting.disable()
                    } else {
                        binding.tvSpinRouletteSpin.enable()
                        binding.ivSpinRouletteClose.enable()
                        binding.ivSpinRouletteSetting.enable()
                        binding.ivSpinRouletteEdit.enable()
                        binding.ivSpinRouletteList.enable()
                    }
                }
            })
        }
    }

    private fun initializePlayer(resource: Int) {
        if (player == null) {
            player = ExoPlayer.Builder(this)
                .build()
                .also { exoPlayer ->
                    val file =
                        Uri.parse("android.resource://${this.packageName}/${resource}")
                    val mediaItem = MediaItem.fromUri(file)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                }
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            exoPlayer.stop()
            exoPlayer.release()
        }
        player = null
    }
}