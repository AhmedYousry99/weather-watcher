package com.senseicoder.weatherwatcher.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Entity(tableName = AlertDTO.ENTITY_NAME)
@TypeConverters(AlertDTOTypeConverter::class)
data class AlertDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
                    val fromTime: String,
                    val toTime: String,
                    val fromDate:String,
                    val toDate: String,
    val isAlarm: Boolean = false,

    var fromTimeLDT: LocalDateTime = LocalDateTime.now(),

    var toTimeLDT: LocalDateTime = LocalDateTime.now()
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        Gson().fromJson(parcel.readString().toString(), LocalDateTime::class.java),
        Gson().fromJson(parcel.readString().toString(), LocalDateTime::class.java),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(fromTime)
        parcel.writeString(toTime)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
        parcel.writeByte(if (isAlarm) 1 else 0)
        parcel.writeString(Gson().toJson(fromTimeLDT))
        parcel.writeString(Gson().toJson(toTimeLDT))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlertDTO> {

        const val ENTITY_NAME = "alert"

        override fun createFromParcel(parcel: Parcel): AlertDTO {
            return AlertDTO(parcel)
        }

        override fun newArray(size: Int): Array<AlertDTO?> {
            return arrayOfNulls(size)
        }
    }
}


class AlertDTOTypeConverter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime?): String? {
        return localDateTime?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateString: String?): LocalDateTime? {
        return dateString?.let {
            return LocalDateTime.parse(it, formatter)
        }
    }
    companion object{
        private const val TAG = "ForecastDTO"
    }

}