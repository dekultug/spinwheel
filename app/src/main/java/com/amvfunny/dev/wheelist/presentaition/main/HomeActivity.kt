package com.amvfunny.dev.wheelist.presentaition.main

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.extention.IStateData
import com.amvfunny.dev.wheelist.base.common.extention.coroutinesLaunch
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.handleStateData
import com.amvfunny.dev.wheelist.base.common.extention.setLocale
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.setUpGradient
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.base.common.util.SystemUtil
import com.amvfunny.dev.wheelist.databinding.HomeActivityBinding
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.languague.LANGUAGE_TYPE
import com.amvfunny.dev.wheelist.presentaition.main.guideplay.GuideActivity
import com.amvfunny.dev.wheelist.presentaition.main.setting.SettingActivity
import com.amvfunny.dev.wheelist.presentaition.main.setting.rate.RATE_TYPE
import com.amvfunny.dev.wheelist.presentaition.main.setting.rate.RateDialog
import com.amvfunny.dev.wheelist.presentaition.main.spinplay.SpinPlayActivity
import com.amvfunny.dev.wheelist.presentaition.roulette.RouletteActivity
import com.amvfunny.dev.wheelist.presentaition.widget.spinview.SPIN_TYPE

class HomeActivity : SpinWheelActivity<HomeActivityBinding>() {

    private val TAG = "HomeActivity"

    private val adapter by lazy { HomeAdapter() }

    private val viewModel by viewModels<HomeViewModel>()

    private var manager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null

    private val itemSpanLookSize = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            val item = adapter.currentList[position]
            return when (item) {
                HOME_TYPE.CHOOSER, HOME_TYPE.HOMOGRAFT -> 2
                else -> 2
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        Log.d(TAG, "attachBaseContext: ")
        super.attachBaseContext(
            newBase?.setLocale(
                SpinWheelPreferences.valueCodeLanguage ?: LANGUAGE_TYPE.ENGLISH.value
            )
        )
    }

    override fun onStart() {
        super.onStart()
        EventBusManager.instance?.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusManager.instance?.unregister(this)
    }

    override fun setBinding(layoutInflater: LayoutInflater): HomeActivityBinding {
        return HomeActivityBinding.inflate(layoutInflater)
    }

    override fun setUpView() {
        Log.d(TAG, "setUpView: ")
        initAds()
        super.setUpView()
        SystemUtil.setLocale(this)
        binding.ivHomeGuidePlay.setOnSafeClick {
            navigateTo(GuideActivity::class.java)
        }
        binding.ivHomeSetting.setOnSafeClick {
            navigateTo(SettingActivity::class.java)
        }

        binding.clHomeHeader.background = setUpGradient(
            intArrayOf(
                getAppColor(R.color.orange_primary_light),
                getAppColor(R.color.orange_primary_bold),
            )
        )

        SpinWheelPreferences.isFirstUseApp = false
    }

    override fun removeListener() {
        super.removeListener()
        adapter.listener = null
    }

    override fun observerData() {
        super.observerData()
        coroutinesLaunch(viewModel.homeListState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    Log.d(TAG, "onSuccess: ${it.data?.size}")
                    adapter.submitList(it.data?.toList())
                }
            })
        }
    }

    override fun setUpAdapter() {
        super.setUpAdapter()
        val girdLayoutManager = GridLayoutManager(this, 2)
        girdLayoutManager.spanSizeLookup = itemSpanLookSize
        binding.rvHome.layoutManager = girdLayoutManager
        binding.rvHome.adapter = adapter

        adapter.listener = object : HomeAdapter.IHomeListener {
            override fun onPlay(type: HOME_TYPE) {
                when (type) {
                    HOME_TYPE.ROULETTE -> {
                        navigateTo(RouletteActivity::class.java)
                    }

                    HOME_TYPE.RANKING -> {
                        navigateTo(
                            SpinPlayActivity::class.java,
                            bundleOf(SpinPlayActivity.TYPE_DATA to SPIN_TYPE.RANK.value)
                        )
                    }

                    HOME_TYPE.CHOOSER -> {
                        navigateTo(
                            SpinPlayActivity::class.java,
                            bundleOf(SpinPlayActivity.TYPE_DATA to SPIN_TYPE.CHOOSE.value)
                        )
                    }

                    HOME_TYPE.HOMOGRAFT -> {
                        navigateTo(
                            SpinPlayActivity::class.java,
                            bundleOf(SpinPlayActivity.TYPE_DATA to SPIN_TYPE.COUPLE.value)
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (SpinWheelPreferences.isRate == true) {
            val dlg = ExitAppDlg()
            viewModel.deleteNative()
            dlg.listener = object : ExitAppDlg.IExitAppCallBack {
                override fun onCancel() {
                    viewModel.getHomeList()
                }

                override fun onExit() {
                    super.onExit()
                    finishAffinity()
                }
            }
            dlg.show(supportFragmentManager, "ExitApp")
        } else {
            var currentBack = SpinWheelPreferences.countClickBack ?: 1
            if (currentBack == 2 || currentBack == 4 || currentBack == 6) {
                val rateDialog = RateDialog()
                viewModel.deleteNative()
                rateDialog.show(supportFragmentManager, rateDialog.tag)
                rateDialog.listener = object : RateDialog.IRateCallBack {
                    override fun onRate() {

                        manager = ReviewManagerFactory.create(this@HomeActivity)
                        val request = manager?.requestReviewFlow()
                        request?.addOnCompleteListener { task: Task<ReviewInfo> ->
                            if (task.isSuccessful) {
                                reviewInfo = task.result
                                val flow =
                                    manager?.launchReviewFlow(
                                        this@HomeActivity,
                                        reviewInfo ?: task.result
                                    )
                                flow?.addOnSuccessListener { result: Void? ->
                                    currentBack += 1
                                    SpinWheelPreferences.countClickBack = currentBack
                                    SpinWheelPreferences.isUsingApp = false
                                    SpinWheelPreferences.isRate = true
                                    Toast.makeText(
                                        this@HomeActivity,
                                        getAppString(
                                            R.string.rated_content,
                                            this@HomeActivity,
                                            RATE_TYPE.RATE_5.value.toString()
                                        ),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    rateDialog.dismiss()
                                    finishAffinity()
                                }
                            } else {
                                currentBack += 1
                                SpinWheelPreferences.countClickBack = currentBack
                                SpinWheelPreferences.isUsingApp = false
                                finishAffinity()
                            }
                        }
                    }

                    override fun onDismiss() {
                        currentBack += 1
                        SpinWheelPreferences.countClickBack = currentBack
                        SpinWheelPreferences.isUsingApp = false
                        finishAffinity()
                    }
                }
            } else {
                val dlg = ExitAppDlg()
                viewModel.deleteNative()
                dlg.listener = object : ExitAppDlg.IExitAppCallBack {
                    override fun onCancel() {
                        viewModel.getHomeList()
                    }

                    override fun onExit() {
                        currentBack += 1
                        SpinWheelPreferences.countClickBack = currentBack
                        finishAffinity()
                    }
                }
                dlg.show(supportFragmentManager, "ExitApp")
            }
        }
    }

    private fun initAds() {
        viewModel.getHomeList()
    }
}