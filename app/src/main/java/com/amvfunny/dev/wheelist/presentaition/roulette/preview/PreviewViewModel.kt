package com.amvfunny.dev.wheelist.presentaition.roulette.preview

import android.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amvfunny.dev.wheelist.base.common.state.StateData
import com.amvfunny.dev.wheelist.data.model.OptionWheel
import com.amvfunny.dev.wheelist.data.model.WHEEL_TYPE
import com.amvfunny.dev.wheelist.data.model.Wheel
import com.amvfunny.dev.wheelist.data.repo.optionwheel.IOptionWheelRepo
import com.amvfunny.dev.wheelist.data.repo.wheel.IWheelRepo
import com.amvfunny.dev.wheelist.presentaition.roulette.preview.PreviewRouletteActivity.Companion.DATA_SPIN_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.preview.PreviewRouletteActivity.Companion.REPEAT_COUNT_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.preview.PreviewRouletteActivity.Companion.TITLE_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.preview.PreviewRouletteActivity.Companion.TYPE_WHEEL_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.preview.PreviewRouletteActivity.Companion.WHEEL_PREVIEW_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rubikstudio.library.model.LuckyItem
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor (
    private val savedStateHandle: SavedStateHandle,
    private val repoWheel: IWheelRepo,
    private var repoOptionWheel: IOptionWheelRepo
) : ViewModel() {

    private var _listOptionWheel = savedStateHandle.get<List<OptionWheel>>(DATA_SPIN_KEY)

    private var _repeatCount = savedStateHandle.get<Int>(REPEAT_COUNT_KEY)?:1

     var wheelPreview = savedStateHandle.get<Wheel>(WHEEL_PREVIEW_KEY)

    val title = savedStateHandle.get<String>(TITLE_KEY)

    val type = WHEEL_TYPE.getType(savedStateHandle.get<Int>(TYPE_WHEEL_KEY))

    private val _listLuckyState = MutableStateFlow<StateData<List<LuckyItem>>>(StateData.Init())
    val listLuckyState = _listLuckyState.asStateFlow()

    private var _startState = MutableStateFlow<StateData<Boolean>>(StateData.Init())
    val startState = _startState.asStateFlow()

    private var _saveAndSpinState = MutableStateFlow<StateData<Boolean>>(StateData.Init())
    val saveAndSpinState = _saveAndSpinState.asStateFlow()

    var isSaveAndSpin = false

    init {
        getListLucky()
    }

    private fun getListLucky() {
        viewModelScope.launch(Dispatchers.IO) {
            val list: MutableList<LuckyItem> = arrayListOf()
            repeat(_repeatCount) {
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
        isSaveAndSpin = true
        _startState.value = StateData.Success(data = isStart)
    }

    fun addWheel() {
        viewModelScope.launch(Dispatchers.IO) {
            repoOptionWheel.deleteAllOptionWheelWhenWheel(wheelPreview?.id)
            _listOptionWheel?.forEach {
                repoOptionWheel.insertOptionWheel(it)
            }
            wheelPreview = if (wheelPreview == null) {
                val tempWheelId = repoWheel.getAllWheel().size + 1
                val id = if (_listOptionWheel?.isNotEmpty() == true) {
                    _listOptionWheel!!.first().wheelId ?: tempWheelId
                } else {
                    tempWheelId
                }
                Wheel(
                    id = id,
                    title = title,
                    countRepeat = _repeatCount,
                    wheelType = WHEEL_TYPE.CUSTOM.value
                )
            } else {
                wheelPreview!!.copy(title = title, countRepeat = _repeatCount, wheelType = WHEEL_TYPE.CUSTOM.value)
            }
            repoWheel.insertWheel(wheelPreview!!)
            _saveAndSpinState.value = StateData.Success(data = true)
        }
    }
}