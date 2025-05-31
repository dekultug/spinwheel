package com.amvfunny.dev.wheelist.presentaition

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

private var application: Application? = null

fun getApplication(): Application = application ?: throw Exception()

@HiltAndroidApp
class SpinWheelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
        FirebaseApp.initializeApp(this)
        SpinWheelPreferences.init(this)
        SpinWheelPreferences.isUsingApp = false
        SpinWheelPreferences.isHasIntroApp = false
    }
}