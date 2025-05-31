package com.amvfunny.dev.wheelist.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Wheel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String? = null,
    var countRepeat: Int? = null,
    var wheelType: Int? = null
) : Parcelable {
    fun getTypeWheel(): WHEEL_TYPE {
        return WHEEL_TYPE.getType(wheelType)
    }
}