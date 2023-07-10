package com.example.week2_v1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.week2_v1.databinding.ActivityMainBinding
import com.example.week2_v1.ui.feed.FeedFragment
import com.example.week2_v1.ui.profile.ProfileFragment
import com.example.week2_v1.ui.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var profileFragment: ProfileFragment
    private lateinit var feedFragment: FeedFragment
    private lateinit var searchFragment: SearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileFragment = ProfileFragment()
        feedFragment = FeedFragment()
        searchFragment = SearchFragment()

        setupViewPager()
        setupBottomNavigationView()
    }

    private fun setupViewPager() {
        val viewPager = binding.viewPager
        viewPager.adapter = MyPagerAdapter(supportFragmentManager, profileFragment, feedFragment, searchFragment)
        viewPager.offscreenPageLimit = 2

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> binding.navView.selectedItemId = R.id.navigation_profile
                    1 -> binding.navView.selectedItemId = R.id.navigation_feed
                    2 -> binding.navView.selectedItemId = R.id.navigation_search
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = binding.navView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_profile -> {
                    binding.viewPager.currentItem = 0
                    true
                }
                R.id.navigation_feed -> {
                    binding.viewPager.currentItem = 1
                    true
                }
                R.id.navigation_search -> {
                    binding.viewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }

    private class MyPagerAdapter(
        fragmentManager: FragmentManager,
        profileFragment: ProfileFragment,
        feedFragment: FeedFragment,
        searchFragment: SearchFragment
    ) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragments = listOf(profileFragment, feedFragment, searchFragment)
        private val titles = listOf("Profile", "Feed", "Search")

        override fun getCount(): Int = fragments.size

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }
}
