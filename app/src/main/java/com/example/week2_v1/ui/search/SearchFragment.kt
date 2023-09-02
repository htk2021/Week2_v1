package com.example.week2_v1.ui.search

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week2_v1.Friendship
import com.example.week2_v1.GlobalApplication
import com.example.week2_v1.Homefeed
import com.example.week2_v1.OthersProfileActivity
import com.example.week2_v1.R
import com.example.week2_v1.SearchActivity
import com.example.week2_v1.databinding.FragmentSearchActivityBinding
import com.example.week2_v1.ui.profile.MyRecyclerAdapter
import com.example.week2_v1.ui.profile.ReviewItem
import com.google.gson.GsonBuilder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.net.URLEncoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SearchFragment : Fragment(), MyRecyclerAdapter.ItemClickListener {

    private var _binding: FragmentSearchActivityBinding? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: MyRecyclerAdapter
    private var mreviewItems: ArrayList<ReviewItem> = ArrayList()
    private lateinit var title : EditText
    private val binding get() = _binding!!
    private var currentPage: Int = 1 // 현재 페이지 번호
    private lateinit var dialogView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchActivityBinding.inflate(inflater, container, false)

        val root: View = binding.root
        title = root.findViewById<EditText>(R.id.input_friend)

        val search = root.findViewById<Button>(R.id.join_button)
        search.setOnClickListener{
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)

            currentPage = 1
            mreviewItems.clear()
            mRecyclerAdapter.notifyDataSetChanged()
            loadMoreData(currentPage)
        }

        mRecyclerView = root.findViewById(R.id.rv)
        mRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        mRecyclerAdapter = MyRecyclerAdapter()
        mRecyclerAdapter.setClickListener(this)

        mRecyclerView.adapter = mRecyclerAdapter
        mRecyclerAdapter.setReviewList(mreviewItems)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = gridLayoutManager.itemCount
                val lastVisibleItem = gridLayoutManager.findLastCompletelyVisibleItemPosition()

                // Check if the end of the list has been reached
                if (totalItemCount <= lastVisibleItem + 2) { // 2 means two items before end of list
                    // Load more data
                    //val nextPage = totalItemCount / 6 + 1  // Since you load 6 items each time
                    loadMoreData(currentPage)
                }
            }
        })

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(view: View, position: Int) {
        val item: ReviewItem = mreviewItems[position]
        val inflater: LayoutInflater = layoutInflater
        dialogView= inflater.inflate(R.layout.activity_detail_review, null)

        val readerimage = dialogView.findViewById<ImageView>(R.id.readerimage)
        val readername = dialogView.findViewById<TextView>(R.id.readername)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val author = dialogView.findViewById<TextView>(R.id.author)
        val dateString = dialogView.findViewById<TextView>(R.id.time)
        val page = dialogView.findViewById<TextView>(R.id.page)
        val log1 = dialogView.findViewById<TextView>(R.id.log1)
        val log2 = dialogView.findViewById<TextView>(R.id.log2)
        val log3 = dialogView.findViewById<ImageView>(R.id.log3)

        val button = dialogView.findViewById<Button>(R.id.button)
        button.visibility = View.GONE

        Glide.with(readerimage.context)
            .load(item.readerImage)
            .placeholder(R.drawable.profile_default_picture)
            .into(readerimage)
        readername?.text = item.readerName
        title?.text = item.title
        author?.text = item.author
        page?.setText(item.startPage.toString()+"쪽부터"+item.endPage.toString()+"쪽까지")
        log1?.setText("\""+item.log1.toString()+"\", "+item.log1page.toString()+"p")
        log2?.setText(item.log2) //log2?.setText(item.log2.toString())
        Glide.with(log3.context)
            .load(item.log3)
            .into(log3)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        dateString.text = "${item.date?.format(dateFormatter)}"

        val readerView = dialogView.findViewById<View>(R.id.reader)
        readerView?.setOnClickListener {
            // Friendship 객체를 생성합니다.
            val friendship = item.reader?.let { it1 ->
                item.readerName?.let { it2 ->
                    Friendship(
                        it1,
                        it2,
                        item.readerImage
                    )
                }
            }

            // OtherProfileActivity를 시작합니다.
            val intent = Intent(requireContext(), OthersProfileActivity::class.java)
            intent.putExtra("item", friendship)
            startActivity(intent)
        }


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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadMoreData(page: Int) {
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
        val inputTitle = title.text.toString() // EditText에서 title 텍스트 추출
        Log.d("title", "$inputTitle")

        val url = "${GlobalApplication.v_url}/reviewsearchwithtitle?reader=$userId&title=$inputTitle&page=$page" // URL에 쿼리 파라미터 추가

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        Log.d("지금 몇페이지", "$page")

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
                            if (reviewsArray.length() > 0) {
                                // 데이터가 있는 경우만 페이지 번호 증가
                                currentPage++
                            }
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
                                val readerName = reviewJson.getString("name")
                                val readerImage = reviewJson.getString("image")
                                val id = reviewJson.getInt("id")
                                val reviewItem = ReviewItem(
                                    title = title,
                                    author = author,
                                    date = readDate,
                                    startPage = startPage,
                                    endPage = endPage,
                                    log1 = log1,
                                    log1page = log1page,
                                    log2 = log2,
                                    log3 = log3,
                                    id = id,
                                    reader = reader,
                                    readerName = readerName,
                                    readerImage = readerImage
                                )

                                mreviewItems.add(reviewItem)
                                Log.d("받아와지는지0","$reviewItem")
                            }
                            mRecyclerAdapter.setReviewList(mreviewItems)
                            Log.d("받아와지는지","")
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