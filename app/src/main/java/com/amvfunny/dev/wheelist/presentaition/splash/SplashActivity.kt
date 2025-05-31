package com.amvfunny.dev.wheelist.presentaition.splash

import android.content.Intent
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.amvfunny.dev.wheelist.BuildConfig
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.TIME_DELAY_NEXT_SCREEN
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.setUpGradient
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.base.common.util.InAppUpdate
import com.amvfunny.dev.wheelist.base.common.util.InstallUpdatedListener
import com.amvfunny.dev.wheelist.databinding.SplashActivityBinding
import com.amvfunny.dev.wheelist.presentaition.languague.LanguageActivity
import com.amvfunny.dev.wheelist.presentaition.languague.LanguageActivity.Companion.START_APP_KEY
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : SpinWheelActivity<SplashActivityBinding>() {

    private lateinit var inAppUpdate: InAppUpdate

    private val TAG = "SplashActivity"

    override fun setBinding(layoutInflater: LayoutInflater): SplashActivityBinding {
        return SplashActivityBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        if (::inAppUpdate.isInitialized)
            inAppUpdate.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::inAppUpdate.isInitialized) {
            inAppUpdate.onDestroy()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (this::inAppUpdate.isInitialized)
            inAppUpdate.onActivityResult(requestCode, resultCode, data)
    }

    override fun setUpView() {
        super.setUpView()
        binding.clSplashRoot.background = setUpGradient(
            intArrayOf(
                getAppColor(R.color.orange_primary_light),
                getAppColor(R.color.orange_primary_bold)
            )
        )
        getRemoteConfig()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun initAdsSplash() {
        nextScreen()
    }

    private fun nextScreen(hasDelay: Boolean = true) {
        lifecycleScope.launch {
            if (hasDelay) {
                delay(TIME_DELAY_NEXT_SCREEN)
            }
            navigateTo(LanguageActivity::class.java, bundleOf(START_APP_KEY to true))
            finish()
        }
    }

    private fun getRemoteConfigBoolean(adUnitId: String): Boolean {
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        return mFirebaseRemoteConfig.getBoolean(adUnitId)
    }

    private fun getRemoteConfigLong(adUnitId: String): Long {
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        return mFirebaseRemoteConfig.getLong(adUnitId)
    }

    private fun getRemoteConfig() {
        val index: Long = BuildConfig.Minimum_Fetch
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(index)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        mFirebaseRemoteConfig.fetchAndActivate()
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    inAppUpdate =
                        InAppUpdate(this, mFirebaseRemoteConfig.getBoolean("force_update"), object :
                            InstallUpdatedListener {
                            override fun onUpdateNextAction() {
                                initAdsSplash()
                            }

                            override fun onUpdateCancel() {
                                finish()
                            }
                        })
                } else {
                    initAdsSplash()
                }
            }
    }
}