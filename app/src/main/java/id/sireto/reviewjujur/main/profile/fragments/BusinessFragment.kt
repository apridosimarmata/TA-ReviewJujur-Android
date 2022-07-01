package id.sireto.reviewjujur.main.profile.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.R
import id.sireto.reviewjujur.databinding.FragmentBusinessBinding
import id.sireto.reviewjujur.main.business.CreateBusinessActivity
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.BusinessResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Auth
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujur.utils.Converter
import id.sireto.reviewjujur.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BusinessFragment(private val apiService: ApiService) : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentBusinessBinding
    private var business = BusinessResponse()
    private var response = BaseResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        setupUserBusiness()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBusinessBinding.inflate(layoutInflater)
        setupUserBusiness()
        setupListeners()
        return binding.root
    }

    private fun setupListeners(){
        binding.businessFragmentCreateBusiness.setOnClickListener{
            requireActivity().startActivity(Intent(requireContext(), CreateBusinessActivity::class.java))
        }
    }

    private fun setupUserBusiness(){
        val token = Auth.getToken(requireContext())
        val refreshToken = Auth.getRefreshToken(requireContext())
        lifecycleScope.launch(Dispatchers.Main){
            val getUserBusiness = lifecycleScope.async {
                response = try {
                    apiService.getUserBusiness(token!!, refreshToken!!).body()!!
                }catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            getUserBusiness.await()

            if (response.meta.code == 200){
                binding.fragmentBusinessDetails.visibility = View.VISIBLE
                binding.businessFragmentNoBusiness.visibility = View.INVISIBLE
                business = Converter.anyToBusinessResponse(response.result as LinkedTreeMap<String, Any>)
                setupBusinessDetails()
            }else{
                binding.businessFragmentNoBusiness.visibility = View.VISIBLE
                binding.fragmentBusinessDetails.visibility = View.INVISIBLE
                UI.showSnackbarByResponseCode(response.meta, binding.fragmentBusinessDetails)
            }
        }
    }

    private fun setupBusinessDetails(){
        binding.fragmentBusinessDetailsName.text = business.name
        binding.fragmentBusinessDetailsAddress.text = business.address
        Glide.with(binding.fragmentBusinessDetailsPhoto)
            .load(Constants.CDN + business.photo + ".png")
            .centerCrop()
            .into(binding.fragmentBusinessDetailsPhoto)
    }

}