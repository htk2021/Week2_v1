package com.example.week2_v1.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week2_v1.Addpage_activity
import com.example.week2_v1.Editpage_activity
import com.example.week2_v1.MainActivity
import com.example.week2_v1.R
import com.example.week2_v1.SecondActivity
import com.example.week2_v1.databinding.ActivityAddpageBinding
import com.example.week2_v1.databinding.FragmentProfileActivityBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale



class ProfileFragment : Fragment(), MyRecyclerAdapter.ItemClickListener, MyRecyclerAdapter.ItemLongClickListener {

    private var _binding: FragmentProfileActivityBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: MyRecyclerAdapter
    private var mreviewItems: ArrayList<ReviewItem> = ArrayList()
    private val REQUEST_CODE = 1
    private val ADD_PAGE_REQUEST_CODE = 123



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mreviewItems = ArrayList()
        val dashboardViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileActivityBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mRecyclerView = root.findViewById(R.id.recyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        mRecyclerAdapter = MyRecyclerAdapter()
        mRecyclerAdapter.setClickListener(this)
        mRecyclerAdapter.setLongClickListener(this)

        mRecyclerView.adapter = mRecyclerAdapter
        mRecyclerAdapter.setReviewList(mreviewItems)

        val iconplus: FloatingActionButton= root.findViewById(R.id.button1)
        Log.d("MyApp", "iconplus: $iconplus")
        Log.d("MyApp", "is iconplus enabled: ${iconplus.isEnabled}")
        Log.d("MyApp", "is iconplus clickable: ${iconplus.isClickable}")


        iconplus.setOnClickListener {
            Log.d("MyApp", "Button clicked") // Add this line

            val intent = Intent(requireActivity(), Addpage_activity::class.java)
            startActivityForResult(intent,ADD_PAGE_REQUEST_CODE)
            //activity?.finish()
            Toast.makeText(requireContext(), "플로팅 버튼 클릭", Toast.LENGTH_SHORT).show()


        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
// handle the result here...
        if (requestCode == ADD_PAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val reviewItem = data.getParcelableExtra<ReviewItem>("newreview")
                Log.d("MainActivity", "New review: $reviewItem")
                reviewItem?.let {
                    mreviewItems.add(it)
                    mRecyclerAdapter.setReviewList(mreviewItems)
                    mRecyclerAdapter.notifyDataSetChanged()
                }
            }
        }
        if (requestCode == 33 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val editedReviewItem = data.getParcelableExtra<ReviewItem>("editedreview")
                val position = data.getIntExtra("position", -1)
                if (editedReviewItem != null && position != -1) {
                    // 수정된 리뷰를 업데이트
                    mreviewItems[position] = editedReviewItem
                    mRecyclerAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    override fun onItemLongClick(view: View, position: Int) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("리뷰 삭제")
        builder.setMessage("삭제하시겠습니까?")
        builder.setNegativeButton("OK") { dialog, _ ->
            mreviewItems.removeAt(position)
            mRecyclerAdapter.setReviewList(mreviewItems)
            mRecyclerAdapter.notifyDataSetChanged()
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
        val item: ReviewItem = mreviewItems[position]

        val inflater: LayoutInflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.activity_detail_review, null)

        val title: TextView = dialogView.findViewById(R.id.title)
        val poster: ImageView = dialogView.findViewById(R.id.poster)
        val message: TextView = dialogView.findViewById(R.id.message)
        val star: RatingBar = dialogView.findViewById(R.id.star)
        val time: TextView = dialogView.findViewById(R.id.time)
        val button: Button = dialogView.findViewById(R.id.button)

        title.text = item.title
        message.text = item.message
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        time.text = "${item.startDate?.format(dateFormatter)} ~ ${item.endDate?.format(dateFormatter)}"
        star.rating = item.star?.toFloat() ?: 0.0f


        val dialog = Dialog(requireContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        button.setOnClickListener {
            val intent = Intent(requireActivity(), Editpage_activity::class.java)
            intent.putExtra("editedreview", item)
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