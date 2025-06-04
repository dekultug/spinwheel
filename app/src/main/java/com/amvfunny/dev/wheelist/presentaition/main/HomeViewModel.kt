package com.amvfunny.dev.wheelist.presentaition.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amvfunny.dev.wheelist.base.common.state.StateData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private var _homeListState = MutableStateFlow<StateData<List<Any>>>(StateData.Init())
    val homeListState = _homeListState.asStateFlow()

    init {
//        getHomeList()
    }

     fun getHomeList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list: MutableList<Any> = arrayListOf()
            list.add(HOME_TYPE.ROULETTE)
            list.add(HOME_TYPE.CHOOSER)
            list.add(HOME_TYPE.RANKING)
            list.add(HOME_TYPE.HOMOGRAFT)
            _homeListState.value = StateData.Success(list)
        }
    }

    fun deleteNative(){

    }
}