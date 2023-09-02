package com.example.week2_v1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

class FriendsListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var follwer_text: TextView
    private lateinit var friendshipAdapter: FriendsListAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friendslist)

        recyclerView = findViewById(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        follwer_text = findViewById(R.id.input_title)
        follwer_text.text = "나의 팔로워 목록"

        getFriendshipsFromServer()
    }

    private fun getFriendshipsFromServer() {
        //val userEmail = GlobalApplication.loggedInUser ?: ""
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
        val encodedUserEmail = URLEncoder.encode(userId, "UTF-8")
        val url = GlobalApplication.v_url+"/friendsofuser?userEmail=$encodedUserEmail"

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

        init {
            itemView.setOnClickListener {
                val item = friendships[adapterPosition]
                val context = itemView.context
                val intent = Intent(context, OthersProfileActivity::class.java)
                intent.putExtra("item", item)
                context.startActivity(intent)
                (context as? AppCompatActivity)?.finish()
            }
        }

        fun bind(friendship: Friendship) {
            nameTextView.text = friendship.name
            emailTextView.text = friendship.email

            if (friendship.image != null) {
                // URL 형태의 이미지를 처리하는 로직을 추가합니다.
                Glide.with(itemView)
                    .load(friendship.image)
                    .into(profileImageView)
            } else {
                // 이미지 URL이 없는 경우에는 기본 이미지를 설정합니다.
                Glide.with(itemView)
                    .load(R.drawable.profile_default_picture)
                    .into(profileImageView)
            }
        }
    }
}

data class Friendship(
    val email: String,
    val name: String,
    val image: String? // URL 형태의 프로필 이미지로 수정합니다.
): Serializable