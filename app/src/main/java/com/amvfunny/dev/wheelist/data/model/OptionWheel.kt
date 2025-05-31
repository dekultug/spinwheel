package com.amvfunny.dev.wheelist.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class OptionWheel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var wheelId: Int? = null,
    var content: String? = null,
    var color: Int? = null,
) : Parcelable{

}