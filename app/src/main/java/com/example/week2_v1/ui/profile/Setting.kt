package com.example.week2_v1.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toFile
import com.example.week2_v1.Addpage_activity
import com.example.week2_v1.GlobalApplication
import com.example.week2_v1.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var selectedImageUri: Uri
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val quit: TextView = findViewById(R.id.quit)
        quit.setOnClickListener {
            finish()
        }

        val end: TextView = findViewById(R.id.end)
        val editName: EditText = findViewById(R.id.EditName)
        val editEmail: EditText = findViewById(R.id.EditEmail)
        val editPassword: EditText = findViewById(R.id.EditPassword)
        imageView = findViewById(R.id.imageView)

        end.setOnClickListener {
            val name = editName.text.toString()
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            // 이미지 선택 여부 확인
            if (!::selectedImageUri.isInitialized) {
                // 이미지 선택 요구 등의 처리
                return@setOnClickListener
            }

            updateUserInfo(name, email, password)

            val listener = ProfileFragment() as? OnSettingExitListener
            listener?.onSettingExit()
            finish()
        }

        val editImage: TextView = findViewById(R.id.editImage)
        editImage.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = data?.data

            selectedImageUri?.let {
                this.selectedImageUri = selectedImageUri
                imageView.setImageURI(selectedImageUri)
            }
        }
    }

    private fun updateUserInfo(name: String, email: String, password: String) {
        val url = GlobalApplication.v_url+"/updateuserinfo"
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", name)
            .addFormDataPart("email", email)
            .addFormDataPart("password", password)
            .addFormDataPart("image", "profile_image.jpg", getImageRequestBody())
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

    private fun getImageRequestBody(): RequestBody {
        val imageFile = File(selectedImageUri.path)
        return RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
    }
}