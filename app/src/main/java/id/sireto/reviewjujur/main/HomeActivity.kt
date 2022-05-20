package id.sireto.reviewjujur.main

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import id.sireto.reviewjujur.databinding.ActivityHomeBinding
import id.sireto.reviewjujur.main.home.HomeFragment
import id.sireto.reviewjujur.main.profile.ProfileFragment
import id.sireto.reviewjujur.main.reviews.ReviewsFragment
import id.sireto.reviewjujur.utils.Auth
import id.sireto.reviewjujur.utils.UI

class HomeActivity : AppCompatActivity() {

    companion object{
        val DETAILS_TAB = arrayOf(
            "Home",
            "Reviews",
            "Profile",
        )
    }

    private lateinit var binding: ActivityHomeBinding

    private lateinit var homeFragment : HomeFragment
    private lateinit var profileFragment : ProfileFragment
    private lateinit var reviewsFragment : ReviewsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        homeFragment = HomeFragment()
        profileFragment = ProfileFragment()
        reviewsFragment = ReviewsFragment()

        setContentView(binding.root)
        setupSections()
    }

    private fun setupSections(){
        binding.homePager.isUserInputEnabled = true
        binding.homePager.offscreenPageLimit = 3
        val tabLayout = binding.tabLayout
        val adapter = SectionAdapter(homeFragment, profileFragment, reviewsFragment, this)
        binding.homePager.adapter = adapter

        TabLayoutMediator(tabLayout, binding.homePager){ tab, position ->
            tab.text = DETAILS_TAB[position]
        }.attach()
        binding.homePager.currentItem = 0
    }
}

class SectionAdapter(private val homeFragment: HomeFragment,
                     private val profileFragment: ProfileFragment,
                     private val reviewsFragment: ReviewsFragment,
                     activity : AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> homeFragment
            1 -> reviewsFragment
            2 -> profileFragment
            else -> Fragment()
        }
    }
}