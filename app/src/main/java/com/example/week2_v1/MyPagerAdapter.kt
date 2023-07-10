package com.example.week2_v1

import android.provider.ContactsContract.Profile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.week2_v1.ui.feed.FeedFragment
import com.example.week2_v1.ui.profile.ProfileFragment
import com.example.week2_v1.ui.search.SearchFragment

class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragment()
            1 -> FeedFragment()
            2 -> SearchFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getCount(): Int {
        return 3
    }
}