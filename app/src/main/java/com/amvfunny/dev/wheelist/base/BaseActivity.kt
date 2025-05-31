package com.amvfunny.dev.wheelist.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.amvfunny.dev.wheelist.base.common.util.SystemUtil

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    protected lateinit var binding: T

    var currentTheme = AppCompatDelegate.MODE_NIGHT_NO
    var isReloadAds = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemUtil.setLocale(this)
        isReloadAds = false
        setContentView(getInflatedLayout(layoutInflater))

    }

    override fun onStart() {
        super.onStart()
        SystemUtil.setLocale(this)
    }

    abstract fun setBinding(layoutInflater: LayoutInflater): T

    private fun getInflatedLayout(inflater: LayoutInflater): View {
        binding = setBinding(inflater)
        return binding.root
    }

    fun reloadActivity(context: Context) {
        (context as Activity).finish()
        ContextCompat.startActivity(context, context.intent, null)
    }

    override fun onStop() {
        super.onStop()
        isReloadAds = true
    }
}