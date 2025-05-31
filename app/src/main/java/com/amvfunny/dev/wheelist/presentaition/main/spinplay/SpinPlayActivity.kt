package com.amvfunny.dev.wheelist.presentaition.main.spinplay

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.TIME_DELAY_GET_RESULT
import com.amvfunny.dev.wheelist.base.common.eventbus.CloseSpinEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.eventbus.IEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.ReloadNativeHome
import com.amvfunny.dev.wheelist.base.common.extention.IStateData
import com.amvfunny.dev.wheelist.base.common.extention.coroutinesLaunch
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppDrawable
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.handleStateData
import com.amvfunny.dev.wheelist.base.common.extention.loadImage
import com.amvfunny.dev.wheelist.base.common.extention.screenShot
import com.amvfunny.dev.wheelist.base.common.extention.setGradientMain
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.setUpGradient
import com.amvfunny.dev.wheelist.base.common.extention.show
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.databinding.SpinPlayActivityBinding
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.widget.spinview.SPIN_TYPE
import com.amvfunny.dev.wheelist.presentaition.widget.spinview.SpinViewLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class SpinPlayActivity : SpinWheelActivity<SpinPlayActivityBinding>() {

    companion object {
        private const val SIZE_DEFAULT = 1
        const val TYPE_DATA = "TYPE_DATA"
    }

    private val viewModel by viewModels<SpinPlayViewModel>()

    private var player: ExoPlayer? = null

    private val adapter by lazy { SpinAdapter() }

    private var isStart = false

    override fun setBinding(layoutInflater: LayoutInflater): SpinPlayActivityBinding {
        return SpinPlayActivityBinding.inflate(layoutInflater)
    }

    override fun setUpView() {
        super.setUpView()

        binding.clHomeHeader.setGradientMain()

        binding.ivSpinPlayClose.setOnSafeClick {
            onBackPressed()
        }

        binding.spvSpinPlay.setSpinType(viewModel.spinType)
        binding.spvSpinPlay.listener = object : SpinViewLayout.ISpinViewCallback {
            override fun onComplete(isComplete: Boolean, type: SPIN_TYPE?) {
                if (isComplete) {
                    lifecycleScope.launch {
                        delay(TIME_DELAY_GET_RESULT)
                        val dataBitmap = screenShot(binding.spvSpinPlay)
                        val bStream = ByteArrayOutputStream()
                        dataBitmap.compress(Bitmap.CompressFormat.PNG, 50, bStream)
                        val byteArray = bStream.toByteArray()
                        navigateTo(
                            ResultSpinActivity::class.java,
                            bundleOf(ResultSpinActivity.DATA_BITMAP_KEY to byteArray)
                        )
                        binding.spvSpinPlay.playAgain()
                        releasePlayer()
                    }
                }
            }

            override fun onStart() {
                isStart = true
            }
        }


        binding.tvSpinPlaySize.text = SIZE_DEFAULT.toString()
        binding.spvSpinPlay.setSizeSpin(SIZE_DEFAULT)

        when (viewModel.spinType) {
            SPIN_TYPE.CHOOSE -> {
                binding.spvSpinPlay.setSizeSpin(1)
                binding.tvSpinPlayTitle.text =
                    getAppString(R.string.chooser_title, this@SpinPlayActivity)
                binding.tvSpinPlaySize.text = "1"
            }

            SPIN_TYPE.COUPLE -> {
                binding.spvSpinPlay.setSizeSpin(2)
                binding.tvSpinPlaySize.text = "2"
                binding.tvSpinPlayTitle.text =
                    getAppString(R.string.homograft_title, this@SpinPlayActivity)
            }

            SPIN_TYPE.RANK -> {
                binding.tvSpinPlayTitle.text =
                    getAppString(R.string.ranking_title, this@SpinPlayActivity)
                binding.llSpinPlaySize.gone()
            }
        }

        binding.llSpinPlaySize.setOnSafeClick {
            binding.llSpinPlaySize.isSelected = !binding.llSpinPlaySize.isSelected
            if (binding.llSpinPlaySize.isSelected) {
                binding.cvSpinPlay.show()
                binding.ivSpinPlayMore.loadImage(getAppDrawable(R.drawable.ic_arrow_up_size))
                setUpViewShowLess()
            } else {
                binding.cvSpinPlay.gone()
                binding.ivSpinPlayMore.loadImage(getAppDrawable(R.drawable.ic_arrow_down_size))
                setUpViewShowMore()
            }
        }

        setUpTitle()

        binding.cvSpinPlay.background = setUpGradient(
            intArrayOf(
                getAppColor(R.color.white),
                getAppColor(R.color.white)
            ),
            getAppDimension(R.dimen.dimen_12),
            strokeColor = getAppColor(R.color.color_n4),
            strokeWidth = getAppDimension(R.dimen.dimen_1).toInt()
        )
    }

    override fun onEvent(event: IEvent) {
        super.onEvent(event)
        when (event) {
            is CloseSpinEvent -> {
                finish()
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
        if (SpinWheelPreferences.isTurnOnBackGroundMusic == true) {
            initializePlayer(R.raw.background_spin)
        }
    }

    override fun onStop() {
        super.onStop()
        EventBusManager.instance?.unregister(this)
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusManager.instance?.postPending(ReloadNativeHome())
        releasePlayer()
    }

    override fun observerData() {
        super.observerData()
        coroutinesLaunch(viewModel.spinListState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    adapter.submitList(it.data)
                }
            })
        }
    }

    override fun setUpAdapter() {
        super.setUpAdapter()
        binding.rvSpinPlay.adapter = adapter
        binding.rvSpinPlay.layoutManager = LinearLayoutManager(this@SpinPlayActivity)

        adapter.listener = object : SpinAdapter.ISpinListener {
            override fun onSelectSize(size: Int?) {
                binding.tvSpinPlaySize.text = size.toString()
                binding.spvSpinPlay.resetCanShow()
                binding.cvSpinPlay.gone()
                size?.let {
                    binding.spvSpinPlay.setSizeSpin(it)
                }
                binding.ivSpinPlayMore.loadImage(getAppDrawable(R.drawable.ic_arrow_down_size))
                setUpViewShowMore()
                viewModel.setState(size)
            }
        }
    }

    private fun setUpViewShowMore() {
        binding.llSpinPlaySize.background =
            getAppDrawable(R.drawable.shape_bg_light_stroke_corner_8)
        binding.tvSpinPlaySize.setTextColor(getAppColor(R.color.white))
        binding.tvSpinPlaySizeTitle.setTextColor(getAppColor(R.color.white))
    }

    private fun setUpViewShowLess() {
        binding.llSpinPlaySize.background =
            getAppDrawable(R.drawable.shape_bg_white_light_transparent_stroke_corner_8)
        binding.tvSpinPlaySize.setTextColor(getAppColor(R.color.n8))
        binding.tvSpinPlaySizeTitle.setTextColor(getAppColor(R.color.n6))
    }

    private fun setUpTitle() {
        when (viewModel.spinType) {
            SPIN_TYPE.CHOOSE -> {
                binding.tvSpinPlayTitle.text =
                    getAppString(R.string.chooser_title, this@SpinPlayActivity)
            }

            SPIN_TYPE.COUPLE -> {
                binding.tvSpinPlayTitle.text =
                    getAppString(R.string.homograft_title, this@SpinPlayActivity)
            }

            SPIN_TYPE.RANK -> {
                binding.tvSpinPlayTitle.text =
                    getAppString(R.string.ranking_title, this@SpinPlayActivity)
            }
        }
    }

    private fun initializePlayer(resource: Int) {
        if (player == null) {
            player = ExoPlayer.Builder(this@SpinPlayActivity)
                .build()
                .also { exoPlayer ->
                    val file =
                        Uri.parse("android.resource://${this@SpinPlayActivity.packageName}/${resource}")
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