package com.example.week2_v1

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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week2_v1.ui.profile.MyRecyclerAdapter
import com.example.week2_v1.ui.profile.ReviewItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.Call
import okhttp3.Callback
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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class OthersProfileActivity : AppCompatActivity(), MyRecyclerAdapter.ItemClickListener {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: MyRecyclerAdapter
    private var mreviewItems: ArrayList<ReviewItem> = ArrayList()
    private lateinit var dialogView: View
    private var userEmail: String? = null
    private var isButtonClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others_profile)

        val item: Friendship? = intent.getSerializableExtra("item") as? Friendship
        userEmail = item?.email //이건 지금 들어가있는 프로필의 계정 (타 계정)
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null) //이건 내 계정

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)

        mRecyclerAdapter = MyRecyclerAdapter()
        mRecyclerAdapter.setClickListener(this)

        mRecyclerView.adapter = mRecyclerAdapter
        mRecyclerAdapter.setReviewList(mreviewItems)

        val followbutton: Button= findViewById(R.id.follow)

        checkFriendshipStatus(userId, userEmail, followbutton)

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

        if (userId != null) {
            fetchUserInformation(userEmail)
        }

        fetchReviews(userEmail)
    }

    private fun checkFriendshipStatus(userId: String?, userEmail: String?, followbutton: Button) {
        val client = OkHttpClient()
        val requestBody = JSONObject()
            .put("email1", userId)
            .put("email2", userEmail)
            .toString()
        val url = GlobalApplication.v_url+"/arewefriends" // 서버의 API 엔드포인트
        val request = Request.Builder()
            .url(url)  // 서버 URL을 적어주세요.
            .post(RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), requestBody))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // 네트워크 호출 실패 처리 (예: 토스트 메시지 출력 등)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    try {
                        val jsonResponse = JSONObject(it.string())
                        val result = jsonResponse.getBoolean("result")
                        runOnUiThread {
                            if (result) {
                                followbutton.text = "Following"
                                isButtonClicked = true
                            } else {
                                followbutton.text = "Follow"
                                isButtonClicked = false
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        // JSON 파싱 오류 처리
                    }
                }
            }
        })
    }

    private fun fetchReviews(email: String?) {
        val url = GlobalApplication.v_url+"/user/reviews/$email" // 서버의 API 엔드포인트
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
                                val readerName = reviewJson.getString("name")       // name 정보 추출
                                val readerImage = reviewJson.getString("image")
                                // 필요한 다른 데이터도 가져와서 ReviewItem에 추가
                                //후기에 띄워놓지 않더라도 일단 받아왔음

                                val reviewItem = ReviewItem(
                                    title = title,
                                    author = author,
                                    date = readDate,
                                    startPage = startPage,
                                    endPage = endPage,
                                    log1 = log1,
                                    log1page = log1page,
                                    log2 = log2,
                                    log3 = log3,
                                    id = id,
                                    reader = reader,
                                    readerName = readerName,
                                    readerImage = readerImage
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
        val url = GlobalApplication.v_url+"/user/$email" // 사용자 정보를 가져올 API 엔드포인트
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
                            val image = userJson.optString("image", null.toString())  // 변경된 부분
                            val follower = userJson.getString("follower_count")
                            val following = userJson.getString("following_count")

                            // 사용자 정보를 UI에 적용
                            val usernameTextView: TextView = findViewById(R.id.username) as TextView
                            val useremailTextView: TextView = findViewById(R.id.userid) as TextView
                            val imageView: ImageView = findViewById(R.id.imageView) as ImageView
                            val userfollower: TextView = findViewById(R.id.follower) as TextView
                            val userfollowing: TextView = findViewById(R.id.following) as TextView

                            usernameTextView.text = name
                            useremailTextView.text = email
                            userfollower.text = "팔로워 | $follower 명"
                            userfollowing.text = "팔로잉 | $following 명"


                            if (image != "null") {
                                // 이미지가 존재하는 경우 Glide를 사용하여 이미지 로드
                                Log.d("과연", "1")
                                Glide.with(this@OthersProfileActivity)
                                    .load(image)
                                    .into(imageView)
                            } else {
                                // 이미지가 없는 경우 빈 화면을 표시 (설정해야 할 기본 이미지 등)
                                Log.d("과연", "2")
                                imageView.setImageResource(R.drawable.profile_default_picture)
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
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null) //이건 내 계정
        val url = GlobalApplication.v_url+"/follow"

        val json = "application/json; charset=utf-8".toMediaTypeOrNull()
        val jsonObject = JSONObject()
        jsonObject.put("loggedInUser", userId)
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
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null) //이건 내 계정
        val url = GlobalApplication.v_url+"/unfollow"

        val json = "application/json; charset=utf-8".toMediaTypeOrNull()
        val jsonObject = JSONObject()
        jsonObject.put("loggedInUser", userId)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(view: View, position: Int) {
        val item: ReviewItem = mreviewItems[position]
        val inflater: LayoutInflater = layoutInflater
        dialogView= inflater.inflate(R.layout.activity_detail_review, null)

        val readerimage = dialogView.findViewById<ImageView>(R.id.readerimage)
        val readername = dialogView.findViewById<TextView>(R.id.readername)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val author = dialogView.findViewById<TextView>(R.id.author)
        val dateString = dialogView.findViewById<TextView>(R.id.time)
        val page = dialogView.findViewById<TextView>(R.id.page)
        val log1 = dialogView.findViewById<TextView>(R.id.log1)
        val log2 = dialogView.findViewById<TextView>(R.id.log2)
        val log3 = dialogView.findViewById<ImageView>(R.id.log3)

        val button = dialogView.findViewById<Button>(R.id.button)
        button.visibility = View.GONE

        Glide.with(readerimage.context)
            .load(item.readerImage)
            .placeholder(R.drawable.profile_default_picture)
            .into(readerimage)
        readername?.text = item.readerName
        title?.text = item.title
        author?.text = item.author
        page?.setText(item.startPage.toString()+"쪽부터"+item.endPage.toString()+"쪽까지")
        log1?.setText("\""+item.log1.toString()+"\", "+item.log1page.toString()+"p")
        log2?.setText(item.log2) //log2?.setText(item.log2.toString())
        Glide.with(log3.context)
            .load(item.log3)
            .into(log3)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        dateString.text = "${item.date?.format(dateFormatter)}"


        val dialog = Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
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
}
