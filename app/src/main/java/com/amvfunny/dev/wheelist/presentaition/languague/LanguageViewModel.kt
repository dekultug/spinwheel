package com.amvfunny.dev.wheelist.presentaition.languague

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amvfunny.dev.wheelist.base.common.state.StateData
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.languague.LanguageActivity.Companion.START_APP_KEY
import com.amvfunny.dev.wheelist.presentaition.repodisplay.language.ILanguageRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val repo: ILanguageRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var _languageState =
        MutableStateFlow<StateData<List<LanguageDisplay>>>(StateData.Init())
    val languageState = _languageState.asStateFlow()

    val isStartApp = savedStateHandle.get<Boolean>(START_APP_KEY)

    val currentLanguage = LANGUAGE_TYPE.getType(SpinWheelPreferences.valueCodeLanguage)

    var type: LANGUAGE_TYPE = LANGUAGE_TYPE.ENGLISH

    init {
        getListLanguage()
    }

    private fun getListLanguage() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repo.getListLanguage()
            _languageState.value = StateData.Success(data = data)
        }
    }

    fun selectLanguage(type: LANGUAGE_TYPE) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = _languageState.value.data?.toMutableList() ?: arrayListOf()
            val oldIndex = list.indexOfFirst {
                it.isSelect
            }
            if (oldIndex >= 0) {
                val item = (list[oldIndex]).copy()
                item.isSelect = false
                list[oldIndex] = item
            }
            val index = list.indexOfFirst {
                it.type == type
            }
            if (index >= 0) {
                val item = (list[index]).copy()
                item.isSelect = true
                list[index] = item
                this@LanguageViewModel.type = type
                if (currentLanguage != type){
                    SpinWheelPreferences.updateText = true
                }
//                SpinWheelPreferences.valueCodeLanguage = type.value
            }
            _languageState.value = StateData.Success(data = list)
        }
    }
}