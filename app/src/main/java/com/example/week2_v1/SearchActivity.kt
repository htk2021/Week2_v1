package com.example.week2_v1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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

class SearchActivity : AppCompatActivity() {

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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 15 && resultCode == Activity.RESULT_OK) {
            val returnedItem = data?.getSerializableExtra("item") as? Item
            returnedItem?.let {
                val returnIntent = Intent()
                returnIntent.putExtra("item", it)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }
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

class SearchAdapter(private val items: List<Item>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: SearchRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        private val imageView: ImageView = binding.rvImage

        init {
            binding.root.setOnClickListener {
                val item = items[adapterPosition]
                val context = itemView.context
                val intent = Intent(context, DetailPageActivity::class.java)
                intent.putExtra("item", item)
                (context as? AppCompatActivity)?.startActivityForResult(intent, 15)
            }
        }

        fun bind(item: Item) {
            Glide.with(itemView)
                .load(item.image)
                .into(imageView)
            binding.rvTitle.text = item.title
            binding.rvAuthor.text = item.author
            // Bind other item properties as needed
        }
    }
}

data class Homefeed (val items : List<Item>)
data class Item(
    val image : String,
    val title : String,
    val author : String,
    val description: String
) : Serializable
