package com.amvfunny.dev.wheelist.presentaition

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

object SpinWheelPreferences {
    private lateinit var preferences: SharedPreferences

    private const val MODE = Context.MODE_PRIVATE

    private val gson = Gson()

    private const val TURN_ON_BACKGROUND_MUSIC_SETTING = "TURN_ON_BACKGROUND_MUSIC_SETTING"
    private const val TURN_ON_RESULT_SOUND_SETTING = "TURN_ON_RESULT_SOUND_SETTING"
    private const val LANGUAGE_SETTING = "LANGUAGE_SETTING"
    private const val USING_APP_KEY = "USING_APP_KEY"
    private const val HAS_INTRO_KEY = "HAS_INTRO_KEY"
    private const val IS_FIRST_USE_APP_KEY = "IS_FIRST_USE_APP_KEY"
    private const val INIT_WHEEL_DEFAULT_KEY = "INIT_WHEEL_DEFAULT_KEY"
    private const val RATE_APP_KEY = "RATE_APP_KEY"
    private const val COUNT_CLICK_ICON_HOME_RESULT_KEY = "COUNT_CLICK_ICON_HOME_RESULT_KEY"
    private const val COUNT_CLICK_BACK_APP_HOME_KEY = "COUNT_CLICK_BACK_APP_HOME_KEY"
    private const val UPDATE_TEXT_KEY = "UPDATE_TEXT_KEY"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(context.packageName, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    private inline fun SharedPreferences.commit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.commit()
    }

    var isTurnOnBackGroundMusic: Boolean?
        get() = preferences.getBoolean(TURN_ON_BACKGROUND_MUSIC_SETTING, true)
        set(value) = preferences.edit {
            value?.let { isTurnOn -> it.putBoolean(TURN_ON_BACKGROUND_MUSIC_SETTING, isTurnOn) }
        }

    var isTurnOnResultSound: Boolean?
        get() = preferences.getBoolean(TURN_ON_RESULT_SOUND_SETTING, true)
        set(value) = preferences.edit {
            value?.let { isTurnOn -> it.putBoolean(TURN_ON_RESULT_SOUND_SETTING, isTurnOn) }
        }

    var valueCodeLanguage: String?
        get() = preferences.getString(LANGUAGE_SETTING, "en")
        set(value) = preferences.edit {
            value?.let { code -> it.putString(LANGUAGE_SETTING, code) }
        }

    var isUsingApp: Boolean?
        get() = preferences.getBoolean(USING_APP_KEY, false)
        set(value) = preferences.edit {
            value?.let { code -> it.putBoolean(USING_APP_KEY, code) }
        }

    var isHasIntroApp: Boolean?
        get() = preferences.getBoolean(HAS_INTRO_KEY, false)
        set(value) = preferences.edit {
            value?.let { code -> it.putBoolean(HAS_INTRO_KEY, code) }
        }

    var isFirstUseApp: Boolean?
        get() = preferences.getBoolean(IS_FIRST_USE_APP_KEY, true)
        set(value) = preferences.edit {
            value?.let { code -> it.putBoolean(IS_FIRST_USE_APP_KEY, code) }
        }

    var isInitDefault: Boolean?
        get() = preferences.getBoolean(INIT_WHEEL_DEFAULT_KEY, false)
        set(value) = preferences.edit {
            value?.let { code -> it.putBoolean(INIT_WHEEL_DEFAULT_KEY, code) }
        }

    var isRate: Boolean?
        get() = preferences.getBoolean(RATE_APP_KEY, false)
        set(value) = preferences.edit {
            value?.let { code -> it.putBoolean(RATE_APP_KEY, code) }
        }

    var countClickHome : Int?
        get() = preferences.getInt(COUNT_CLICK_ICON_HOME_RESULT_KEY, 1)
        set(value)= preferences.edit {
            value?.let { code -> it.putInt(COUNT_CLICK_ICON_HOME_RESULT_KEY, code) }
        }

    var countClickBack : Int?
        get() = preferences.getInt(COUNT_CLICK_BACK_APP_HOME_KEY, 1)
        set(value)= preferences.edit {
            value?.let { code -> it.putInt(COUNT_CLICK_BACK_APP_HOME_KEY, code) }
        }

    var updateText : Boolean?
        get() = preferences.getBoolean(UPDATE_TEXT_KEY, false)
        set(value)= preferences.edit {
            value?.let { code -> it.putBoolean(UPDATE_TEXT_KEY, code) }
        }
}
