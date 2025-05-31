package com.amvfunny.dev.wheelist.presentaition.roulette.addeditroulette

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amvfunny.dev.wheelist.base.common.state.StateData
import com.amvfunny.dev.wheelist.data.model.OptionWheel
import com.amvfunny.dev.wheelist.data.model.WHEEL_TYPE
import com.amvfunny.dev.wheelist.data.model.Wheel
import com.amvfunny.dev.wheelist.data.repo.optionwheel.IOptionWheelRepo
import com.amvfunny.dev.wheelist.data.repo.wheel.IWheelRepo
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditroulette.AddEditRouletteActivity.Companion.IS_EDIT_FROM_SPIN_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditroulette.AddEditRouletteActivity.Companion.WHEEL_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class AddEditRouletteViewModel @Inject constructor(
    private val repo: IOptionWheelRepo,
    private val savedStateHandle: SavedStateHandle,
    private val repoWheel: IWheelRepo
) : ViewModel() {

    private var _wheel = savedStateHandle.get<Wheel>(WHEEL_KEY)
    val wheel: Wheel?
        get() = _wheel

    private var _optionWheelListState = MutableStateFlow<StateData<List<Any>>>(StateData.Init())
    val optionWheelListState = _optionWheelListState.asStateFlow()

    val editSpinFromSpinRoulette = savedStateHandle.get<Boolean>(IS_EDIT_FROM_SPIN_KEY)?:false

    private var _saveAndSpinState = MutableStateFlow<StateData<Boolean>>(StateData.Init())
    val saveAndSpinState = _saveAndSpinState.asStateFlow()

    val listOptionWheelTemp: MutableList<OptionWheel> = arrayListOf()

    var indexScroll = -1

    init {
        getListOptionWheel(_wheel?.id)
    }

    private fun getListOptionWheel(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            val list: MutableList<Any> = arrayListOf()
            repo.getAllOptionWheel(id).let {
                if (it.isNotEmpty()) {
                    list.addAll(it)
                    listOptionWheelTemp.addAll(it)
                }
            }
            list.add(AddRouletteAdapter.DATA_ADD_OPTION)
            _optionWheelListState.value = StateData.Success(list)
        }
    }

    fun deleteOptionWheel(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // temp
            val indexTemp = listOptionWheelTemp.indexOfFirst {
                it.id == id
            }
            if (indexTemp >= 0) {
                listOptionWheelTemp.removeAt(indexTemp)
            }

            // update ui
            val list = _optionWheelListState.value.data?.toMutableList() ?: arrayListOf()
            val index = list.indexOfFirst {
                it is OptionWheel && it.id == id
            }

            if (index >= 0) {
                list.removeAt(index)
            }
            _optionWheelListState.value = StateData.Success(data = list)
        }
    }

    fun addOrUpdateOptionWheel(optionWheel: OptionWheel) {
        viewModelScope.launch(Dispatchers.IO) {
            // list temp
            val indexTemp = listOptionWheelTemp.indexOfFirst {
                it.id == optionWheel.id
            }
            if (indexTemp >= 0) {
                listOptionWheelTemp[indexTemp] = optionWheel
            } else {
                listOptionWheelTemp.add(optionWheel)
            }

            // update ui
            val list = _optionWheelListState.value.data?.toMutableList() ?: arrayListOf()
            val index = list.indexOfFirst {
                it is OptionWheel && it.id == optionWheel.id
            }
            if (index >= 0) {
                val item = (list[index] as OptionWheel).copy()
                item.content = optionWheel.content
                item.color = optionWheel.color
                list[index] = item
                indexScroll = index
            } else {
                list.add(list.size - 1, optionWheel)
                indexScroll = list.lastIndex
            }
            _optionWheelListState.value = StateData.Success(data = list)
        }
    }

    fun resetStateSaveOrEdit() {
        _saveAndSpinState.value = StateData.Init()
    }

    fun getCountSize(): Int {
        return listOptionWheelTemp.size + 1
    }

    fun addWheel(title: String, repeatCount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAllOptionWheelWhenWheel(_wheel?.id)
            listOptionWheelTemp.forEach {
                repo.insertOptionWheel(it)
            }
            _wheel = if (_wheel == null) {
                val tempWheelId = repoWheel.getAllWheel().size + 1
                val id = if (listOptionWheelTemp.isNotEmpty()) {
                    listOptionWheelTemp.first().wheelId ?: tempWheelId
                } else {
                    tempWheelId
                }
                Wheel(
                    id = id,
                    title = title,
                    countRepeat = repeatCount,
                    wheelType = WHEEL_TYPE.CUSTOM.value
                )
            } else {
                _wheel!!.copy(title = title, countRepeat = repeatCount, wheelType = WHEEL_TYPE.CUSTOM.value)
            }
            repoWheel.insertWheel(_wheel!!)
            _saveAndSpinState.value = StateData.Success(data = true)
        }
    }
}