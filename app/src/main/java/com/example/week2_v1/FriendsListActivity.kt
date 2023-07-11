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

class FriendsListActivity : AppCompatActivity() {

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
        val url = "https://short-poets-clean.loca.lt/friendsofuser?userEmail=$encodedUserEmail"

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
                    Toast.makeText(this@FriendsListActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}

class FriendsListAdapter(private val friendships: List<Friendship>) : RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friends_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendship = friendships[position]
        holder.bind(friendship)
    }

    override fun getItemCount(): Int {
        return friendships.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.rv_image)
        private val nameTextView: TextView = itemView.findViewById(R.id.rv_name)
        private val emailTextView: TextView = itemView.findViewById(R.id.rv_email)

        fun bind(friendship: Friendship) {
            nameTextView.text = friendship.name
            emailTextView.text = friendship.email

            friendship.profileImageBlob?.let { imageBlob ->
                // Blob 형태의 이미지를 처리하는 로직을 추가해주세요.
                // 예시로는 Glide를 사용하여 이미지를 표시하도록 작성하였습니다.
                Glide.with(itemView)
                    .load(imageBlob)
                    .into(profileImageView)
            } ?: run {
                // 이미지가 없는 경우에는 빈 화면을 표시하거나 기본 이미지를 설정할 수 있습니다.
                profileImageView.setImageDrawable(null) // 빈 화면을 표시하는 예시입니다.
            }
        }
    }
}

data class Friendship(
    val email: String,
    val name: String,
    val profileImageBlob: ByteArray? // Blob 형태의 프로필 이미지를 받을 필드로 수정합니다.
)