package com.amvfunny.dev.wheelist.base.common.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.BaseActivity
import com.amvfunny.dev.wheelist.base.common.eventbus.IEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.IEventHandler
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Objects


abstract class SpinWheelActivity<T : ViewBinding> : BaseActivity<T>(), IEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setStatusColor(getAppColor(R.color.orange_primary_light))
        setUpView()
        setUpAdapter()
        observerData()
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    override fun onEvent(event: IEvent) {
    }

//    abstract fun getContainerView(): View?

    open fun setUpView() {
        setFullScreen()
    }

    open fun observerData() {}
    open fun setUpAdapter() {}
    open fun removeListener() {}

    private fun setFullScreen() {
        val windowInsetsController: WindowInsetsControllerCompat? =
            if (Build.VERSION.SDK_INT >= 30) {
                ViewCompat.getWindowInsetsController(window.decorView)
            } else WindowInsetsControllerCompat(window, binding.root)

        if (windowInsetsController == null) {
            return
        }
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        window.decorView.setOnSystemUiVisibilityChangeListener { i: Int ->
            if (i == 0) {
                Handler().postDelayed({
                    val windowInsetsController1: WindowInsetsControllerCompat? =
                        if (Build.VERSION.SDK_INT >= 30) {
                            ViewCompat.getWindowInsetsController(window.decorView)
                        } else {
                            WindowInsetsControllerCompat(window, binding.root)
                        }
                    Objects.requireNonNull(windowInsetsController1)
                        ?.hide(WindowInsetsCompat.Type.navigationBars())
                }, 3000)
            }
        }

        // hide bottom nav system
        val decorView = window.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions

        // caculator padding top status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @SuppressLint("ResourceAsColor")
    fun setStatusColor(color: Int = R.color.orange_primary_bold, isDarkText: Boolean = false) {
        window?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                decorView.let {
                    ViewCompat.getWindowInsetsController(it)?.apply {
                        // Light text == dark status bar
                        // máy ảo bị bug có lúc hiển thị sai cái này, trên device thật vẫn sẽ show bt
                        isAppearanceLightStatusBars = isDarkText
                    }
                }
            } else {
                //set text status old api
                decorView.let {
                    it.systemUiVisibility =
                        if (!isDarkText) {
                            it.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                        } else {
                            it.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        }
                }
            }

            //set status color
            statusBarColor = color
        }
    }

    fun navigateTo(
        clazz: Class<out BaseActivity<*>>,
        bundle: Bundle = bundleOf(),
        isClearTop: Boolean = false,
        isBack: Boolean = false
    ) {
        val intent = Intent(this, clazz)
        intent.putExtras(bundle)
        if (isClearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(intent)
        if (isBack) {
            animBackScreen()
        } else {
            animOpenScreen()
        }
    }

    fun animOpenScreen() {
        overridePendingTransition(R.anim.slide_enter_left_to_right, R.anim.slide_exit_right_to_left)
    }

    fun animBackScreen() {
        overridePendingTransition(R.anim.slide_pop_enter_right_to_left, R.anim.slide_pop_exit_left_to_right)
    }
}