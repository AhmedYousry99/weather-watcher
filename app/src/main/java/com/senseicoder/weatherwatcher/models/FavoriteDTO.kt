package com.senseicoder.weatherwatcher.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = FavoriteDTO.ENTITY_NAME)
data class FavoriteDTO (
    @PrimaryKey
    val location: String,
    val longitude: String,
    val latitude: String) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(location)
        parcel.writeString(longitude)
        parcel.writeString(latitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavoriteDTO> {
        const val ENTITY_NAME = "favorite"
        override fun createFromParcel(parcel: Parcel): FavoriteDTO {
            return FavoriteDTO(parcel)
        }

        override fun newArray(size: Int): Array<FavoriteDTO?> {
            return arrayOfNulls(size)
        }
    }
}