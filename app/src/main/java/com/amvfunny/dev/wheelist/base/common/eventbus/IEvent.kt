package com.amvfunny.dev.wheelist.base.common.eventbus

import com.amvfunny.dev.wheelist.data.model.OptionWheel
import com.amvfunny.dev.wheelist.data.model.Wheel
import com.amvfunny.dev.wheelist.presentaition.languague.LANGUAGE_TYPE

interface IEvent

class UpdateOptionWheel(val optionWheel: OptionWheel) : IEvent
class UpdateWheelEvent(val wheel: Wheel?) : IEvent
class UpdateWheelRouletteEvent(val wheel: Wheel?) : IEvent

class CustomColorEvent(val color: Int?): IEvent
class SettingRouletteEvent(val valueTime: Int, val valueSpeed: Int, val valueStop: Boolean): IEvent
class CloseSpinEvent(): IEvent

class ReloadNativeHome(): IEvent
class ReLoadNativeAllRoulette(): IEvent
class ReloadNativeAllSpinRoulette(): IEvent
class ReloadNativeAllAddEdit(): IEvent

