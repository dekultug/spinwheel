package com.amvfunny.dev.wheelist.data.repo.wheel

import com.amvfunny.dev.wheelist.data.model.Wheel

interface IWheelRepo {

    fun initWheel()

    fun insertWheel(wheel: Wheel)

    fun updateWheel(wheel: Wheel)

    fun getAllWheel(): List<Wheel>

    fun getWheel(id: Int?): Wheel?

    fun deleteWheel(id: Int?)

    fun duplicateWheel(wheel: Wheel)
}