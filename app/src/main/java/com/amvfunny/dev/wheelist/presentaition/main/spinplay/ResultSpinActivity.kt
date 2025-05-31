package com.amvfunny.dev.wheelist.presentaition.main.spinplay

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.eventbus.CloseSpinEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.extention.setGradientMain
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.show
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.databinding.ResultSpinActivityBinding
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.main.setting.rate.RATE_TYPE
import com.amvfunny.dev.wheelist.presentaition.main.setting.rate.RateDialog

class ResultSpinActivity : SpinWheelActivity<ResultSpinActivityBinding>() {

    companion object {
        const val DATA_BITMAP_KEY = "DATA_BITMAP_KEY"
    }

    private var bitmapData: Bitmap? = null

    private var player: ExoPlayer? = null

    private var manager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null

    override fun setBinding(layoutInflater: LayoutInflater): ResultSpinActivityBinding {
        return ResultSpinActivityBinding.inflate(layoutInflater)
    }

    override fun setUpView() {
        super.setUpView()
        if (intent.hasExtra(DATA_BITMAP_KEY)) {
            //convert to bitmap
            val byteArray = intent.getByteArrayExtra(DATA_BITMAP_KEY) ?: return
            bitmapData = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            binding.ivResultSpin.setImageBitmap(bitmapData)
        }

        binding.tvResultSpinTryAgain.setOnSafeClick {
            finish()
        }

        binding.ivResultSpinClose.setOnSafeClick {
            finish()
        }

        binding.ivResultSpinHome.setOnSafeClick {
            var currentClick = SpinWheelPreferences.countClickHome ?: 1
            if (SpinWheelPreferences.isRate == true) {
                EventBusManager.instance?.postPending(CloseSpinEvent())
                finish()
            } else {
                if (currentClick == 1 || currentClick == 3 || currentClick == 5) {
                    val rateDialog = RateDialog()
                    rateDialog.show(supportFragmentManager, rateDialog.tag)
                    rateDialog.listener = object : RateDialog.IRateCallBack {
                        override fun onRate() {

                            manager = ReviewManagerFactory.create(this@ResultSpinActivity)
                            val request = manager?.requestReviewFlow()
                            request?.addOnCompleteListener { task: Task<ReviewInfo> ->
                                if (task.isSuccessful) {
                                    reviewInfo = task.result
                                    val flow =
                                        manager?.launchReviewFlow(this@ResultSpinActivity, reviewInfo?: task.result)
                                    flow?.addOnSuccessListener { result: Void? ->
                                        binding.rlResultSpinAdmob.show()
                                        currentClick += 1
                                        SpinWheelPreferences.countClickHome = currentClick
                                        EventBusManager.instance?.postPending(CloseSpinEvent())
                                        SpinWheelPreferences.isRate  = true
                                        Toast.makeText(this@ResultSpinActivity, getAppString(R.string.rated_content, this@ResultSpinActivity, RATE_TYPE.RATE_5.value.toString()), Toast.LENGTH_SHORT).show()
                                        rateDialog.dismiss()
                                        finish()
                                    }
                                } else {
                                    binding.rlResultSpinAdmob.show()
                                    currentClick += 1
                                    SpinWheelPreferences.countClickHome = currentClick
                                    EventBusManager.instance?.postPending(CloseSpinEvent())
                                    finish()
                                }
                            }
                        }

                        override fun onDismiss() {
                            binding.rlResultSpinAdmob.show()
                            currentClick += 1
                            SpinWheelPreferences.countClickHome = currentClick
                            EventBusManager.instance?.postPending(CloseSpinEvent())
                            finish()
                        }
                    }
                    binding.rlResultSpinAdmob.gone()
                } else {
                    currentClick += 1
                    SpinWheelPreferences.countClickHome = currentClick
                    EventBusManager.instance?.postPending(CloseSpinEvent())
                    finish()
                }
            }
        }

        binding.clHomeHeader.setGradientMain()
        binding.tvResultSpinTryAgain.setGradientButton(getAppDimension(R.dimen.dimen_16))
    }

    override fun onResume() {
        super.onResume()
        if (SpinWheelPreferences.isTurnOnResultSound == true) {
            initializePlayer(R.raw.result_spin)
        }
    }

    private fun initializePlayer(resource: Int) {
        if (player == null) {
            player = ExoPlayer.Builder(this@ResultSpinActivity)
                .build()
                .also { exoPlayer ->
                    val file =
                        Uri.parse("android.resource://${this@ResultSpinActivity.packageName}/${resource}")
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

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}