package com.amvfunny.dev.wheelist.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amvfunny.dev.wheelist.data.model.OptionWheel
import com.amvfunny.dev.wheelist.data.model.Wheel

@Database(entities = [Wheel::class, OptionWheel::class], version = 1, exportSchema = false)
abstract class SpinDatabase : RoomDatabase() {
    abstract fun getWheelDao(): IWheelDao
    abstract fun getOptionWheelDao(): IWheelOptionDao
}