package com.amvfunny.dev.wheelist.base.common.util

import android.content.Context
import android.content.res.Configuration
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import java.util.Locale

object SystemUtil {
    private var myLocale: Locale? = null
    // Load lại ngôn ngữ đã lưu và thay đổi chúng
    fun setLocale(context: Context) {
        val language = getPreLanguage()
        if (language == "") {
            val config = Configuration()
            val locale = Locale.getDefault()
            Locale.setDefault(locale)
            config.locale = locale
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        } else {
            changeLang(language, context)
        }
    }

    // method phục vụ cho việc thay đổi ngôn ngữ.
    fun changeLang(lang: String?, context: Context) {
        if (lang.equals("", ignoreCase = true)) return
        myLocale = Locale(lang)
        Locale.setDefault(myLocale)
        val config = Configuration()
        config.locale = myLocale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getPreLanguage(): String? {
       return SpinWheelPreferences.valueCodeLanguage
    }
}