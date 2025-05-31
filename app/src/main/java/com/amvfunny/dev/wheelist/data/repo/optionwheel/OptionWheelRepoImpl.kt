package com.amvfunny.dev.wheelist.data.repo.optionwheel

import com.amvfunny.dev.wheelist.data.model.OptionWheel
import com.amvfunny.dev.wheelist.data.source.local.IWheelDao
import com.amvfunny.dev.wheelist.data.source.local.IWheelOptionDao
import javax.inject.Inject

class OptionWheelRepoImpl @Inject constructor(
    private val optionWheelDao: IWheelOptionDao,
    private val wheelDao: IWheelDao
) : IOptionWheelRepo {
    override fun insertOptionWithoutWheel(optionWheel: OptionWheel) {
        optionWheelDao.insertOptionWheel(optionWheel)
    }
    override fun insertOptionWheel(optionWheel: OptionWheel) {
        val wheel = wheelDao.getWheel(optionWheel.wheelId)
        val list = wheelDao.getAllWheel().toList()
        if (list.isEmpty()) {
            optionWheel.wheelId = wheel?.id ?: 1
        } else {
            optionWheel.wheelId = wheel?.id ?: (list.last().id + 1)
        }
        optionWheelDao.insertOptionWheel(optionWheel)
    }

    override fun updateOptionWheel(optionWheel: OptionWheel) {
        optionWheelDao.updateOptionWheel(optionWheel)
    }

    override fun getOptionWheel(id: Int): OptionWheel? {
        return optionWheelDao.getOptionWheel(id)
    }

    override fun deleteOptionWheel(id: Int) {
        optionWheelDao.deleteOptionWheel(id)
    }

    override fun deleteAllOptionWheelWhenWheel(idWheel: Int?) {
        optionWheelDao.deleteAllOptionWheelWhenWheel(idWheel)
    }

    override fun getAllOptionWheel(idWheel: Int?): List<OptionWheel> {
        return optionWheelDao.getAllOptionWheel(idWheel)
    }

    override fun getAllOptionWithoutWheel(): List<OptionWheel> {
        return optionWheelDao.getAllOptionWheelWithoutWheel()
    }
}