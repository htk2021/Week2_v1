package com.example.week2_v1.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week2_v1.Addpage_activity
import com.example.week2_v1.Editpage_activity
import com.example.week2_v1.FriendsListActivity
import com.example.week2_v1.FriendsListActivity2
import com.example.week2_v1.GlobalApplication
import com.example.week2_v1.MainActivity
import com.example.week2_v1.R
import com.example.week2_v1.SecondActivity
import com.example.week2_v1.databinding.FragmentProfileActivityBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

interface OnSettingExitListener {
    fun onSettingExit()
}

class ProfileFragment : Fragment(), OnSettingExitListener, MyRecyclerAdapter.ItemClickListener, MyRecyclerAdapter.ItemLongClickListener {

    private var _binding: FragmentProfileActivityBinding? = null
    private lateinit var dialogView: View
    private var currentItemPosition: Int = 0

    private val binding get() = _binding!!
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: MyRecyclerAdapter
    private var mreviewItems: ArrayList<ReviewItem> = ArrayList()
    private val REQUEST_CODE = 1
    private val ADD_PAGE_REQUEST_CODE = 123

    override fun onSettingExit() {
        // Setting 액티비티에서 호출되어 프래그먼트로 돌아왔을 때의 처리 로직을 구현합니다.
        // 예를 들어 필요한 작업을 수행하거나 UI를 업데이트할 수 있습니다.
        fetchUserInformation(GlobalApplication.loggedInUser)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mreviewItems = loadReviewItems()
        val dashboardViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileActivityBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mRecyclerView = root.findViewById(R.id.recyclerView)
        mRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        mRecyclerAdapter = MyRecyclerAdapter()
        mRecyclerAdapter.setClickListener(this)
        mRecyclerAdapter.setLongClickListener(this)

        mRecyclerView.adapter = mRecyclerAdapter
        mRecyclerAdapter.setReviewList(mreviewItems)

        val iconplus: FloatingActionButton= root.findViewById(R.id.button1)
        iconplus.setOnClickListener {
            Log.d("MyApp", "Button clicked") // Add this line

            val intent = Intent(requireActivity(), Addpage_activity::class.java)
            startActivityForResult(intent,ADD_PAGE_REQUEST_CODE)
        }

        val settingbutton: Button= root.findViewById(R.id.setting)
        settingbutton.setOnClickListener {
            val intent = Intent(requireActivity(), Setting::class.java)
            startActivityForResult(intent,10)
        }
        val followerTextView: TextView = root.findViewById(R.id.follower)
        followerTextView.setOnClickListener {
            val intent = Intent(requireActivity(), FriendsListActivity2::class.java)
            startActivity(intent)
        }
        val followingTextView: TextView = root.findViewById(R.id.following)
        followingTextView.setOnClickListener {
            val intent = Intent(requireActivity(), FriendsListActivity::class.java)
            startActivity(intent)
        }
        val loggedInUser = GlobalApplication.loggedInUser // 현재 사용자의 이메일
        fetchUserInformation(loggedInUser)

        fetchReviews()

        return root
    }

    private fun fetchReviews() {
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
                                val title = reviewJson.getString("title")
                                val author = reviewJson.getString("author")
                                val readerName = reviewJson.getString("name")
                                val readerImage = reviewJson.getString("image")
                                val readDateStr = reviewJson.getString("read_date")
                                val readDate = ZonedDateTime.parse(readDateStr).toLocalDate() // read_date를 LocalDate로 변환
                                val startPage = reviewJson.getInt("start_page")
                                val endPage = reviewJson.getInt("end_page")
                                val log1 = reviewJson.getString("memorable_quote")
                                val log1page = reviewJson.getInt("memorable_page")
                                val log2 = reviewJson.getString("comment")
                                val log3 = reviewJson.getString("photo")
                                val id = reviewJson.getInt("id")
                                // 필요한 다른 데이터도 가져와서 ReviewItem에 추가
                                //후기에 띄워놓지 않더라도 일단 받아왔음

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
                                    reader = GlobalApplication.loggedInUser,
                                    readerName = readerName,
                                    readerImage = readerImage
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

    private fun fetchUserInformation(email: String?) {
        val url = GlobalApplication.v_url+"/user/$email" // 사용자 정보를 가져올 API 엔드포인트
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // API 요청 실패 처리
                activity?.runOnUiThread {
                    // 에러 처리 로직 구현
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    // API 응답 성공 및 사용자 정보 처리
                    activity?.runOnUiThread {
                        try {
                            val userJson = JSONObject(responseBody)
                            val name = userJson.getString("name")
                            val image = userJson.getString("image")
                            val follower = userJson.getString("follower_count")
                            val following = userJson.getString("following_count")

                            // 사용자 정보를 UI에 적용
                            val usernameTextView: TextView = _binding?.root?.findViewById(R.id.username) as TextView
                            val useremailTextView: TextView = _binding?.root?.findViewById(R.id.userid) as TextView
                            val imageView: ImageView = _binding?.root?.findViewById(R.id.imageView) as ImageView
                            val userfollower: TextView = _binding?.root?.findViewById(R.id.follower) as TextView
                            val userfollowing: TextView = _binding?.root?.findViewById(R.id.following) as TextView

                            usernameTextView.text = name
                            useremailTextView.text = email
                            userfollower.text = "팔로워 | $follower"
                            userfollowing.text = "팔로잉 | $following"


                            if (image.isNotEmpty()) {
                                // 이미지가 존재하는 경우 Glide를 사용하여 이미지 로드
                                Glide.with(this@ProfileFragment)
                                    .load(image)
                                    .into(imageView)
                            } else {
                                // 이미지가 없는 경우 빈 화면을 표시 (설정해야 할 기본 이미지 등)
                                imageView.setImageDrawable(null)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    // API 응답 실패 처리
                    activity?.runOnUiThread {
                        // 에러 처리 로직 구현
                    }
                }
            }
        })
    }

    override fun onItemLongClick(view: View, position: Int) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("리뷰 삭제")
        builder.setMessage("삭제하시겠습니까?")
        builder.setNegativeButton("OK") { dialog, _ ->
            mreviewItems.removeAt(position)
            mRecyclerAdapter.setReviewList(mreviewItems)
            mRecyclerAdapter.notifyDataSetChanged()
            saveReviewItems()
            dialog.dismiss()
        }
        builder.setPositiveButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(view: View, position: Int) {
        currentItemPosition = position
        showItemDialog(position)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun showItemDialog(position: Int) {
        val item: ReviewItem = mreviewItems[position]
        val inflater: LayoutInflater = layoutInflater
        dialogView= inflater.inflate(R.layout.activity_detail_review, null)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val author = dialogView.findViewById<TextView>(R.id.author)
        val readerimage = dialogView.findViewById<ImageView>(R.id.readerimage)
        val readername = dialogView.findViewById<TextView>(R.id.readername)
        val dateString = dialogView.findViewById<TextView>(R.id.time)
        val page = dialogView.findViewById<TextView>(R.id.page)
        val log1 = dialogView.findViewById<TextView>(R.id.log1)
        val log2 = dialogView.findViewById<TextView>(R.id.log2)
        val log3 = dialogView.findViewById<ImageView>(R.id.log3)
        val button = dialogView.findViewById<Button>(R.id.button)

        title?.text = item.title
        author?.text = item.author
        readername.text = item.readerName
        Glide.with(readerimage.context).load(item.readerImage).into(readerimage)
        page?.setText(item.startPage.toString()+"쪽부터"+item.endPage.toString()+"쪽까지")
        log1?.setText("\""+item.log1.toString()+"\", "+item.title+" "+item.log1page.toString()+"p")
        log2?.setText(item.log2.toString())
        Glide.with(log3.context)
            .load(item.log3)
            .into(log3)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        dateString.text = "${item.date?.format(dateFormatter)}"

        val dialog = Dialog(requireContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        button.setOnClickListener {
            val intent = Intent(requireActivity(), Editpage_activity::class.java)
            intent.putExtra("editedreview", item as Parcelable)
            intent.putExtra("position", position)
            startActivityForResult(intent, 33)
        }
        dialog.setContentView(dialogView)
        dialog.show()

        dialogView.setOnTouchListener(object : View.OnTouchListener {
            private var downX = 0f
            private val CLOSE_THRESHOLD = 50

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = event.x
                        return true
                    }
                    MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                        if (event.x - downX > CLOSE_THRESHOLD) {
                            dialog.dismiss()
                            return true
                        }
                    }
                }
                return false
            }
        })

        val scrollView = dialogView.findViewById<ScrollView>(R.id.scrollView)
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = scrollView.scrollY
            val threshold = 500 // 스크롤을 어느 정도 이동해야 아이템을 변경할지 설정합니다.
            val maxScroll = scrollView.getChildAt(0).height - scrollView.height

            if (scrollY >= maxScroll - threshold) {
                // 스크롤이 맨 아래에 도달하면 다음 아이템을 보여줍니다.
                showNextItem()
            } else if (scrollY == 0 && position > 0) {
                // 스크롤이 맨 위에 도달하면 이전 아이템을 보여줍니다.
                showPreviousItem()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNextItem() {
        if (currentItemPosition < mreviewItems.size - 1) {
            currentItemPosition++
            showItemDialog(currentItemPosition)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPreviousItem() {
        if (currentItemPosition > 0) {
            currentItemPosition--
            showItemDialog(currentItemPosition)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun saveReviewItems() {
        try {
            val fos = requireActivity().openFileOutput("reviews.dat", Context.MODE_PRIVATE)
            val os = ObjectOutputStream(fos)
            os.writeObject(mreviewItems)
            os.close()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun loadReviewItems(): ArrayList<ReviewItem> {
        try {
            val fis = requireActivity().openFileInput("reviews.dat")
            val isObject = ObjectInputStream(fis)
            val reviews: ArrayList<ReviewItem> = isObject.readObject() as ArrayList<ReviewItem>
            isObject.close()
            fis.close()
            return reviews
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ArrayList()
    }
}