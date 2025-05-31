package com.amvfunny.dev.wheelist.presentaition.main.spinplay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amvfunny.dev.wheelist.base.common.state.StateData
import com.amvfunny.dev.wheelist.presentaition.main.spinplay.SpinPlayActivity.Companion.TYPE_DATA
import com.amvfunny.dev.wheelist.presentaition.repodisplay.spinplay.ISpinPlayRepo
import com.amvfunny.dev.wheelist.presentaition.widget.spinview.SPIN_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpinPlayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repoSizeSpin: ISpinPlayRepo
): ViewModel() {

    private var valueType = savedStateHandle.get<Int>(TYPE_DATA)
    val spinType = SPIN_TYPE.getType(valueType)

    private var _spinListState = MutableStateFlow<StateData<List<SpinPlayDisplay>>>(StateData.Init())
    val spinListState = _spinListState.asStateFlow()

    init {
        getListSpin()
    }

    private fun getListSpin(){
        viewModelScope.launch(Dispatchers.IO) {
            _spinListState.value = StateData.Success(data = repoSizeSpin.getListSpinPlay(spinType))
        }
    }

    fun setState(size:Int?){
        viewModelScope.launch(Dispatchers.IO) {
            val list = _spinListState.value.data?.toMutableList()?: arrayListOf()
            val oldIndex = list.indexOfFirst {
                it.isSelect
            }
            if (oldIndex >=0){
                val item = (list[oldIndex]).copy()
                item.isSelect = false
                list[oldIndex] = item
            }

            val index= list.indexOfFirst {
                it.size == size
            }
            if (index >= 0){
                val item = (list[index]).copy()
                item.isSelect = true
                list[index] = item
            }
            _spinListState.value = StateData.Success(data = list)
        }
    }
}