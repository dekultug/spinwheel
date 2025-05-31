package com.amvfunny.dev.wheelist.base.common.screen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.amvfunny.dev.wheelist.presentaition.MainActivity
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.eventbus.IEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.IEventHandler
import com.amvfunny.dev.wheelist.base.common.extention.TIME_DELAY_CHANGE_STATUS_BAR
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.loader.aim.IScreenAnim
import com.amvfunny.dev.wheelist.base.common.loader.aim.SlideAnimation
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class SpinWheelFragment : Fragment(), IEventHandler {

    protected val TAG = "SpinWheelFragment"

    protected val mainActivity by lazy {
        (requireActivity() as? MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity?.setStatusColor(getAppColor(R.color.orange_primary_light))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        observerData()
        setUpAdapter()
        requireActivity().onBackPressedDispatcher.addCallback(
            this.viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backFragment()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeListener()
    }

    fun setStatusColor(color: Int) {
        mainActivity?.setStatusColor(color)
    }

    open fun observerData() {}
    open fun setUpView() {}

    open fun setUpAdapter() {}
    open fun removeListener() {}

    fun addFragment(
        fragment: Fragment,
        bundle: Bundle? = null,
        keepToBackStack: Boolean = true,
        isAdd: Boolean = false
    ) {

    }

    open fun backFragment() {
        setStatusColor(getAppColor(R.color.orange_primary_light))
        Log.d(TAG, "backFragment: ${mainActivity?.supportFragmentManager?.backStackEntryCount}")
        try {
            if ((mainActivity?.supportFragmentManager?.backStackEntryCount ?: 1) <= 1) {
                SpinWheelPreferences.isUsingApp = false
                SpinWheelPreferences.isHasIntroApp = false
                mainActivity?.finishAffinity()
            } else {
                mainActivity?.supportFragmentManager?.popBackStack()
            }
        } catch (e: Exception) {
            SpinWheelPreferences.isUsingApp = false
            SpinWheelPreferences.isHasIntroApp = false
            mainActivity?.finishAffinity()
        }
    }

    fun backFragment(tag: String) {
        mainActivity?.supportFragmentManager?.popBackStack(
            tag, 0
        )
    }

    fun changeStatusBarDelayDarkTransparent(){
        Handler(Looper.getMainLooper()).postDelayed({setStatusColor(getAppColor(R.color.dark_transparent))},
            TIME_DELAY_CHANGE_STATUS_BAR
        )
    }

    fun changeStatusBarDelayPrimary(){
        Handler(Looper.getMainLooper()).postDelayed({setStatusColor(getAppColor(R.color.orange_primary_light))},
            TIME_DELAY_CHANGE_STATUS_BAR
        )
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    override fun onEvent(event: IEvent) {
    }
}