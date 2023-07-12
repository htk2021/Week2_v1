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
import com.example.week2_v1.databinding.ActivityFriendSearchBinding
import com.example.week2_v1.databinding.SearchRecyclerviewBinding
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.io.Serializable
import java.net.URLEncoder
import java.sql.Blob

class FriendSearch : AppCompatActivity() {

    private lateinit var binding: ActivityFriendSearchBinding
    private lateinit var searchAdapter: UserSearchAdapter

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
            // 전체 유저를 띄우는데, 이름이 매치될수록 상위권에 배치

            client.newCall(request).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    Log.d("Body", "$body")
                    val users = GsonBuilder().create().fromJson(body, Array<User>::class.java).toList()

                    runOnUiThread {
                        showSearchResults(users)
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

    private fun showSearchResults(items: List<User>) {
        binding.rv.layoutManager = LinearLayoutManager(this)
        searchAdapter = UserSearchAdapter(items)
        binding.rv.adapter = searchAdapter

        Log.d("FriendSearchActivity", "Search Results: $items")
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputTitle.windowToken, 0)
    }
}

data class UserGroup (val items : List<User>)
data class User(
    val name: String,
    val email: String,
    val image: Blob?
) : Serializable

class UserSearchAdapter(private val users: List<User>) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friends_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.name
        holder.email.text = user.email

        Glide.with(holder.itemView.context)
            .load(user.image)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.rv_name)
        val email: TextView = view.findViewById(R.id.rv_email)
        val imageView: ImageView = view.findViewById(R.id.rv_image)
    }
}
