package com.amvfunny.dev.wheelist.presentaition.repodisplay.spinplay

import com.amvfunny.dev.wheelist.presentaition.main.spinplay.SpinPlayDisplay
import com.amvfunny.dev.wheelist.presentaition.widget.spinview.SPIN_TYPE
import javax.inject.Inject

class SpinPlayRepoImpl @Inject constructor() : ISpinPlayRepo {
    override fun getListSpinPlay(type: SPIN_TYPE): List<SpinPlayDisplay> {
        val list: MutableList<SpinPlayDisplay> = arrayListOf()
        when {
            type == SPIN_TYPE.CHOOSE -> {
                list.add(SpinPlayDisplay(size = 1, isSelect = true))
                list.add(SpinPlayDisplay(size = 2))
                list.add(SpinPlayDisplay(size = 3))
                list.add(SpinPlayDisplay(size = 4))
            }

            type == SPIN_TYPE.COUPLE -> {
                list.add(SpinPlayDisplay(size = 2, isSelect = true))
                list.add(SpinPlayDisplay(size = 3))
                list.add(SpinPlayDisplay(size = 4))
                list.add(SpinPlayDisplay(size = 5))
            }
        }
        return list
    }
}