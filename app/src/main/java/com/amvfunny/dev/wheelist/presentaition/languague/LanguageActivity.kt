package com.amvfunny.dev.wheelist.presentaition.languague

import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.IStateData
import com.amvfunny.dev.wheelist.base.common.extention.coroutinesLaunch
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.handleStateData
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.show
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.databinding.LanguageActivityBinding
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.intro.IntroActivity
import com.amvfunny.dev.wheelist.presentaition.main.HomeActivity
import com.amvfunny.dev.wheelist.presentaition.main.guideplay.GuideActivity
import com.amvfunny.dev.wheelist.presentaition.main.guideplay.GuideActivity.Companion.IS_SHOW_FIRST_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageActivity : SpinWheelActivity<LanguageActivityBinding>() {

    companion object {
        const val START_APP_KEY = "START_APP_KEY"
    }

    private val viewModel by viewModels<LanguageViewModel>()

    private val adapter by lazy { LanguageAdapter() }
    private var selectedLanguageCode = ""

    override fun setBinding(layoutInflater: LayoutInflater): LanguageActivityBinding {
        return LanguageActivityBinding.inflate(layoutInflater)
    }

    override fun observerData() {
        super.observerData()
        coroutinesLaunch(viewModel.languageState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    adapter.submitList(it.data)
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setStatusColor(getAppColor(R.color.orange_primary_light))
    }

    override fun setUpView() {
        super.setUpView()
        setStatusColor(getAppColor(R.color.n10))
        binding.ivLanguageSelect.setOnSafeClick {
            SpinWheelPreferences.valueCodeLanguage = selectedLanguageCode
            SpinWheelPreferences.isUsingApp = true
            if (viewModel.currentLanguage != viewModel.type) {
                SpinWheelPreferences.isHasIntroApp = viewModel.isStartApp
            }
            if (viewModel.isStartApp == true) {
                navigateTo(GuideActivity::class.java, bundleOf(IS_SHOW_FIRST_KEY to true))
            }
            finish()
        }

        if (viewModel.isStartApp == true) {
            binding.ivLanguageBack.gone()
            binding.ivLanguageSelect.alpha = 0.2f
            binding.ivLanguageSelect.isEnabled = false
        } else {
            binding.ivLanguageBack.show()
            binding.ivLanguageSelect.alpha = 1f
            binding.ivLanguageSelect.isEnabled = true
        }

        binding.ivLanguageBack.setOnSafeClick {
            finish()
        }
    }

    override fun setUpAdapter() {
        binding.rvLanguage.adapter = adapter
        binding.rvLanguage.layoutManager = LinearLayoutManager(this)

        adapter.listener = object : LanguageAdapter.ILanguageListener {
            override fun onSelectType(type: LANGUAGE_TYPE) {
                selectedLanguageCode = type.value
                viewModel.selectLanguage(type)
                binding.ivLanguageSelect.alpha = 1f
                binding.ivLanguageSelect.isEnabled = true
            }
        }
        if (viewModel.isStartApp == false) {
            val currentLanguage = LANGUAGE_TYPE.getType(SpinWheelPreferences.valueCodeLanguage)
            viewModel.selectLanguage(currentLanguage)
            selectedLanguageCode = SpinWheelPreferences.valueCodeLanguage ?: "en"
        }
    }
}