package com.example.week2_v1
import android.app.Activity
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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class Addpage_activity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private lateinit var datetext: TextView
    private var date: LocalDate? = null
    private val ADD_PAGE_REQUEST_CODE = 123
    private var selectedImageUri: Uri? = null


    //황태경이 추가한 코드. item 넘겨받는 용도
    private lateinit var searchButton: TextView
    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var imageView: ImageView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addpage)
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

        // Get EditText instances
        val title = findViewById<TextView>(R.id.title)
        val author = findViewById<TextView>(R.id.author)
        val poster = findViewById<ImageView>(R.id.poster)
        val bookdetail = findViewById<TextView>(R.id.bookdetail)
        val dateString = findViewById<TextView>(R.id.time)
        val page1 = findViewById<EditText>(R.id.page1)
        val page2 = findViewById<EditText>(R.id.page2)
        val log1 = findViewById<EditText>(R.id.log1)
        val log1page = findViewById<EditText>(R.id.log1page)
        val log2 = findViewById<EditText>(R.id.log2)
        val log3 = findViewById<ImageView>(R.id.log3)

        // Get ImageView instance and set listener
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
            val title = title.text.toString()
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN)

            val datetext = LocalDate.parse(dateString.text.toString(),formatter)
            val startPage = page1.text.toString().toIntOrNull() ?: 0
            val endPage = page2.text.toString().toIntOrNull() ?: 0
            val log1 = log1.text.toString()
            val log1page = log1page.text.toString().toIntOrNull() ?: 0
            val log2 = log2.text.toString()
            val log3 = selectedImageUri?.toString()


            val reviewItem: Parcelable =
                ReviewItem(title, datetext, startPage, endPage, log1, log1page, log2, log3)


            val intent = Intent(this, MainActivity::class.java)
            Log.d("why..itemadd","$reviewItem")
            intent.putExtra("newreview", reviewItem)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            setResult(Activity.RESULT_OK, intent)
            Log.d("NpsearchActivity!!", "Going to SearchActivity")
            finish()
        }

        // Set up DatePickerDialog
                val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select a date")
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setValidator(SingleDateValidator(LocalDate.now(), null))
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