package com.amvfunny.dev.wheelist.presentaition.roulette.addeditdlg

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.state.StateData
import com.amvfunny.dev.wheelist.data.model.OptionWheel
import com.amvfunny.dev.wheelist.data.model.Wheel
import com.amvfunny.dev.wheelist.data.repo.optionwheel.IOptionWheelRepo
import com.amvfunny.dev.wheelist.data.repo.wheel.IWheelRepo
import com.amvfunny.dev.wheelist.presentaition.repodisplay.color.IColorRepo
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditdlg.AddEditRouletteDlg.Companion.OPTION_WHEEL_DATA_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditdlg.AddEditRouletteDlg.Companion.SIZE_OPTION_TEMP_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditdlg.AddEditRouletteDlg.Companion.WHEEL_ID_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repo: IColorRepo,
    private val savedStateHandle: SavedStateHandle,
    private val repoWheel: IWheelRepo,
    private val repoOptionWheel: IOptionWheelRepo
) : ViewModel() {

    private var _optionWheel = savedStateHandle.get<OptionWheel>(OPTION_WHEEL_DATA_KEY)
    val optionWheel = _optionWheel

    private var _countSize = savedStateHandle.get<Int>(SIZE_OPTION_TEMP_KEY)

    private var _wheelId = savedStateHandle.get<Int>(WHEEL_ID_KEY)

    private var _listColor = MutableStateFlow<StateData<List<ColorDisplay>>>(StateData.Init())
    val listColor = _listColor.asStateFlow()

    private var _addOrEditState = MutableStateFlow<StateData<OptionWheel>>(StateData.Init())
    val addOrEditState = _addOrEditState.asStateFlow()

    private var _colorStateOriginal = MutableStateFlow<StateData<Boolean>>(StateData.Init())
    val colorStateOriginal = _colorStateOriginal.asStateFlow()

    private var jobListColor: Job? = null

    var colorCustom: Int? = null

    init {
        getListColor()
    }

    private fun getListColor() {
        jobListColor?.cancel()
        jobListColor = viewModelScope.launch(Dispatchers.IO) {
            val data = repo.getListColor(_optionWheel?.color)
            _listColor.value = StateData.Success(data = data)
        }
    }

    fun setSelectColor(color: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = _listColor.value.data?.toMutableList() ?: arrayListOf()
            val oldIndex = list.indexOfFirst {
                it.isSelect
            }
            if (oldIndex >= 0) {
                val item = list[oldIndex].copy()
                item.isSelect = false
                list[oldIndex] = item
            }
            val newIndex = list.indexOfFirst {
                it.color == color
            }
            if (newIndex >= 0) {
                val item = list[newIndex].copy()
                item.isSelect = true
                list[newIndex] = item
            }
            _listColor.value = StateData.Success(data = list)
        }
    }

    fun checkColorInList(color: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            jobListColor?.invokeOnCompletion {
                val listData = _listColor.value.data?.toMutableList()
                _colorStateOriginal.value =
                    StateData.Success(data = listData?.find { it.color == color } != null)
            }
        }
    }

    fun resetColorInList() {
        _colorStateOriginal.value = StateData.Init()
    }

    fun addEditOptionWheel(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val color = if (colorCustom == null) {
                _listColor.value.data?.find {
                    it.isSelect
                }?.color ?: getAppColor(R.color.option_color_1)
            } else {
                colorCustom
            }
            if (_optionWheel != null) {
                _optionWheel = _optionWheel!!.copy(
                    content = name,
                    color = color
                )
                _addOrEditState.value = StateData.Success(data = _optionWheel)
            } else {
                val tempIdWheel = if (repoWheel.getAllWheel().isNotEmpty()) {
                    repoWheel.getAllWheel().last().id + 1
                } else {
                    repoWheel.getAllWheel().size + 1
                }

                val maxId = if (repoOptionWheel.getAllOptionWithoutWheel().isNotEmpty()) {
                    repoOptionWheel.getAllOptionWithoutWheel().last().id
                } else {
                    repoOptionWheel.getAllOptionWithoutWheel().size
                }

                val optionWheel = OptionWheel(
                    color = color,
                    content = name,
                    wheelId = _wheelId ?: tempIdWheel,
                    id = (_countSize ?: 1) + maxId
                )
                _addOrEditState.value = StateData.Success(data = optionWheel)
            }
        }
    }

    fun resetStateAddEdit() {
        _addOrEditState.value = StateData.Init()
    }
}