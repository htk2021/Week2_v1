package com.example.week2_v1.ui.profile
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date

data class ReviewItem(
    var title: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    var message: String?,
    var star: Int?
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()?.let { LocalDate.parse(it) },
        parcel.readString()?.let { LocalDate.parse(it) },
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(startDate?.toString())
        parcel.writeString(endDate?.toString())
        parcel.writeString(message)
        parcel.writeValue(star)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReviewItem> {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun createFromParcel(parcel: Parcel): ReviewItem {
            return ReviewItem(parcel)
        }

        override fun newArray(size: Int): Array<ReviewItem?> {
            return arrayOfNulls(size)
        }
    }
}
