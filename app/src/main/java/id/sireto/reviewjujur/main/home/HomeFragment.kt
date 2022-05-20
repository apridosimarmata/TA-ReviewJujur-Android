package id.sireto.reviewjujur.main.home

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.sireto.reviewjujur.R
import id.sireto.reviewjujur.databinding.FragmentHomeBinding
import id.sireto.reviewjujur.main.ChooseLocationActivity
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujur.utils.SharedPref
import id.sireto.reviewjujur.utils.UI

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding

    private var previousSelectedLocationUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        previousSelectedLocationUid = SharedPref.getFromSharedPref(requireActivity().applicationContext, Constants.KEY_SELECTED_LOCATION)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setupListeners()
        return binding.root
    }

    private fun setupListeners(){
        binding.homeChooseLocationBtn.setOnClickListener{
            startActivity(Intent(requireActivity(), ChooseLocationActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        with(SharedPref.getFromSharedPref(requireContext(), Constants.KEY_SELECTED_LOCATION)){

            if (this != null){
                binding.homeLocationNotChoosen.visibility = View.INVISIBLE
                showDetails()
                if (this != previousSelectedLocationUid){
                    setupBusinesses(this)
                    previousSelectedLocationUid = this
                }
            }else{
                binding.homeLocationNotChoosen.visibility = View.VISIBLE
                hideDetails()
            }
        }
    }

    private fun setupBusinesses(locationUid: String){
        UI.snackbarTop(binding.homeChooseLocationBtn, "Katanya beda")
        setupMostReviewBusinesses()
    }

    private fun setupMostReviewBusinesses(){

    }

    private fun hideDetails(){
        val invisible = View.INVISIBLE
        with(binding){
            homeLocationName.visibility = invisible
            homeProvinceName.visibility = invisible
            homeBestScore.visibility = invisible
            homeMostReview.visibility = invisible
            homeReviewed.visibility = invisible
            rvBestScore.visibility = invisible
            rvMostReview.visibility = invisible
            rvReviewed.visibility = invisible
        }
    }

    private fun showDetails(){
        val visible = View.VISIBLE
        with(binding){
            homeLocationName.visibility = visible
            homeProvinceName.visibility = visible
            homeBestScore.visibility = visible
            homeMostReview.visibility = visible
            homeReviewed.visibility = visible
            rvBestScore.visibility = visible
            rvMostReview.visibility = visible
            rvReviewed.visibility = visible
        }
    }
}