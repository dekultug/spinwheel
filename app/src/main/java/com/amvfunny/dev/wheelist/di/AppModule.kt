package com.amvfunny.dev.wheelist.di

import android.content.Context
import androidx.room.Room
import com.amvfunny.dev.wheelist.data.source.local.IWheelDao
import com.amvfunny.dev.wheelist.data.source.local.IWheelOptionDao
import com.amvfunny.dev.wheelist.data.source.local.SpinDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): SpinDatabase {
        return Room.databaseBuilder(
            appContext,
            SpinDatabase::class.java,
            "SpinDatabase"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun providerWheelDao(database: SpinDatabase): IWheelDao {
        return database.getWheelDao()
    }

    @Provides
    @Singleton
    fun providerOptionWheelDao(database: SpinDatabase): IWheelOptionDao {
        return database.getOptionWheelDao()
    }
}