package com.example.week2_v1.ui.profile
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week2_v1.R
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

class MyRecyclerAdapter : RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>() {
    public var mReviewList: ArrayList<ReviewItem> = ArrayList()
    private var mClickListener: ItemClickListener? = null
    private var mLongClickListener: ItemLongClickListener? = null

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface ItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyclerview, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getItemCount(): Int {
        return mReviewList.size
    }

    fun getItem(position: Int): ReviewItem? {
        return if (position < mReviewList.size) {
            mReviewList[position]
        } else {
            null
        }
    }

    fun setReviewList(list: ArrayList<ReviewItem>?) {
        mReviewList = list ?: ArrayList()
        notifyDataSetChanged()
    }

    fun setClickListener(itemClickListener: ItemClickListener) {
        mClickListener = itemClickListener
    }

    fun setLongClickListener(itemLongClickListener: ItemLongClickListener) {
        mLongClickListener = itemLongClickListener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener,
        View.OnLongClickListener {
        val title = itemView.findViewById<TextView>(R.id.title)
        val author = itemView.findViewById<TextView>(R.id.author)
        val dateString = itemView.findViewById<TextView>(R.id.time)
        val page = itemView.findViewById<TextView>(R.id.page)
        val log1 = itemView.findViewById<TextView>(R.id.log1)
        val log3 = itemView.findViewById<ImageView>(R.id.log3)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun onBind(item: ReviewItem?) {
            item?.let {
                title.text = it.title
                val formatter = DateTimeFormatter.ofPattern("MM.dd.yyyy")
                dateString.setText(it.date?.format(formatter) ?: "")
                page.setText(it.startPage.toString()+"페이지 ~ "+it.endPage.toString()+"페이지")
                log1.setText("\""+it.log1?.toString()+"\", "+ it.log1page.toString() + "p"?: it.log2.toString())
                Glide.with(log3.context)
                    .load(it.log3)
                    .into(log3)

            }
        }

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, adapterPosition)
        }

        override fun onLongClick(view: View): Boolean {
            mLongClickListener?.onItemLongClick(view, adapterPosition)
            return true
        }
    }
}

