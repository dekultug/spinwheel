package com.amvfunny.dev.wheelist.di

import com.amvfunny.dev.wheelist.presentaition.repodisplay.color.ColorDisplayImpl
import com.amvfunny.dev.wheelist.presentaition.repodisplay.color.IColorRepo
import com.amvfunny.dev.wheelist.presentaition.repodisplay.language.ILanguageRepo
import com.amvfunny.dev.wheelist.presentaition.repodisplay.language.LanguageRepoImpl
import com.amvfunny.dev.wheelist.presentaition.repodisplay.spinplay.ISpinPlayRepo
import com.amvfunny.dev.wheelist.presentaition.repodisplay.spinplay.SpinPlayRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoDisplayModule {
    @Binds
    abstract fun bindRepoDisplay(
        repo: LanguageRepoImpl
    ): ILanguageRepo

    @Binds
    abstract fun bindColorRepoDisplay(
        repo: ColorDisplayImpl
    ): IColorRepo

    @Binds
    abstract fun bindSpinSizeRepoDisplay(
        repo: SpinPlayRepoImpl
    ): ISpinPlayRepo
}
