package com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel

import android.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amvfunny.dev.wheelist.base.common.state.StateData
import com.amvfunny.dev.wheelist.data.model.OptionWheel
import com.amvfunny.dev.wheelist.data.model.Wheel
import com.amvfunny.dev.wheelist.data.repo.optionwheel.IOptionWheelRepo
import com.amvfunny.dev.wheelist.data.repo.wheel.IWheelRepo
import com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel.SpinRouletteActivity.Companion.PREVIEW_LIST_OPTION_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel.SpinRouletteActivity.Companion.REPEAT_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel.SpinRouletteActivity.Companion.TITLE_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel.SpinRouletteActivity.Companion.WHEEL_DATA_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rubikstudio.library.model.LuckyItem
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class SpinWheelViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repoOptionWheel: IOptionWheelRepo
) : ViewModel() {

    var _wheel =
        savedStateHandle.get<Wheel>(WHEEL_DATA_KEY)

    val title = savedStateHandle.get<String>(TITLE_KEY)

    private var _repeat = savedStateHandle.get<Int>(REPEAT_KEY) ?: 1
    private var _listOptionWheel =
        savedStateHandle.get<List<OptionWheel>>(PREVIEW_LIST_OPTION_KEY)

    private val _listLuckyState = MutableStateFlow<StateData<List<LuckyItem>>>(StateData.Init())
    val listLuckyState = _listLuckyState.asStateFlow()

    private var _startState = MutableStateFlow<StateData<Boolean>>(StateData.Init())
    val startState = _startState.asStateFlow()

    init {
        getListLucky()
    }

    private fun getListLucky() {
        viewModelScope.launch(Dispatchers.IO) {
            val list: MutableList<LuckyItem> = arrayListOf()
            when {
                _wheel != null -> {
                    val listOption = repoOptionWheel.getAllOptionWheel(_wheel!!.id)
                    repeat(_repeat) {
                        listOption.forEach { optionItem ->
                            val optionNameLanguage = optionItem.content
                            val luckyItem = LuckyItem()
                            //set color
                            luckyItem.color = optionItem.color ?: Color.TRANSPARENT

                            //setName
                            optionItem.content?.let {
                                if (it.length > 8) {
                                    val sportName = optionNameLanguage?.substring(0, 7) + "..."
                                    luckyItem.secondaryText = sportName.toUpperCase()
                                } else {
                                    luckyItem.secondaryText = optionNameLanguage?.toUpperCase()
                                }
                            }
                            list.add(luckyItem)
                        }
                    }
                    _listLuckyState.value = StateData.Success(data = list)
                }

                _listOptionWheel != null -> {
                    repeat(_repeat) {
                        _listOptionWheel?.forEach { optionItem ->
                            val optionNameLanguage = optionItem.content
                            val luckyItem = LuckyItem()
                            //set color
                            luckyItem.color = optionItem.color ?: Color.TRANSPARENT

                            //setName
                            optionItem.content?.let {
                                if (it.length > 8) {
                                    val sportName = optionNameLanguage?.substring(0, 7) + "..."
                                    luckyItem.secondaryText = sportName.toUpperCase()
                                } else {
                                    luckyItem.secondaryText = optionNameLanguage?.toUpperCase()
                                }
                            }
                            list.add(luckyItem)
                        }
                    }
                    _listLuckyState.value = StateData.Success(data = list)
                }
            }
        }
    }

    fun updateWheel(wheel: Wheel) {
        _wheel = wheel
        wheel.countRepeat?.let {
            _repeat = it
        }
        getListLucky()
    }

    fun getRandomIndex(): Int {
        val rand = Random()
        val list = _listLuckyState.value.data ?: arrayListOf()
        val randomIndex = if (list.size < 2) {
            0
        } else {
            rand.nextInt(list.size)
        }

        return randomIndex
    }

    fun setStateStart(isStart: Boolean) {
        _startState.value = StateData.Success(data = isStart)
    }
}