package com.amvfunny.dev.wheelist.presentaition.roulette

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amvfunny.dev.wheelist.base.common.state.StateData
import com.amvfunny.dev.wheelist.base.common.util.AdsConfig
import com.amvfunny.dev.wheelist.data.model.WHEEL_TYPE
import com.amvfunny.dev.wheelist.data.model.Wheel
import com.amvfunny.dev.wheelist.data.repo.wheel.IWheelRepo
import com.amvfunny.dev.wheelist.presentaition.roulette.RouletteActivity.Companion.SEE_LIST_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouletteViewModel @Inject constructor(
    private val repo: IWheelRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val isOnlySeeList = savedStateHandle.get<Boolean>(SEE_LIST_KEY) ?: false

    private var _rouletteListState = MutableStateFlow<StateData<List<Any>>>(StateData.Init())
    val rouletteListState = _rouletteListState.asStateFlow()

    private var jobList: Job? = null

    var indexScroll = -1

    init {
        setWheelDefault()
    }

    private fun setWheelDefault() {
        val job = viewModelScope.launch(Dispatchers.IO) {
            repo.initWheel()
        }
        job.invokeOnCompletion {
            getWheelList()
        }
    }

    private fun getWheelList() {
        jobList?.cancel()
        jobList = viewModelScope.launch(Dispatchers.IO) {
            val data = repo.getAllWheel()
            val list: MutableList<Any> = arrayListOf()
            list.addAll(data)
            _rouletteListState.value = StateData.Success(data = list)
        }
    }

    fun addOrEditWheel(wheel: Wheel) {
        viewModelScope.launch(Dispatchers.IO) {
            var list = _rouletteListState.value.data?.toMutableList() ?: arrayListOf()
            if (list.isEmpty()) {
                getWheelList()
            }
            jobList?.invokeOnCompletion {
                list = _rouletteListState.value.data?.toMutableList() ?: arrayListOf()
                val index = list.indexOfFirst {
                    it is Wheel && it.id == wheel.id
                }
                if (index >= 0) {
                    val item = (list[index] as Wheel).copy()
                    item.wheelType = WHEEL_TYPE.CUSTOM.value
                    item.title = wheel.title
                    item.countRepeat = wheel.countRepeat
                    list[index] = item
                    indexScroll = index
                } else {
                    list.add(wheel)
                    indexScroll = list.lastIndex
                }
                _rouletteListState.value = StateData.Success(data = list)
            }
        }
    }

    fun duplicateWheel(wheel: Wheel?) {
        viewModelScope.launch(Dispatchers.IO) {
            var list = _rouletteListState.value.data?.toMutableList() ?: arrayListOf()
            if (list.isEmpty()) {
                getWheelList()
            }
            jobList?.invokeOnCompletion {
                list = _rouletteListState.value.data?.toMutableList() ?: arrayListOf()
                val listDBWheel = repo.getAllWheel().toList()
                listDBWheel.sortedWith(Comparator.comparingInt { it.id ?: 1 })
                val id = (listDBWheel.last().id ?: 0) + 1
                wheel?.let {
                    val item = wheel.copy(id = id)
                    list.add(item)
                    indexScroll = list.size + 1
                    repo.duplicateWheel(wheel)
                    _rouletteListState.value = StateData.Success(data = list)
                }
            }
        }
    }

    fun removeWheel(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            var list = _rouletteListState.value.data?.toMutableList() ?: arrayListOf()
            if (list.isEmpty()) {
                getWheelList()
            }
            jobList?.invokeOnCompletion {
                list = _rouletteListState.value.data?.toMutableList() ?: arrayListOf()
                val index = list.indexOfFirst {
                    it is Wheel && it.id == id
                }
                if (index >= 0) {
                    list.removeAt(index)
                    repo.deleteWheel(id)
                    _rouletteListState.value = StateData.Success(data = list)
                }
            }
        }
    }

    fun showAds(){
        viewModelScope.launch(Dispatchers.IO) {
            var list = _rouletteListState.value.data?.toMutableList() ?: arrayListOf()
            if (list.isEmpty()) {
                getWheelList()
            }
            jobList?.invokeOnCompletion {
                list = _rouletteListState.value.data?.toMutableList() ?: arrayListOf()
                val index = list.indexOfFirst {
                    it is NativeDisplay
                }
                if (index >= 0) {
                    val item = (list[index] as NativeDisplay).copy()
                    item.isShow = true
                    list[index] = item
                    _rouletteListState.value = StateData.Success(data = list)
                }
            }
        }
    }

    fun hideAds(){
        viewModelScope.launch(Dispatchers.IO) {
            var list = _rouletteListState.value.data?.toMutableList() ?: arrayListOf()
            if (list.isEmpty()) {
                getWheelList()
            }
            jobList?.invokeOnCompletion {
                list = _rouletteListState.value.data?.toMutableList() ?: arrayListOf()
                val index = list.indexOfFirst {
                    it is NativeDisplay
                }
                if (index >= 0) {
                    val item = (list[index] as NativeDisplay).copy()
                    item.isShow = false
                    list[index] = item
                    _rouletteListState.value = StateData.Success(data = list)
                }
            }
        }
    }
}