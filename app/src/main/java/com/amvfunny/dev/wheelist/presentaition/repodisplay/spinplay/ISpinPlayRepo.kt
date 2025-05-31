package com.amvfunny.dev.wheelist.presentaition.repodisplay.spinplay

import com.amvfunny.dev.wheelist.presentaition.main.spinplay.SpinPlayDisplay
import com.amvfunny.dev.wheelist.presentaition.widget.spinview.SPIN_TYPE

interface ISpinPlayRepo {
    fun getListSpinPlay(type: SPIN_TYPE): List<SpinPlayDisplay>
}