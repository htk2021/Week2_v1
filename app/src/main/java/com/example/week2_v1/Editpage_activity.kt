package com.example.week2_v1

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.week2_v1.ui.profile.ReviewItem
import com.example.week2_v1.ui.profile.detail_review
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import android.app.Activity
import android.content.SharedPreferences
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.net.URLEncoder
import kotlin.properties.Delegates

class Editpage_activity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private lateinit var datetext: TextView
    private var date: LocalDate? = null
    private var selectedImageUri: Uri? = null

    private lateinit var searchButton: TextView

    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var posterImageView: ImageView
    private lateinit var dateStringTextView: TextView
    private lateinit var page1EditText: EditText
    private lateinit var page2EditText: EditText
    private lateinit var log1EditText: EditText
    private lateinit var log1pageEditText: EditText
    private lateinit var log2EditText: EditText

    private var returnedItem: Item? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editpage)

        //황태경 추가
        searchButton = findViewById(R.id.editTitle)
        searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)

            startActivityForResult(intent, 14) //그대로 14 해도 되겠지?
        }

        titleTextView = findViewById(R.id.title)
        authorTextView = findViewById(R.id.author)
        posterImageView = findViewById(R.id.poster) //log3
        dateStringTextView = findViewById(R.id.time)
        page1EditText = findViewById(R.id.page1)
        page2EditText = findViewById(R.id.page2)
        log1EditText = findViewById(R.id.log1)
        log1pageEditText = findViewById(R.id.log1page)
        log2EditText = findViewById(R.id.log2)

        // ReviewItem 객체와 position을 인텐트로부터 가져옴
        val item = intent.getParcelableExtra<ReviewItem>("editedreview")!!
        val position = intent.getIntExtra("position", -1)

        // 가져온 데이터를 사용하여 화면의 뷰들에 값을 설정
        titleTextView.text = item.title
        authorTextView.text = item.author
        posterImageView.let {
            Glide.with(this)
                .load(item.log3)
                .into(it)
        }
        dateStringTextView.text = item.date?.let {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            it.format(dateFormatter)
        } ?: ""
        page1EditText.setText(item.startPage.toString())
        page2EditText.setText(item.endPage.toString())
        log1EditText.setText(item.log1)
        log1pageEditText.setText(item.log1page.toString())
        log2EditText.setText(item.log2)

        // Get Button instance and set listener
        val addbutton = findViewById<Button>(R.id.addbutton)
        addbutton.setOnClickListener {
            val reviewTitle = titleTextView.text.toString()
            val reviewAuthor = authorTextView.text.toString()
            val reviewPoster = item.log3
            val reviewDateString = dateStringTextView.text.toString()
            val reviewPage1 = page1EditText.text.toString().toIntOrNull() ?: 0
            val reviewPage2 = page2EditText.text.toString().toIntOrNull() ?: 0
            val reviewlog1page = log1pageEditText.text.toString().toIntOrNull() ?: 0
            val reviewlog1 = log1EditText.text.toString()
            val reviewlog2 = log2EditText.text.toString()
            val id = item.id

            val reviewDateLocalDate = LocalDate.parse(reviewDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val addReviewUrl = GlobalApplication.v_url+"/editreviewsend" // 저장

            // 리뷰 데이터를 JSON 형식으로 생성
            val reviewData = """
                {
                    "title": "$reviewTitle",
                    "author": "$reviewAuthor",
                    "photo": "$reviewPoster",
                    "read_date": "$reviewDateString",
                    "start_page": $reviewPage1,
                    "end_page": $reviewPage2,
                    "memorable_page": $reviewlog1page,
                    "memorable_quote": "$reviewlog1",
                    "comment": "$reviewlog2",
                    "id":"$id"
                }
            """.trimIndent()

            // OkHttp를 사용하여 POST 요청을 전송
            val client = OkHttpClient()
            val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), reviewData)
            val request = Request.Builder()
                .url(addReviewUrl)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 요청 실패
                    runOnUiThread {
                        // 실패 처리 로직을 구현하세요
                        Toast.makeText(this@Editpage_activity, "실패.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    // 요청 성공
                    runOnUiThread {
                        // 성공 처리 로직을 구현하세요
                        val EditedReviewItem = ReviewItem(
                            title = reviewTitle,
                            author = reviewAuthor,
                            date = reviewDateLocalDate,
                            startPage = reviewPage1,
                            endPage = reviewPage2,
                            log1 = reviewlog1,
                            log1page = reviewlog1page,
                            log2 = reviewlog2,
                            log3 = reviewPoster,
                            id = item.id,
                            reader = item.reader,
                            readerName = item.readerName,
                            readerImage = item.readerImage
                        )
                        // 결과를 설정하고 액티비티 종료
                        val resultIntent = Intent().apply {
                            putExtra("editedreview", EditedReviewItem)
                            putExtra("position", position)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)

                        // MainActivity로 돌아가는 대신 액티비티를 종료합니다.
                        finish()
                    }
                }
            })
        }

        // Set up DatePickerDialog
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(Addpage_activity.SingleDateValidator(LocalDate.now(), null))
                    .build()
            )
            .build()

        val datebutton = findViewById<ImageView>(R.id.datebutton)  // DatePicker 대신 Button으로 변경했습니다.
        datebutton.setOnClickListener {
            datePicker.show(supportFragmentManager, "date_picker")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedMillis = selection ?: return@addOnPositiveButtonClickListener

            val selectedCalendar = Calendar.getInstance().apply {
                timeInMillis = selectedMillis
            }

            val selectedDate = LocalDate.of(
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH) + 1,
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
            )
            val selectedDateString =
                selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            dateStringTextView.text = selectedDateString
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 14 && resultCode == Activity.RESULT_OK) {
            returnedItem = data?.getSerializableExtra("item") as? Item
            returnedItem?.let { item ->
                titleTextView.text = item.title
                authorTextView.text = item.author
                Glide.with(this)
                    .load(item.image)
                    .into(posterImageView)
            }
        }
    }

    class SingleDateValidator(private val minDate: LocalDate, private val maxDate: LocalDate?) : CalendarConstraints.DateValidator, Parcelable {
        @RequiresApi(Build.VERSION_CODES.O)
        constructor(parcel: Parcel) : this(
            LocalDate.ofEpochDay(parcel.readLong()),
            parcel.readLong().let { if (it != -1L) LocalDate.ofEpochDay(it) else null }
        )

        @RequiresApi(Build.VERSION_CODES.O)
        override fun isValid(date: Long): Boolean {
            val dateToCheck = LocalDate.ofEpochDay(date / (24 * 60 * 60))
            return dateToCheck.isAfter(minDate) && (maxDate == null || dateToCheck.isBefore(maxDate))
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeLong(minDate.toEpochDay())
            parcel.writeLong(maxDate?.toEpochDay() ?: -1L)
        }
        override fun describeContents(): Int {
            return 0
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SingleDateValidator> = object : Parcelable.Creator<SingleDateValidator> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun createFromParcel(parcel: Parcel): SingleDateValidator {
                    return SingleDateValidator(parcel)
                }

                override fun newArray(size: Int): Array<SingleDateValidator?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}