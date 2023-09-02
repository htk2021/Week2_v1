package com.example.week2_v1.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import com.example.week2_v1.Addpage_activity
import com.example.week2_v1.GlobalApplication
import com.example.week2_v1.LoginActivity
import com.example.week2_v1.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException

class Setting : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var editName: EditText
    private lateinit var editEmail: TextView
    private lateinit var editPassword: EditText

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        val quit: TextView = findViewById(R.id.quit)
        quit.setOnClickListener {
            finish()
        }

        val end: TextView = findViewById(R.id.end)

        val logoutButton: Button = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            logoutAndNavigateToLogin()
        }

        val unlinkButton: Button = findViewById(R.id.unlink_button)
        unlinkButton.setOnClickListener {
            deleteUserAndLogout()
        }

        editName = findViewById(R.id.EditName)
        editEmail = findViewById(R.id.EditEmail)
        editPassword = findViewById(R.id.EditPassword)
        imageView = findViewById(R.id.imageView)

        // userId가 null이 아닐 경우 서버에서 사용자 정보 가져오기
        if (userId != null) {
            fetchUserInfo(userId)
        }

        end.setOnClickListener {
            val name = editName.text.toString()
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()
            Log.d("email 확인", email)

            updateUserInfo(name, email, password)

            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun fetchUserInfo(userId: String) {
        val url = GlobalApplication.v_url+"/startSetting?email=$userId"
        val request = Request.Builder()
            .url(url)
            .get() // GET 요청으로 변경
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Log.d("fetchUserInfo 실패","")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    // 응답으로 받은 JSON 데이터 파싱 (예: Gson 사용)
                    val parsedData = Gson().fromJson(responseBody, UserInfo::class.java) // 이 부분은 예시입니다. UserInfo 클래스를 따로 정의해야 합니다.

                    runOnUiThread {
                        editName.setText(parsedData.name)
                        editEmail.text = parsedData.email
                        // parsedData.password가 null이 아니면 설정한다.
                        if (parsedData.password != null) {
                            editPassword.setText(parsedData.password)
                        }

                        // 이미지 URL이 없거나 null일 경우 기본 이미지를 띄우고, 그렇지 않으면 해당 URL의 이미지를 띄운다.
                        if ((parsedData.image == null) || parsedData.image.isEmpty()) {
                            Glide.with(this@Setting)
                                .load(R.drawable.profile_default_picture)
                                .into(imageView)
                        } else {
                            Glide.with(this@Setting)
                                .load(parsedData.image)
                                .into(imageView)
                        }

                        Log.d("fetchUserInfo 성공", "")
                    }
                } else {
                    runOnUiThread {
                        Log.d("fetchUserInfo 실패", "")
                    }
                }
            }

        })
    }

    private fun updateUserInfo(name: String, email: String, password: String) {
        val url = GlobalApplication.v_url+"/updateuserinfo"
        val requestBody = FormBody.Builder()
            .add("name", name)
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 서버 요청 실패 처리
                e.printStackTrace()
                runOnUiThread {
                    // 실패 메시지 표시 등의 UI 업데이트 작업 수행
                    Log.d("함수 실행 확인1","")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    // 서버 응답 처리
                    runOnUiThread {
                        // 응답 메시지에 따른 UI 업데이트 작업 수행
                        Log.d("함수 실행 확인2","")
                    }
                } else {
                    // 서버 응답 실패 처리
                    runOnUiThread {
                        // 실패 메시지 표시 등의 UI 업데이트 작업 수행
                        Log.d("함수 실행 확인3","")
                    }
                }
            }
        })
    }

    private fun logoutAndNavigateToLogin() {
        // userId를 null로 설정
        with(sharedPreferences.edit()) {
            putString("userId", null)
            apply()
        }

        // LoginActivity로 전환
        val intent = Intent(this@Setting, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun deleteUserAndLogout() {
        val email = sharedPreferences.getString("userId", null) ?: return

        val url = GlobalApplication.v_url + "/unlink?email=$email"

        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 서버 요청 실패 처리
                e.printStackTrace()
                runOnUiThread {
                    // 실패 메시지 표시 등의 UI 업데이트 작업 수행
                    Log.d("Unlink 실패", "")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // 유저 정보 삭제 성공 후 로그아웃 및 LoginActivity로 전환
                    logoutAndNavigateToLogin()
                } else {
                    runOnUiThread {
                        // 실패 메시지 표시 등의 UI 업데이트 작업 수행
                        Log.d("Unlink 실패", "")
                    }
                }
            }
        })
    }

    data class UserInfo(
        val name: String,
        val email: String,
        val password: String?,
        val image: String?
    )
}