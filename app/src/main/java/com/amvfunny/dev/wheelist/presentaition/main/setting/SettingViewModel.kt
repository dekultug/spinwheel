package com.amvfunny.dev.wheelist.presentaition.main.setting

import androidx.lifecycle.ViewModel
import com.amvfunny.dev.wheelist.base.common.state.StateData
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.languague.LANGUAGE_TYPE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.Thread.State

class SettingViewModel : ViewModel() {

    private var _stateBackGroundMusic = MutableStateFlow<StateData<Boolean>>(StateData.Init())
    val stateBackGroundMusic = _stateBackGroundMusic.asStateFlow()

    private var _stateResultSound = MutableStateFlow<StateData<Boolean>>(StateData.Init())
    val stateResultSound = _stateResultSound.asStateFlow()

    private var _stateLanguage = MutableStateFlow<StateData<LANGUAGE_TYPE>>(StateData.Init())
    val stateLanguage = _stateLanguage.asStateFlow()

    init {
        _stateBackGroundMusic.value =
            StateData.Success(data = SpinWheelPreferences.isTurnOnBackGroundMusic)
        _stateResultSound.value = StateData.Success(data = SpinWheelPreferences.isTurnOnResultSound)
        _stateLanguage.value =
            StateData.Success(data = LANGUAGE_TYPE.getType(SpinWheelPreferences.valueCodeLanguage))
    }

    fun setStateBackgroundMusic(isTurnOn: Boolean) {
        SpinWheelPreferences.isTurnOnBackGroundMusic = isTurnOn
        _stateBackGroundMusic.value = StateData.Success(data = isTurnOn)
    }

    fun setStateResultSound(isTurnOn: Boolean) {
        SpinWheelPreferences.isTurnOnResultSound = isTurnOn
        _stateResultSound.value = StateData.Success(data = isTurnOn)
    }

    fun setStateLanguage(type: LANGUAGE_TYPE) {
        _stateLanguage.value = StateData.Success(data = type)
        SpinWheelPreferences.valueCodeLanguage = type.value
    }
}