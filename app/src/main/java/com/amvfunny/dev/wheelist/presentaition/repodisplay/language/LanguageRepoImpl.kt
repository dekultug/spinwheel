package com.amvfunny.dev.wheelist.presentaition.repodisplay.language

import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.languague.LANGUAGE_TYPE
import com.amvfunny.dev.wheelist.presentaition.languague.LanguageDisplay
import com.amvfunny.dev.wheelist.presentaition.repodisplay.language.ILanguageRepo
import javax.inject.Inject

class LanguageRepoImpl @Inject constructor(): ILanguageRepo {
    override fun getListLanguage(): List<LanguageDisplay> {
        val list: MutableList<LanguageDisplay> = arrayListOf()
//        val currentLanguage = LANGUAGE_TYPE.getType(SpinWheelPreferences.valueCodeLanguage)
        list.add(LanguageDisplay(type = LANGUAGE_TYPE.ENGLISH, false))
        list.add(LanguageDisplay(type = LANGUAGE_TYPE.SPANISH, false))
        list.add(LanguageDisplay(type = LANGUAGE_TYPE.FRENCH,false))
        list.add(LanguageDisplay(type = LANGUAGE_TYPE.HINDI,false))
        list.add(LanguageDisplay(type = LANGUAGE_TYPE.PORTUGUESE,false))
        return list
    }
}