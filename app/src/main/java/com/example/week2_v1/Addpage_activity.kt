package com.example.week2_v1
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
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
    private lateinit var date: TextView
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
    private val ADD_PAGE_REQUEST_CODE = 123

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addpage)

        // Get RatingBar instance and set listener
        val ratingbar: RatingBar = findViewById(R.id.ratingbarStyle)
        ratingbar.setOnRatingBarChangeListener(Listener())
        val rating: Float = ratingbar.rating

        // Get EditText instances
        val editTitle = findViewById<EditText>(R.id.editTitle)
        val editMessage = findViewById<EditText>(R.id.editMessage)
        val dateString = findViewById<TextView>(R.id.time)


        // Get ImageView instance and set listener
        val poster = findViewById<ImageView>(R.id.poster)
        poster.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(intent, 55)
        }

        // Get Button instance and set listener
        val addbutton = findViewById<Button>(R.id.addbutton)
        addbutton.setOnClickListener {
            val title = editTitle.text.toString()
            val message = editMessage.text.toString()
            val star = ratingbar.rating.roundToInt()
            val dateParts = dateString.text.split(" ~ ")
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN)

            val startDate1 = LocalDate.parse(dateParts[0].trim(), formatter)
            val endDate1 = LocalDate.parse(dateParts[1].trim(), formatter)

            val reviewItem = ReviewItem(title, startDate1, endDate1, message, star)
            Log.d("ReviewItem", "Start Date: ${reviewItem.startDate}, End Date: ${reviewItem.endDate}")


            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("newreview", reviewItem)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        // Get TextView instance
        date = findViewById<TextView>(R.id.time)

        // Set up DatePickerDialog
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select dates")
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(RangeValidator(LocalDate.now(), null))
                    .build())
            .build()

        val datebutton = findViewById<DatePicker>(R.id.datebutton)
        datebutton.setOnClickListener {
            dateRangePicker.show(supportFragmentManager, "date_range_picker")
        }

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val selectionPair = selection as androidx.core.util.Pair<Long, Long>
            val startMillis = selectionPair.first ?: return@addOnPositiveButtonClickListener
            val endMillis = selectionPair.second ?: return@addOnPositiveButtonClickListener

            val startCalendar = Calendar.getInstance().apply {
                timeInMillis = startMillis
            }
            val endCalendar = Calendar.getInstance().apply {
                timeInMillis = endMillis
            }

            startDate = LocalDate.of(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH))
            endDate = LocalDate.of(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH) + 1, endCalendar.get(Calendar.DAY_OF_MONTH))

            val startDateString = startDate?.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            val endDateString = endDate?.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            date.text = "$startDateString ~ $endDateString"
        }
    }


    class RangeValidator(private val minDate: LocalDate, private val maxDate: LocalDate?) : CalendarConstraints.DateValidator, Parcelable {
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
            val CREATOR: Parcelable.Creator<RangeValidator> = object : Parcelable.Creator<RangeValidator> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun createFromParcel(parcel: Parcel): RangeValidator {
                    return RangeValidator(parcel)
                }

                override fun newArray(size: Int): Array<RangeValidator?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    internal class Listener : OnRatingBarChangeListener {
        override fun onRatingChanged(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {

        }
    }
}
