package com.amvfunny.dev.wheelist.presentaition.main.setting

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.amvfunny.dev.wheelist.BuildConfig
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.eventbus.ReloadNativeHome
import com.amvfunny.dev.wheelist.base.common.extention.IStateData
import com.amvfunny.dev.wheelist.base.common.extention.coroutinesLaunch
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.handleStateData
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.setUpGradient
import com.amvfunny.dev.wheelist.base.common.extention.shareDefault
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.databinding.SettingActivityBinding
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.languague.LANGUAGE_TYPE
import com.amvfunny.dev.wheelist.presentaition.languague.LanguageActivity
import com.amvfunny.dev.wheelist.presentaition.main.setting.rate.RATE_TYPE
import com.amvfunny.dev.wheelist.presentaition.main.setting.rate.RateDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingActivity : SpinWheelActivity<SettingActivityBinding>() {

    private val viewModel by viewModels<SettingViewModel>()
    val LINK_APP =
        "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"

    private var manager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null

    override fun setBinding(layoutInflater: LayoutInflater): SettingActivityBinding {
        return SettingActivityBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        binding.tvSettingBackgroundMusic.text = getAppString(R.string.background_music_title, this)
        binding.tvSettingResultSound.text = getAppString(R.string.result_sound_title, this)
        binding.tvSettingRate.text = getAppString(R.string.rate_title, this)
        binding.tvSettingShare.text = getAppString(R.string.share_title, this)
        binding.tvSettingLanguage.text = getAppString(R.string.language_title, this)
        binding.tvSettingPrivacy.text = getAppString(R.string.privacy_title, this)
        binding.tvSettingTitle.text = getAppString(R.string.setting_title, this)
        viewModel.setStateLanguage(LANGUAGE_TYPE.getType(SpinWheelPreferences.valueCodeLanguage))
    }

    override fun setUpView() {
        super.setUpView()

        binding.clHomeHeader.background = setUpGradient(
            intArrayOf(
                getAppColor(R.color.orange_primary_light),
                getAppColor(R.color.orange_primary_bold),
            )
        )

        binding.swSettingBackgroundMusic.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setStateBackgroundMusic(isChecked)
        }

        binding.swSettingResultSound.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setStateResultSound(isChecked)
        }

        binding.ivSettingClose.setOnSafeClick {
            onBackPressed()
        }

        binding.clSettingRate.setOnSafeClick {
            val rateDialog = RateDialog()

            rateDialog.listener = object : RateDialog.IRateCallBack {
                override fun onRate() {
                    manager = ReviewManagerFactory.create(this@SettingActivity)
                    val request = manager?.requestReviewFlow()
                    request?.addOnCompleteListener { task: Task<ReviewInfo> ->
                        if (task.isSuccessful) {
                            reviewInfo = task.result
                            val flow =
                                manager?.launchReviewFlow(
                                    this@SettingActivity,
                                    reviewInfo ?: task.result
                                )
                            flow?.addOnSuccessListener { result: Void? ->
                                SpinWheelPreferences.isRate = true
                                Toast.makeText(
                                    this@SettingActivity,
                                    getAppString(
                                        R.string.rated_content,
                                        this@SettingActivity,
                                        RATE_TYPE.RATE_5.value.toString()
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                                rateDialog.dismiss()
                            }
                        } else {
                            Toast.makeText(
                                this@SettingActivity,
                                getAppString(
                                    R.string.rated_content,
                                    this@SettingActivity,
                                    RATE_TYPE.RATE_5.value.toString()
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                            rateDialog.dismiss()
                        }
                    }
                }

                override fun onDismiss() {

                }
            }

            rateDialog.show(supportFragmentManager, rateDialog.tag)
        }

        binding.clSettingShare.setOnSafeClick {
            shareDefault(LINK_APP)
        }

        binding.clSettingLanguage.setOnSafeClick {
            navigateTo(LanguageActivity::class.java)
        }

        binding.clSettingPrivacy.setOnSafeClick {
            try {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/live/89fe3282-83da-40e7-b957-5b2e9fe25c64"))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun observerData() {
        super.observerData()
        coroutinesLaunch(viewModel.stateBackGroundMusic) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    binding.swSettingBackgroundMusic.isChecked = it.data ?: false
                }
            })
        }

        coroutinesLaunch(viewModel.stateResultSound) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    binding.swSettingResultSound.isChecked = it.data ?: false
                }
            })
        }

        coroutinesLaunch(viewModel.stateLanguage) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    when (it.data) {
                        LANGUAGE_TYPE.ENGLISH -> {
                            binding.tvSettingLanguageContent.text =
                                getAppString(R.string.en_title, this@SettingActivity)
                        }

                        LANGUAGE_TYPE.SPANISH -> {
                            binding.tvSettingLanguageContent.text =
                                getAppString(R.string.spanish_title, this@SettingActivity)
                        }

                        LANGUAGE_TYPE.HINDI -> {
                            binding.tvSettingLanguageContent.text =
                                getAppString(R.string.hindi_title, this@SettingActivity)
                        }

                        LANGUAGE_TYPE.FRENCH -> {
                            binding.tvSettingLanguageContent.text =
                                getAppString(R.string.french_title, this@SettingActivity)
                        }

                        LANGUAGE_TYPE.PORTUGUESE -> {
                            binding.tvSettingLanguageContent.text =
                                getAppString(R.string.portuguese_title, this@SettingActivity)
                        }

                        LANGUAGE_TYPE.VIETNAMESE -> {
                            binding.tvSettingLanguageContent.text =
                                getAppString(R.string.vietnameese_title, this@SettingActivity)
                        }

                        else -> {
                            binding.tvSettingLanguageContent.text =
                                getAppString(R.string.en_title, this@SettingActivity)
                        }
                    }
                }
            })
        }
    }

    override fun onDestroy() {
        EventBusManager.instance?.postPending(ReloadNativeHome())
        super.onDestroy()
    }
}