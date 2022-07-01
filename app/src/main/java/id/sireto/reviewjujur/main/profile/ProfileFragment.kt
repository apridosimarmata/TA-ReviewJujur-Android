package id.sireto.reviewjujur.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import id.sireto.reviewjujur.R
import id.sireto.reviewjujur.databinding.FragmentProfileBinding
import id.sireto.reviewjujur.main.profile.fragments.BusinessFragment
import id.sireto.reviewjujur.main.profile.fragments.NameFragment
import id.sireto.reviewjujur.main.profile.fragments.PasswordFragment
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import retrofit2.Retrofit

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {

    companion object{
        val TAB_TITLES = arrayOf(
            "Account",
            "Business",
            "Password",
        )
    }

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentProfileBinding
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit

    private lateinit var nameFragment : NameFragment
    private lateinit var businessFragment : BusinessFragment
    private lateinit var passwordFragment : PasswordFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        nameFragment = NameFragment(apiService)
        businessFragment = BusinessFragment(apiService)
        passwordFragment = PasswordFragment(apiService)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        val tabLayout = binding.profileTabLayout
        val adapter = ProfileFragmentSectionAdapter(this, nameFragment, businessFragment, passwordFragment)
        binding.profilePager.adapter = adapter

        TabLayoutMediator(tabLayout, binding.profilePager){ tab, position ->
            tab.text = TAB_TITLES[position]
        }.attach()
        changeTabsFont()

        return binding.root
    }

    private fun changeTabsFont() {

        val typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_bold)

        val vg = binding.profileTabLayout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildsCount = vgTab.childCount
            for (i in 0 until tabChildsCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.typeface = typeface
                }
            }
        }
    }
}

class ProfileFragmentSectionAdapter(
    fragment: Fragment,
    private val nameFragment: NameFragment,
    private val businessFragment: BusinessFragment,
    private val passwordFragment: PasswordFragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> nameFragment
            1 -> businessFragment
            2 -> passwordFragment
            else -> Fragment()
        }
    }
}