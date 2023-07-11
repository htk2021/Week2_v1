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
import com.example.week2_v1.R
import com.example.week2_v1.SearchAdapter
import com.example.week2_v1.databinding.ActivitySearchBinding
import com.example.week2_v1.databinding.FragmentFeedActivityBinding
import com.example.week2_v1.ui.profile.MyRecyclerAdapter
import com.example.week2_v1.ui.profile.ReviewItem
import okhttp3.OkHttpClient
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
        val search = root.findViewById<EditText>(R.id.input_friend)
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
        val button = dialogView.findViewById<Button>(R.id.button)

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
        button.setOnClickListener {
            val intent = Intent(requireActivity(), Editpage_activity::class.java)
            intent.putExtra("editedreview", item as Parcelable)
            intent.putExtra("position", position)
            startActivityForResult(intent, 33)
        }
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
}