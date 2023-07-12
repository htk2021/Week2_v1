package com.example.week2_v1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
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
import com.example.week2_v1.ui.profile.ProfileFragment
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.io.Serializable
import java.net.URLEncoder

class FriendsListActivity2 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var friendshipAdapter: FriendsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friendslist)

        recyclerView = findViewById(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getFriendshipsFromServer()
    }

    private fun getFriendshipsFromServer() {
        val userEmail = GlobalApplication.loggedInUser ?: ""
        Log.d("계정주인", "$userEmail")
        val encodedUserEmail = URLEncoder.encode(userEmail, "UTF-8")
        val url = GlobalApplication.v_url+"/friendsofuser2?userEmail=$encodedUserEmail"

        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val friendships = GsonBuilder().create().fromJson(body, Array<Friendship>::class.java).toList()
                runOnUiThread {
                    friendshipAdapter = FriendsListAdapter(friendships)
                    recyclerView.adapter = friendshipAdapter
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패
                runOnUiThread {
                    Toast.makeText(this@FriendsListActivity2, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}

