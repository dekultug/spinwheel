package com.amvfunny.dev.wheelist.di

import com.amvfunny.dev.wheelist.data.repo.optionwheel.IOptionWheelRepo
import com.amvfunny.dev.wheelist.data.repo.optionwheel.OptionWheelRepoImpl
import com.amvfunny.dev.wheelist.data.repo.wheel.IWheelRepo
import com.amvfunny.dev.wheelist.data.repo.wheel.WheelRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    abstract fun bindRepoWheel(
        repo: WheelRepoImpl
    ): IWheelRepo

    @Binds
    abstract fun bindRepoOptionWheel(
        repo: OptionWheelRepoImpl
    ): IOptionWheelRepo
}
