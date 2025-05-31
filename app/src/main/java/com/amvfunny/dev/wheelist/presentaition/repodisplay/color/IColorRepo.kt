package com.amvfunny.dev.wheelist.presentaition.repodisplay.color

import com.amvfunny.dev.wheelist.presentaition.roulette.addeditdlg.ColorDisplay

interface IColorRepo {
    fun getListColor(color: Int?): List<ColorDisplay>
}