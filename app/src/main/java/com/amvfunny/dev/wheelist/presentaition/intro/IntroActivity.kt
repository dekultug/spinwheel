package com.amvfunny.dev.wheelist.presentaition.intro

import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.viewpager2.widget.ViewPager2
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.databinding.IntroActivityBinding
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.main.guideplay.GuideActivity
import com.amvfunny.dev.wheelist.presentaition.main.guideplay.GuideActivity.Companion.IS_SHOW_FIRST_KEY

class IntroActivity : SpinWheelActivity<IntroActivityBinding>() {

    private var isNext = false
    private val adapter by lazy { IntroAdapter() }
    override fun setBinding(layoutInflater: LayoutInflater): IntroActivityBinding {
        return IntroActivityBinding.inflate(layoutInflater)
    }

    override fun setUpView() {
        super.setUpView()
        setStatusColor(getAppColor(R.color.orange_primary_bold))
        binding.vp2Intro.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                isNext = position >= INTRO_TYPE.entries.size - 1
            }
        })

        binding.tvIntroNext.setOnSafeClick {
            if (isNext) {
                navigateTo(GuideActivity::class.java, bundleOf(IS_SHOW_FIRST_KEY to true))
                finish()
            } else {
                binding.vp2Intro.currentItem += 1
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        SpinWheelPreferences.isUsingApp = false
    }

    override fun setUpAdapter() {
        binding.vp2Intro.adapter = adapter
        adapter.submitList(INTRO_TYPE.entries)
        binding.dicIntro.attachTo(binding.vp2Intro)
    }
}