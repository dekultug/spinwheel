package com.amvfunny.dev.wheelist.presentaition.main.guideplay

import android.view.LayoutInflater
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.eventbus.ReloadNativeHome
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.setUpGradient
import com.amvfunny.dev.wheelist.base.common.extention.show
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.databinding.GuidePlayActivityBinding
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.main.HomeActivity

class GuideActivity : SpinWheelActivity<GuidePlayActivityBinding>() {

    companion object {
        const val IS_SHOW_FIRST_KEY = "IS_SHOW_FIRST_KEY"
    }

    private var isFirst = false

    override fun setBinding(layoutInflater: LayoutInflater): GuidePlayActivityBinding {
        return GuidePlayActivityBinding.inflate(layoutInflater)
    }

    override fun setUpView() {
        super.setUpView()

        isFirst = intent?.getBooleanExtra(IS_SHOW_FIRST_KEY, false) ?: false

        if (isFirst) {
            binding.ivGuidePlayBack.gone()
        } else {
            binding.ivGuidePlayBack.show()
        }

        binding.clHomeHeader.background = setUpGradient(
            intArrayOf(
                getAppColor(R.color.orange_primary_light),
                getAppColor(R.color.orange_primary_bold),
            )
        )

        if (SpinWheelPreferences.isFirstUseApp == false && isFirst) {
            binding.tvGuidePlaySkip.show()
        } else {
            binding.tvGuidePlaySkip.gone()
        }

        binding.tvGuidePlaySkip.setOnSafeClick {
            if (isFirst) {
                navigateTo(HomeActivity::class.java)
            }
            finish()
        }

        binding.ivGuidePlayBack.setOnSafeClick {
            onBackPressed()
        }

        binding.tvGuidePlayGotIt.setGradientButton(radius = getAppDimension(R.dimen.dimen_12))

        binding.tvGuidePlayGotIt.setOnSafeClick {
            if (isFirst) {
                navigateTo(HomeActivity::class.java)
            }
            finish()
        }
    }

    override fun onDestroy() {
        if (!isFirst) {
            EventBusManager.instance?.postPending(ReloadNativeHome())
        }
        super.onDestroy()
    }
}