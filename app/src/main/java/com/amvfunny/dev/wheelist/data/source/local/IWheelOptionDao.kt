package com.amvfunny.dev.wheelist.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amvfunny.dev.wheelist.data.model.OptionWheel

@Dao
interface IWheelOptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOptionWheel(optionWheel: OptionWheel)
    @Update
    fun updateOptionWheel(optionWheel: OptionWheel)

    @Query("select * from optionwheel where id = :id")
    fun getOptionWheel(id: Int): OptionWheel?

    @Query("select * from optionwheel where wheelId = :idWheel ORDER BY id ASC")
    fun getAllOptionWheel(idWheel: Int?): List<OptionWheel>

    @Query("DELETE FROM optionwheel WHERE id = :id")
    fun deleteOptionWheel(id: Int)

    @Query("delete from optionwheel where wheelId = :idWheel")
    fun deleteAllOptionWheelWhenWheel(idWheel: Int?)

    @Query("select * from optionwheel ORDER BY id ASC")
    fun getAllOptionWheelWithoutWheel(): List<OptionWheel>

//    @Query("delete from optionwheel where wheelId is null")
//    fun getAllOptionWheelWhenWheelNull(): List<OptionWheel>
}