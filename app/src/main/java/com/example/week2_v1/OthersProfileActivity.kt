package com.example.week2_v1

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week2_v1.ui.profile.MyRecyclerAdapter
import com.example.week2_v1.ui.profile.ReviewItem
import com.example.week2_v1.ui.profile.Setting
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class OthersProfileActivity : AppCompatActivity(), MyRecyclerAdapter.ItemClickListener, MyRecyclerAdapter.ItemLongClickListener {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: MyRecyclerAdapter
    private var mreviewItems: ArrayList<ReviewItem> = ArrayList()
    private val REQUEST_CODE = 1
    private val ADD_PAGE_REQUEST_CODE = 123


    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others_profile)

        userEmail = intent.getStringExtra("email")

        mreviewItems = loadReviewItems()

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mRecyclerAdapter = MyRecyclerAdapter()
        mRecyclerAdapter.setClickListener(this)
        mRecyclerAdapter.setLongClickListener(this)

        mRecyclerView.adapter = mRecyclerAdapter
        mRecyclerAdapter.setReviewList(mreviewItems)

        val iconplus: FloatingActionButton = findViewById(R.id.button1)
        iconplus.setOnClickListener {
            Log.d("MyApp", "Button clicked")
            val intent = Intent(this, Addpage_activity::class.java)
            startActivityForResult(intent, ADD_PAGE_REQUEST_CODE)
        }

        var isButtonClicked = false
        val followbutton: Button= findViewById(R.id.follow)
        followbutton.setOnClickListener{
            isButtonClicked = !isButtonClicked
            if (isButtonClicked) {
                // 버튼이 클릭된 상태일 때 수행되는 코드
                // 서버에 "on" 상태를 보낼 함수를 호출
                followbutton.text = "Following" // 버튼 텍스트 변경
                follow(userEmail)
            } else {
                // 버튼이 클릭되지 않은 상태일 때 수행되는 코드
                // 서버에 "on" 상태를 보낼 함수를 호출
                followbutton.text = "Follow" // 버튼 텍스트 변경
                unfollow(userEmail)
            }


        }

        val followerTextView: TextView = findViewById(R.id.follower)
        followerTextView.setOnClickListener {
            val intent = Intent(this, FriendsListActivity::class.java)
            startActivity(intent)
        }

        val loggedInUser = GlobalApplication.loggedInUser // 현재 사용자의 이메일
        fetchUserInformation(loggedInUser)
        fetchReviews()
    }

    private fun fetchReviews() {
        val loggedInUser = GlobalApplication.loggedInUser // 현재 사용자의 이메일
        val url = GlobalApplication.v_url+"/user/reviews/$userEmail" // 서버의 API 엔드포인트
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    // API 요청 실패 처리
                    // 에러 처리 로직 구현
                }
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    runOnUiThread {
                        try {
                            val reviewsArray = JSONArray(responseBody)
                            mreviewItems.clear()
                            for (i in 0 until reviewsArray.length()) {
                                val reviewJson = reviewsArray.getJSONObject(i)

                                val author = reviewJson.getString("author")
                                val reader = reviewJson.getString("reader")
                                val title = reviewJson.getString("title")
                                val readDateStr = reviewJson.getString("read_date")
                                val readDate = ZonedDateTime.parse(readDateStr).toLocalDate() // read_date를 LocalDate로 변환
                                val startPage = reviewJson.getInt("start_page")
                                val endPage = reviewJson.getInt("end_page")
                                val log1 = reviewJson.getString("memorable_quote")
                                val log1page = reviewJson.getInt("memorable_page")
                                val log2 = reviewJson.getString("comment")
                                val log3 = reviewJson.getString("photo")
                                val id = reviewJson.getInt("id")
                                // 필요한 다른 데이터도 가져와서 ReviewItem에 추가
                                //후기에 띄워놓지 않더라도 일단 받아왔음

                                val reviewItem = ReviewItem(
                                    title = title,
                                    date = readDate,
                                    startPage = startPage,
                                    endPage = endPage,
                                    log1 = log1,
                                    log1page = log1page,
                                    log2 = log2,
                                    log3 = log3,
                                    id = id
                                    // 필요한 다른 데이터 전달
                                )

                                mreviewItems.add(reviewItem)
                            }
                            mRecyclerAdapter.setReviewList(mreviewItems)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    runOnUiThread {
                        // API 응답 실패 처리
                        // 에러 처리 로직 구현
                    }
                }
            }
        })
    }

    private fun fetchUserInformation(email: String?) {
        val url = GlobalApplication.v_url+"/user/$userEmail" // 사용자 정보를 가져올 API 엔드포인트
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // API 요청 실패 처리
                runOnUiThread {
                    // 에러 처리 로직 구현
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    // API 응답 성공 및 사용자 정보 처리
                    runOnUiThread {
                        try {
                            val userJson = JSONObject(responseBody)
                            val name = userJson.getString("name")
                            val image = userJson.getString("image")

                            // 사용자 정보를 UI에 적용
                            val usernameTextView: TextView = findViewById(R.id.username)
                            val useremailTextView: TextView = findViewById(R.id.userid)
                            val imageView: ImageView = findViewById(R.id.imageView)

                            usernameTextView.text = name
                            useremailTextView.text = email

                            if (image.isNotEmpty()) {
                                // 이미지가 존재하는 경우 Glide를 사용하여 이미지 로드
                                Glide.with(this@OthersProfileActivity)
                                    .load(image)
                                    .into(imageView)
                            } else {
                                // 이미지가 없는 경우 빈 화면을 표시 (설정해야 할 기본 이미지 등)
                                imageView.setImageDrawable(null)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    // API 응답 실패 처리
                    runOnUiThread {
                        // 에러 처리 로직 구현
                    }
                }
            }
        })
    }
    private fun follow(email: String?) {
        val loggedInUser = GlobalApplication.loggedInUser
        val url = GlobalApplication.v_url+"/follow"

        val json = "application/json; charset=utf-8".toMediaTypeOrNull()
        val jsonObject = JSONObject()
        jsonObject.put("loggedInUser", loggedInUser)
        jsonObject.put("email", email)
        val requestBody = RequestBody.create(json, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // API 요청 실패 처리
                runOnUiThread {
                    // 에러 처리 로직 구현
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    // API 응답 성공 및 사용자 정보 처리
                    runOnUiThread {
                        try {

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    // API 응답 실패 처리
                    runOnUiThread {
                        // 에러 처리 로직 구현
                    }
                }
            }
        })
    }
    private fun unfollow(email: String?) {
        val loggedInUser = GlobalApplication.loggedInUser
        val url = GlobalApplication.v_url+"/unfollow"

        val json = "application/json; charset=utf-8".toMediaTypeOrNull()
        val jsonObject = JSONObject()
        jsonObject.put("loggedInUser", loggedInUser)
        jsonObject.put("email", email)
        val requestBody = RequestBody.create(json, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // API 요청 실패 처리
                runOnUiThread {
                    // 에러 처리 로직 구현
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    // API 응답 성공 및 사용자 정보 처리
                    runOnUiThread {
                        try {

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    // API 응답 실패 처리
                    runOnUiThread {
                        // 에러 처리 로직 구현
                    }
                }
            }
        })
    }



    override fun onItemLongClick(view: View, position: Int) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("리뷰 삭제")
        builder.setMessage("삭제하시겠습니까?")
        builder.setNegativeButton("OK") { dialog, _ ->
            mreviewItems.removeAt(position)
            mRecyclerAdapter.setReviewList(mreviewItems)
            mRecyclerAdapter.notifyDataSetChanged()
            saveReviewItems()
            dialog.dismiss()
        }
        builder.setPositiveButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(view: View, position: Int) {
        val item: ReviewItem = mreviewItems[position]
        val inflater: LayoutInflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.activity_detail_review, null)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val author = dialogView.findViewById<TextView>(R.id.author)
        val dateString = dialogView.findViewById<TextView>(R.id.time)
        val page = dialogView.findViewById<TextView>(R.id.page)
        val log1 = dialogView.findViewById<TextView>(R.id.log1)
        val log2 = dialogView.findViewById<TextView>(R.id.log2)
        val log3 = dialogView.findViewById<ImageView>(R.id.log3)
        val button = dialogView.findViewById<Button>(R.id.button)

        title?.text = item.title
        page?.setText("${item.startPage}쪽부터 ${item.endPage}쪽까지")
        log1?.setText("\"${item.log1}\", ${item.log1page}p")
        log2?.setText(item.log2)
        Glide.with(log3.context)
            .load(item.log3)
            .into(log3)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        dateString.text = item.date?.format(dateFormatter)

        val dialog = Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        button.setOnClickListener {
            val intent = Intent(this, Editpage_activity::class.java)
            intent.putExtra("editedreview", item as Parcelable)
            intent.putExtra("position", position)
            startActivityForResult(intent, 33)
        }
        dialog.setContentView(dialogView)
        dialog.show()

        dialogView.setOnTouchListener(object : View.OnTouchListener {
            private var downY = 0f
            private val CLOSE_THRESHOLD = 50
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downY = event.y
                        return true
                    }
                    MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                        if (downY - event.y > CLOSE_THRESHOLD) {
                            dialog.dismiss()
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

    private fun saveReviewItems() {
        try {
            val fos = openFileOutput("reviews.dat", Context.MODE_PRIVATE)
            val os = ObjectOutputStream(fos)
            os.writeObject(mreviewItems)
            os.close()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadReviewItems(): ArrayList<ReviewItem> {
        try {
            val fis = openFileInput("reviews.dat")
            val isObject = ObjectInputStream(fis)
            val reviews: ArrayList<ReviewItem> = isObject.readObject() as ArrayList<ReviewItem>
            isObject.close()
            fis.close()
            return reviews
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ArrayList()
    }
}