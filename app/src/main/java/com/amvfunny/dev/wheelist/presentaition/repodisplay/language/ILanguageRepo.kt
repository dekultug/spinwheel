package com.amvfunny.dev.wheelist.presentaition.repodisplay.language

import com.amvfunny.dev.wheelist.presentaition.languague.LanguageDisplay

interface ILanguageRepo {
    fun getListLanguage(): List<LanguageDisplay>
}