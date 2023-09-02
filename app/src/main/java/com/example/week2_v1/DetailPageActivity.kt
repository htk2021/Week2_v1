package com.example.week2_v1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException


class DetailPageActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var descriptionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        imageView = findViewById(R.id.image)
        titleTextView = findViewById(R.id.title)
        authorTextView = findViewById(R.id.author)
        descriptionTextView = findViewById(R.id.summary)

        val item = intent.getSerializableExtra("item") as? Item

        item?.let {
            // 이미지 로드
            Glide.with(this)
                .load(item.image)
                .into(imageView)

            // 제목, 작가, 설명 설정
            titleTextView.text = item.title
            authorTextView.text = item.author ?: "작가 미상"
            descriptionTextView.text = item.description ?: ""

            //item 정보 보내주면서 후기 작성 페이지로 전환
            val selectButton: Button = findViewById(R.id.select_button)
            selectButton.setOnClickListener {
                val returnIntent = Intent()
                returnIntent.putExtra("item", item)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }
    }
}