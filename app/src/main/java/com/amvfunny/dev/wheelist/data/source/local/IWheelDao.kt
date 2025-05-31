package com.amvfunny.dev.wheelist.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amvfunny.dev.wheelist.data.model.Wheel

@Dao
interface IWheelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWheel(wheel: Wheel)
    @Update
    fun updateWheel(wheel: Wheel)
    @Query("select * from wheel ORDER BY id ASC")
    fun getAllWheel(): List<Wheel>

    @Query("select * from wheel where id = :id")
    fun getWheel(id: Int?): Wheel?

    @Query("DELETE FROM wheel WHERE id = :id")
    fun deleteWheel(id: Int?)
}