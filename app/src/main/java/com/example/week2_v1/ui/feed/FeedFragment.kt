package com.example.week2_v1.ui.feed

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week2_v1.Addpage_activity
import com.example.week2_v1.Editpage_activity
import com.example.week2_v1.FriendSearch

import com.example.week2_v1.GlobalApplication

import com.example.week2_v1.R
import com.example.week2_v1.SearchAdapter
import com.example.week2_v1.databinding.ActivitySearchBinding
import com.example.week2_v1.databinding.FragmentFeedActivityBinding
import com.example.week2_v1.ui.profile.MyRecyclerAdapter
import com.example.week2_v1.ui.profile.ReviewItem

import okhttp3.OkHttpClient

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.io.ObjectInputStream
import java.time.ZonedDateTime

import java.time.format.DateTimeFormatter

class FeedFragment : Fragment(),MyRecyclerAdapter.ItemClickListener{

    private var _binding: FragmentFeedActivityBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val clientId = "GpcrHzcJDuOme8mLdoyt"
    private val clientSecret = "xSG6F2STlR"
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: MyRecyclerAdapter
    private var mreviewItems: ArrayList<ReviewItem> = ArrayList()
    private val binding get() = _binding!!

    private lateinit var searchAdapter: SearchAdapter

    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(FeedViewModel::class.java)

        _binding = FragmentFeedActivityBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val search = root.findViewById<TextView>(R.id.input_friend)

        search.setOnClickListener{
            val intent = Intent(requireActivity(), FriendSearch::class.java)
            startActivity(intent)
            //다이알로그
        }
        mRecyclerView = root.findViewById(R.id.rv)
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        mRecyclerAdapter = MyRecyclerAdapter()
        mRecyclerAdapter.setClickListener(this)


        mRecyclerView.adapter = mRecyclerAdapter
        mRecyclerAdapter.setReviewList(mreviewItems)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = linearLayoutManager.itemCount
                val lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()

                // Check if the end of the list has been reached
                if (totalItemCount <= lastVisibleItem + 2) { // 2 means two items before end of list
                    // Load more data
                    val nextPage = totalItemCount / 5 + 1  // Assume you load ITEMS_PER_PAGE items each time
                    loadMoreData(nextPage)

                }
            }
        })

        feedReviews()

        return root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(view: View, position: Int) {
        val item: ReviewItem = mreviewItems[position]
        val inflater: LayoutInflater = layoutInflater
        val dialogView= inflater.inflate(R.layout.activity_detail_review, null)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val author = dialogView.findViewById<TextView>(R.id.author)
        val dateString = dialogView.findViewById<TextView>(R.id.time)
        val page = dialogView.findViewById<TextView>(R.id.page)
        val log1 = dialogView.findViewById<TextView>(R.id.log1)
        val log2 = dialogView.findViewById<TextView>(R.id.log2)
        val log3 = dialogView.findViewById<ImageView>(R.id.log3)

        title?.text = item.title
        page?.setText(item.startPage.toString()+"쪽부터"+item.endPage.toString()+"쪽까지")
        log1?.setText("\""+item.log1.toString()+"\", "+item.log1page.toString()+"p")
        log2?.setText(item.log2.toString())
        Glide.with(log3.context)
            .load(item.log3)
            .into(log3)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        dateString.text = "${item.date?.format(dateFormatter)}"


        val dialog = Dialog(requireContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)

        dialog.setContentView(dialogView)
        dialog.show()

        dialogView.setOnTouchListener(object : View.OnTouchListener {
            private var downY = 0f
            private val CLOSE_THRESHOLD = 50
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downY = event.y
                        return true
                    }
                    MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                        if (downY - event.y > CLOSE_THRESHOLD) {
                            dialog.dismiss()
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

    private fun feedReviews() {
        val loggedInUser = GlobalApplication.loggedInUser // 현재 사용자의 이메일
        val url = GlobalApplication.v_url+"/user/reviews/$loggedInUser" // 서버의 API 엔드포인트
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    // API 요청 실패 처리
                    // 에러 처리 로직 구현
                }
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    activity?.runOnUiThread {
                        try {
                            val reviewsArray = JSONArray(responseBody)
                            mreviewItems.clear()
                            for (i in 0 until reviewsArray.length()) {
                                val reviewJson = reviewsArray.getJSONObject(i)

                                val author = reviewJson.getString("author")
                                val reader = reviewJson.getString("reader")
                                val title = reviewJson.getString("title")
                                val readDateStr = reviewJson.getString("read_date")
                                val readDate = ZonedDateTime.parse(readDateStr).toLocalDate() // read_date를 LocalDate로 변환
                                val startPage = reviewJson.getInt("start_page")
                                val endPage = reviewJson.getInt("end_page")
                                val log1 = reviewJson.getString("memorable_quote")
                                val log1page = reviewJson.getInt("memorable_page")
                                val log2 = reviewJson.getString("comment")
                                val log3 = reviewJson.getString("photo")
                                // 필요한 다른 데이터도 가져와서 ReviewItem에 추가
                                //후기에 띄워놓지 않더라도 일단 받아왔음

                                val reviewItem = ReviewItem(
                                    title = title,
                                    date = readDate,
                                    startPage = startPage,
                                    endPage = endPage,
                                    log1 = log1,
                                    log1page = log1page,
                                    log2 = log2,
                                    log3 = log3,
                                    id = id
                                    // 필요한 다른 데이터 전달
                                )

                                mreviewItems.add(reviewItem)
                            }
                            mRecyclerAdapter.setReviewList(mreviewItems)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        // API 응답 실패 처리
                        // 에러 처리 로직 구현
                    }
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun loadMoreData(page: Int) {
        val loggedInUser = GlobalApplication.loggedInUser // 현재 사용자의 이메일
        val url = "https://famous-parrots-feel.loca.lt/user/feedscroll/$loggedInUser?page=$page" // 서버의 API 엔드포인트
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    // API 요청 실패 처리
                    // 에러 처리 로직 구현
                }
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    activity?.runOnUiThread {
                        try {
                            val reviewsArray = JSONArray(responseBody)
                            for (i in 0 until reviewsArray.length()) {
                                val reviewJson = reviewsArray.getJSONObject(i)

                                val author = reviewJson.getString("author")
                                val reader = reviewJson.getString("reader")
                                val title = reviewJson.getString("title")
                                val readDateStr = reviewJson.getString("read_date")
                                val readDate = ZonedDateTime.parse(readDateStr).toLocalDate() // read_date를 LocalDate로 변환
                                val startPage = reviewJson.getInt("start_page")
                                val endPage = reviewJson.getInt("end_page")
                                val log1 = reviewJson.getString("memorable_quote")
                                val log1page = reviewJson.getInt("memorable_page")
                                val log2 = reviewJson.getString("comment")
                                val log3 = reviewJson.getString("photo")
                                val reviewItem = ReviewItem(
                                    title = title,
                                    date = readDate,
                                    startPage = startPage,
                                    endPage = endPage,
                                    log1 = log1,
                                    log1page = log1page,
                                    log2 = log2,
                                    log3 = log3,
                                    id = id
                                )

                                mreviewItems.add(reviewItem)
                            }
                            mRecyclerAdapter.setReviewList(mreviewItems)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        // API 응답 실패 처리
                        // 에러 처리 로직 구현
                    }
                }
            }
        })
    }



}