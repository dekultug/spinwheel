package com.amvfunny.dev.wheelist.presentaition.repodisplay.color

import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditdlg.ColorDisplay
import javax.inject.Inject

class ColorDisplayImpl @Inject constructor() : IColorRepo {
    override fun getListColor(color: Int?): List<ColorDisplay> {
        val listColor = mutableListOf<ColorDisplay>()
        listColor.add(
            ColorDisplay(
                color = getAppColor(R.color.option_color_1),
                isSelect = color == getAppColor(R.color.option_color_1)
            )
        )
        listColor.add(
            ColorDisplay(
                color = getAppColor(R.color.option_color_2),
                isSelect = color == getAppColor(R.color.option_color_2)
            )
        )
        listColor.add(
            ColorDisplay(
                color = getAppColor(R.color.option_color_3),
                isSelect = color == getAppColor(R.color.option_color_3)
            )
        )
        listColor.add(
            ColorDisplay(
                color = getAppColor(R.color.option_color_4),
                isSelect = color == getAppColor(R.color.option_color_4)
            )
        )
        listColor.add(
            ColorDisplay(
                color = getAppColor(R.color.option_color_5),
                isSelect = color == getAppColor(R.color.option_color_5)
            )
        )
        listColor.add(
            ColorDisplay(
                color = getAppColor(R.color.option_color_6),
                isSelect = color == getAppColor(R.color.option_color_6)
            )
        )
        listColor.add(
            ColorDisplay(
                color = getAppColor(R.color.option_color_7),
                isSelect = color == getAppColor(R.color.option_color_7)
            )
        )
        listColor.add(
            ColorDisplay(
                color = getAppColor(R.color.option_color_8),
                isSelect = color == getAppColor(R.color.option_color_8)
            )
        )
        return listColor
    }
}