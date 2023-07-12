package com.example.week2_v1.ui.profile
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date

data class ReviewItem(
    var title: String?,
    var author: String?,
    var date: LocalDate?,
    val startPage: Int?,
    val endPage: Int?,
    var log1: String?,
    var log1page: Int?,
    var log2: String?,
    var log3: String?,
    var id : Int,
    var reader: String?,
    var readerName: String?,
    var readerImage: String?

) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(LocalDate::class.java.classLoader)?.let { it as LocalDate },
        parcel.readValue(Int::class.java.classLoader)?.let { it as Int },
        parcel.readValue(Int::class.java.classLoader)?.let { it as Int },
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader)?.let { it as Int },
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )


    @RequiresApi(Build.VERSION_CODES.O)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(date?.toString())
        parcel.writeValue(startPage) // Change this
        parcel.writeValue(endPage) // Change this
        parcel.writeString(log1)
        parcel.writeValue(log1page) // Change this
        parcel.writeString(log2)
        parcel.writeString(log3)
        parcel.writeString(reader)
        parcel.writeString(readerImage)
        parcel.writeString(readerName)
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
