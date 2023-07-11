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
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
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
    }
}