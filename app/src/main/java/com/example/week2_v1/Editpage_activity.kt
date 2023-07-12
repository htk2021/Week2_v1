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
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.net.URLEncoder
import kotlin.properties.Delegates

class Editpage_activity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private lateinit var datetext: TextView
    private var date: LocalDate? = null
    private val ADD_PAGE_REQUEST_CODE = 123
    private var selectedImageUri: Uri? = null

    private lateinit var searchButton: TextView
    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var imageView: ImageView
    private var id by Delegates.notNull<Int>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editpage)

        //황태경 추가
        searchButton = findViewById(R.id.editTitle)
        searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            Log.d("AddpageActivity", "Going to SearchActivity")

            finish()
        }
        titleTextView = findViewById(R.id.title)
        authorTextView = findViewById(R.id.author)
        descriptionTextView = findViewById(R.id.bookdetail)
        imageView = findViewById(R.id.poster)
        val item = intent.getSerializableExtra("item") as? Item
        item?.let {
            titleTextView.text = item.title
            authorTextView.text = item.author
            descriptionTextView.text = item.description
            Glide.with(this)
                .load(item.image)
                .into(imageView)
        }


        val dateString = findViewById<TextView>(R.id.time)
        val page1 = findViewById<EditText>(R.id.page1)
        val page2 = findViewById<EditText>(R.id.page2)
        val log1 = findViewById<EditText>(R.id.log1)
        val log1page = findViewById<EditText>(R.id.log1page)
        val log2 = findViewById<EditText>(R.id.log2)
        val log3 = findViewById<ImageView>(R.id.log3)



        val url = GlobalApplication.v_url+"/editreviewsstart?userId=$id"  // 로드

        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    // API 요청 실패 처리
                    // 에러 처리 로직 구현
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    runOnUiThread {
                        try {
                            val reviewsArray = JSONArray(responseBody)
                            for (i in 0 until reviewsArray.length()) {
                                val reviewJson = reviewsArray.getJSONObject(i)
                                val title1 = reviewJson.getString("title")
                                val author1 = reviewJson.getString("author")
                                val readDate = reviewJson.getString("read_date")
                                val photo = reviewJson.getString("photo")
                                val start_page = reviewJson.getInt("start_page")
                                val end_page = reviewJson.getInt("end_page")
                                val memorable_page = reviewJson.getInt("memorable_page")
                                val memorable_quote = reviewJson.getString("memorable_quote")
                                val comment = reviewJson.getString("comment")



                                titleTextView.text = title1
                                authorTextView.text = author1
                                Glide.with(this@Editpage_activity).load(photo).into(imageView)
                                page1.setText(start_page.toString())
                                page2.setText(end_page.toString())
                                log1.setText(memorable_quote)
                                log1page.setText(memorable_page.toString())
                                log2.setText(comment)
                                Glide.with(this@Editpage_activity)
                                    .load(photo)
                                    .into(log3)
                                val formatter =
                                    DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN)
                                val DateString = LocalDate.parse(readDate).format(formatter)
                                dateString.text = "$DateString"
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })


        log3.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(intent, 55)
        }


        // Get Button instance and set listener
        val addbutton = findViewById<Button>(R.id.addbutton)
        addbutton.setOnClickListener {
            val title = titleTextView.text.toString()
            val author = authorTextView.text.toString()
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN)
            val datetext = LocalDate.parse(dateString.text.toString(),formatter)
            val startPage = page1.text.toString().toIntOrNull() ?: 0
            val endPage = page2.text.toString().toIntOrNull() ?: 0
            val log1 = log1.text.toString()
            val log1page = log1page.text.toString().toIntOrNull() ?: 0
            val log2 = log2.text.toString()
            val log3 = selectedImageUri?.toString()
            val loggedInUser = GlobalApplication.loggedInUser ?: ""


            val addReviewUrl = GlobalApplication.v_url+"/editreviewsend" // 저장

            // Get Button instance and set listener


            // 리뷰 데이터를 JSON 형식으로 생성
            val reviewData = """
    {
        "title": "$title",
        "author": "$author",
        "photo": "$log3",
        "read_date": "$datetext",
        "start_page": $startPage,
        "end_page": $endPage,
        "memorable_page": $log1page,
        "memorable_quote": "$log1",
        "comment": "$log2",
        "reader": "$loggedInUser",
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
                        val intent = Intent(this@Editpage_activity, MainActivity::class.java)
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }
                }
            })






            val intent = Intent(this, MainActivity::class.java)
            setResult(Activity.RESULT_OK, intent)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            finish()
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

        val datebutton = findViewById<Button>(R.id.datebutton)  // DatePicker 대신 Button으로 변경했습니다.
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
                selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            dateString.text = selectedDateString
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 55 && resultCode == RESULT_OK && data != null) {
            // 이미지 선택 결과를 받음
            selectedImageUri = data.data
        }
        if (requestCode == 33 && resultCode == RESULT_OK && data != null) {
            val editedReview = data.getParcelableExtra<ReviewItem>("editedreview")
            val position = data.getIntExtra("position", -1)
            id = editedReview!!.id.toInt()

            // 여기서 editedReview와 position을 사용하는 코드를 작성하면 됩니다.
            // 예를 들면, 업데이트된 리뷰를 리스트에 반영하는 등의 작업이 될 수 있습니다.
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