package com.example.week2_v1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException


class JoinActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var joinButton: Button
    private lateinit var deleteButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        nameEditText = findViewById(R.id.join_name)
        emailEditText = findViewById(R.id.join_email)
        passwordEditText = findViewById(R.id.join_password)
        joinButton = findViewById(R.id.join_button)
        deleteButton = findViewById(R.id.delete)

        joinButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            saveUserToMySQL(name, email, password)
        }

        deleteButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }

    private fun saveUserToMySQL(name: String, email: String, password: String) {
        val url = GlobalApplication.v_url+"/saveUser" // MySQL 서버 URL

        val json = """
        {
            "name": "$name",
            "email": "$email",
            "password": "$password"
        }
        """.trimIndent()

        val requestBody = RequestBody.create("application/json".toMediaType(), json)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // 사용자 정보 저장 성공
                    runOnUiThread {
                        Toast.makeText(this@JoinActivity, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        // 회원가입 완료 후 다음 작업 수행

                        // SharedPreferences에 email 저장
                        val editor = sharedPreferences.edit()
                        editor.putString("userId", email)
                        editor.apply()

                        val intent = Intent(this@JoinActivity, MainActivity::class.java)
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }
                } else {
                    // 사용자 정보 저장 실패
                    runOnUiThread {
                        Toast.makeText(this@JoinActivity, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }

                    val intent = Intent(this@JoinActivity, LoginActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패
                runOnUiThread {
                    Toast.makeText(this@JoinActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }

                val intent = Intent(this@JoinActivity, LoginActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        })
    }
}
