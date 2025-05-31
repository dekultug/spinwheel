package com.amvfunny.dev.wheelist.data.repo.wheel

import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.data.model.OptionWheel
import com.amvfunny.dev.wheelist.data.model.WHEEL_TYPE
import com.amvfunny.dev.wheelist.data.model.Wheel
import com.amvfunny.dev.wheelist.data.repo.optionwheel.IOptionWheelRepo
import com.amvfunny.dev.wheelist.data.source.local.IWheelDao
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import javax.inject.Inject

class WheelRepoImpl @Inject constructor(
    private val wheelDao: IWheelDao,
    private val optionWheelDao: IOptionWheelRepo
) : IWheelRepo {
    override fun initWheel() {
        val list = wheelDao.getAllWheel()
        if (list.isEmpty() && SpinWheelPreferences.isInitDefault == false) {
            wheelDao.insertWheel(
                Wheel(
                    id = WHEEL_TYPE.EAT.value,
                    title = getAppString(R.string.wheel_init_1),
                    countRepeat = 1,
                    wheelType = WHEEL_TYPE.EAT.value
                )
            )

            wheelDao.insertWheel(
                Wheel(
                    id = WHEEL_TYPE.WINNER.value,
                    title = getAppString(R.string.wheel_init_2),
                    countRepeat = 1,
                    wheelType = WHEEL_TYPE.WINNER.value
                )
            )

            wheelDao.insertWheel(
                Wheel(
                    id = WHEEL_TYPE.EATOPTION.value,
                    title = getAppString(R.string.wheel_init_1),
                    countRepeat = 1,
                    wheelType = WHEEL_TYPE.EATOPTION.value
                )
            )

            wheelDao.insertWheel(
                Wheel(
                    id = WHEEL_TYPE.YESNO.value,
                    title = getAppString(R.string.wheel_init_3),
                    countRepeat = 4,
                    wheelType = WHEEL_TYPE.YESNO.value
                )
            )

            for (optionWheel in listOptionWheelEat()) {
                optionWheelDao.insertOptionWithoutWheel(optionWheel)
            }

            for (optionWheel in listOptionWheelWinner()) {
                optionWheelDao.insertOptionWithoutWheel(optionWheel)
            }

            for (optionWheel in listOptionWheelEatOption()) {
                optionWheelDao.insertOptionWithoutWheel(optionWheel)
            }

            for (optionWheel in listOptionWheelYesNo()) {
                optionWheelDao.insertOptionWithoutWheel(optionWheel)
            }
            SpinWheelPreferences.isInitDefault = true
        }
    }

    override fun insertWheel(wheel: Wheel) {
        wheelDao.insertWheel(wheel)
    }

    override fun updateWheel(wheel: Wheel) {
        wheelDao.updateWheel(wheel)
    }

    override fun getAllWheel(): List<Wheel> {
        return wheelDao.getAllWheel()
    }

    override fun getWheel(id: Int?): Wheel? {
        return wheelDao.getWheel(id)
    }

    override fun deleteWheel(id: Int?) {
        wheelDao.deleteWheel(id)
        optionWheelDao.deleteAllOptionWheelWhenWheel(id)
    }

    override fun duplicateWheel(wheel: Wheel) {
        val list = wheelDao.getAllWheel()
        val id = if (list.isEmpty()) {
            wheel.id + 1
        } else {
            list.last().id + 1
        }
        val newItem = wheel.copy(id = id)
        wheelDao.insertWheel(newItem)

        val listOptionWheelAll = optionWheelDao.getAllOptionWithoutWheel()
        val newId = if (listOptionWheelAll.isNotEmpty()) {
            listOptionWheelAll.last().id + 1
        } else {
            1
        }

        val listOptionWheel = optionWheelDao.getAllOptionWheel(wheel.id)
        listOptionWheel.forEachIndexed { index, optionWheel ->
            optionWheelDao.insertOptionWheel(optionWheel.copy(wheelId = id, id = newId + index))
        }
    }

    private fun listOptionWheelEat(): List<OptionWheel> {
        val list: MutableList<OptionWheel> = arrayListOf()
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EAT.value,
                content = "Sandwich",
                color = getAppColor(R.color.option_color_1)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EAT.value,
                content = "Salad",
                color = getAppColor(R.color.option_color_2)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EAT.value,
                content = "Sushi",
                color = getAppColor(R.color.option_color_3)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EAT.value,
                content = "Hot dog",
                color = getAppColor(R.color.option_color_4)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EAT.value,
                content = "Pizza",
                color = getAppColor(R.color.option_color_5)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EAT.value,
                content = "Bacon",
                color = getAppColor(R.color.option_color_6)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EAT.value,
                content = "BBQ",
                color = getAppColor(R.color.option_color_7)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EAT.value,
                content = "Pasta",
                color = getAppColor(R.color.option_color_8)
            )
        )
        return list
    }

    private fun listOptionWheelWinner(): List<OptionWheel> {
        val list: MutableList<OptionWheel> = arrayListOf()
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.WINNER.value,
                content = "Alejandro Ganacho",
                color = getAppColor(R.color.option_color_1)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.WINNER.value,
                content = "Bruno fernandes",
                color = getAppColor(R.color.option_color_2)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.WINNER.value,
                content = "Rasmus Hojlund",
                color = getAppColor(R.color.option_color_3)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.WINNER.value,
                content = "Maguire",
                color = getAppColor(R.color.option_color_4)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.WINNER.value,
                content = "Martinez",
                color = getAppColor(R.color.option_color_5)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.WINNER.value,
                content = "Mainoo",
                color = getAppColor(R.color.option_color_6)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.WINNER.value,
                content = "Atony",
                color = getAppColor(R.color.option_color_7)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.WINNER.value,
                content = "Rashford",
                color = getAppColor(R.color.option_color_8)
            )
        )
        return list
    }

    private fun listOptionWheelEatOption(): List<OptionWheel> {
        val list: MutableList<OptionWheel> = arrayListOf()
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EATOPTION.value,
                content = "Option 1",
                color = getAppColor(R.color.option_color_8)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EATOPTION.value,
                content = "Option 2",
                color = getAppColor(R.color.option_color_1)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EATOPTION.value,
                content = "Option 3",
                color = getAppColor(R.color.option_color_5)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.EATOPTION.value,
                content = "Option 4",
                color = getAppColor(R.color.option_color_7)
            )
        )
        return list
    }

    private fun listOptionWheelYesNo(): List<OptionWheel> {
        val list: MutableList<OptionWheel> = arrayListOf()
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.YESNO.value,
                content = "Yes",
                color = getAppColor(R.color.option_color_6)
            )
        )
        list.add(
            OptionWheel(
                wheelId = WHEEL_TYPE.YESNO.value,
                content = "No",
                color = getAppColor(R.color.option_color_3)
            )
        )
        return list
    }
}