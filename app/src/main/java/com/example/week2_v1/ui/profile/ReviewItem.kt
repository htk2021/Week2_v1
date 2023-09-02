package com.example.week2_v1.ui.profile
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.time.LocalDate

data class ReviewItem(
    var title: String?, //책 제목
    var author: String?, //책 저자
    var date: LocalDate?, //읽은 날짜
    val startPage: Int?, //읽기 시작한 페이지
    val endPage: Int?, //읽은 마지막 페이지
    var log1: String?, //인상깊은 구절
    var log1page: Int?, //인상깊은 구절이 적혀있는 페이지
    var log2: String?, //오늘의 글 기록(읽은 사람이 작성하는 후기 같은 개념)
    var log3: String?, //책 표지 이미지의 url, xml 상에서 사진 자체의 id는 일단 poster로 되어있다.
    var id: Int, //리뷰 고유의 DB상의 id
    var reader: String?, //책을 읽고 리뷰를 작성한 사람의 email
    var readerName: String?, //책을 읽고 리뷰를 작성한 사람의 name
    var readerImage: String? // 책을 읽고 리뷰를 작성한 사람의 image

) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        LocalDate.parse(parcel.readString()),
        //parcel.readValue(LocalDate::class.java.classLoader)?.let { it as LocalDate },
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
        parcel.writeValue(startPage)
        parcel.writeValue(endPage)
        parcel.writeString(log1)
        parcel.writeValue(log1page)
        parcel.writeString(log2)
        parcel.writeString(log3)
        parcel.writeInt(id)
        parcel.writeString(reader)
        parcel.writeString(readerName)
        parcel.writeString(readerImage)
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
