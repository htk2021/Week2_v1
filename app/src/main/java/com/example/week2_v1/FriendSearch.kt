package com.example.week2_v1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.week2_v1.databinding.ActivityFriendSearchBinding
import com.example.week2_v1.databinding.ActivitySearchBinding
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.net.URLEncoder

class FriendSearch : AppCompatActivity() {

    private lateinit var binding: ActivityFriendSearchBinding
    private lateinit var friendshipAdapter: FriendsListAdapter

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendSearchBinding.inflate(layoutInflater)
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
            val url = GlobalApplication.v_url+"/alluser/$query"

            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .build()

            client.newCall(request).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    Log.d("Body", "$body")
                    val friendships = GsonBuilder().create().fromJson(body, Array<Friendship>::class.java).toList()

                    runOnUiThread {
                        showSearchResults(friendships)
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

    private fun showSearchResults(items: List<Friendship>) {
        binding.rv.layoutManager = LinearLayoutManager(this)
        friendshipAdapter = FriendsListAdapter(items)
        binding.rv.adapter = friendshipAdapter

        Log.d("FriendSearchActivity", "Search Results: $items")
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputTitle.windowToken, 0)
    }
}
