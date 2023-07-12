package com.example.week2_v1

import android.content.Intent
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


class LoginEmailActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var joinButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_email)

        emailEditText = findViewById(R.id.join_email)
        passwordEditText = findViewById(R.id.join_password)
        joinButton = findViewById(R.id.join_button)
        deleteButton = findViewById(R.id.delete)

        joinButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            Log.d("zebal", "$email, $password")
            // MySQL에 사용자 정보 저장 요청
            CheckMySQL(email, password)
        }

        deleteButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }

    private fun CheckMySQL(email: String, password: String) {
        val url = GlobalApplication.v_url+"/checkUser" // MySQL 서버 URL

        val json = """
        {
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
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null && responseBody.toBoolean()) {
                    // 사용자 정보 저장 성공
                    runOnUiThread {
                        Toast.makeText(this@LoginEmailActivity, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                        GlobalApplication.loggedInUser=email

                        val intent = Intent(this@LoginEmailActivity, MainActivity::class.java)
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }
                } else {
                    // 사용자 정보 저장 실패
                    runOnUiThread {
                        Toast.makeText(this@LoginEmailActivity, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }

                    val intent = Intent(this@LoginEmailActivity, LoginActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패
                runOnUiThread {
                    Toast.makeText(this@LoginEmailActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }

                val intent = Intent(this@LoginEmailActivity, LoginActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        })
    }
}
