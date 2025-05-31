package com.amvfunny.dev.wheelist.data.repo.optionwheel

import com.amvfunny.dev.wheelist.data.model.OptionWheel

interface IOptionWheelRepo {

    fun insertOptionWithoutWheel(optionWheel: OptionWheel)
    fun insertOptionWheel(optionWheel: OptionWheel)
    fun updateOptionWheel(optionWheel: OptionWheel)
    fun getOptionWheel(id: Int): OptionWheel?
    fun deleteOptionWheel(id: Int)
    fun deleteAllOptionWheelWhenWheel(idWheel: Int?)
    fun getAllOptionWheel(idWheel: Int?): List<OptionWheel>

    fun getAllOptionWithoutWheel(): List<OptionWheel>
}