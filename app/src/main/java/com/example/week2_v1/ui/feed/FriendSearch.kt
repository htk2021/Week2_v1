package com.example.week2_v1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.week2_v1.databinding.ActivitySearchBinding
import com.example.week2_v1.databinding.SearchRecyclerviewBinding
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.io.Serializable
import java.net.URLEncoder

class FriendSearch : AppCompatActivity() {

    private val clientId = "GpcrHzcJDuOme8mLdoyt"
    private val clientSecret = "xSG6F2STlR"

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: SearchAdapter

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        binding.joinButton.setOnClickListener {
            val query = binding.inputTitle.text.toString()
            if (query.isEmpty()) {
                return@setOnClickListener
            }

            hideKeyboard()

            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "https://openapi.naver.com/v1/search/book.json?query=$encodedQuery&display=10&start=1"

            val request = Request.Builder()
                .url(url)
                .addHeader("X-Naver-Client-Id", clientId)
                .addHeader("X-Naver-Client-Secret", clientSecret)
                .method("GET", null)
                .build()
            // 전체 유저를 띄우는데, 이름이 매치될수록 상위권에 배치

            client.newCall(request).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val homefeed = GsonBuilder().create().fromJson(body, Homefeed::class.java)

                    runOnUiThread {
                        showSearchResults(homefeed.items ?: emptyList())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    // Handle failure
                    println("Failed to execute request")
                    e.printStackTrace()
                }
            })
        }
    }

    private fun showSearchResults(items: List<Item>) {
        binding.rv.layoutManager = LinearLayoutManager(this)
        searchAdapter = SearchAdapter(items)
        binding.rv.adapter = searchAdapter

        Log.d("SearchActivity", "Search Results: $items")
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputTitle.windowToken, 0)
    }
}
