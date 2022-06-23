package id.sireto.reviewjujur.main.reviews

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.R
import id.sireto.reviewjujur.authentication.LoginActivity
import id.sireto.reviewjujur.databinding.FragmentCreateReviewBinding
import id.sireto.reviewjujur.models.*
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Auth
import id.sireto.reviewjujur.utils.Converter
import id.sireto.reviewjujur.utils.Fingerprint
import id.sireto.reviewjujur.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CreateReviewFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentCreateReviewBinding
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit
    private var response = BaseResponse()
    private var business = BusinessResponse()
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateReviewBinding.inflate(layoutInflater)
        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        val businessUid: String? = requireActivity().intent?.dataString?.split("/")?.last()

        if(!businessUid.isNullOrEmpty()){
            setupBusinessDetails(businessUid)
        }
        hideScoreDetails()
        setupListeners()
        return binding.root
    }


    private fun hideScoreDetails(){
        with(binding){
            createReviewScoreConnector1.visibility = View.GONE
            createReviewScoreConnector2.visibility = View.GONE
            createReviewScoreConnector3.visibility = View.GONE
            createReviewScoreConnector4.visibility = View.GONE
        }
    }

    private fun setupBusinessDetails(businessUid : String){
        UI.snackbarTop(binding.imageView15, Fingerprint.getAndroidID(requireActivity().contentResolver))
        lifecycleScope.launch(Dispatchers.Main){
            val getBusinessDetails = lifecycleScope.async {
                response = try {
                    apiService.getBusinessByUid(businessUid).body()!!
                }catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }
            getBusinessDetails.await()

            if (response.meta.code == 200){
                business = Converter.anyToBusinessResponse(response.result as LinkedTreeMap<String, Any>)
                showBusinessDetails()
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.createReviewBusinessAddress)
            }
        }
    }

    private fun showBusinessDetails(){
        with(binding){
            createReviewBusinessAddress.text = business.address
            createReviewBusinessName.text = business.name
            createReviewBusinessRating.text = if(business.reviewsCount > 0 ){
                "${business.totalScore/business.reviewsCount}"
            }else{
                "-"
            }
            Glide.with(createReviewBusinessPhoto)
                .load(business.photo)
                .centerCrop()
                .into(createReviewBusinessPhoto)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createReview(){
        val activity = requireActivity()
        val contentResolver = activity.contentResolver
        val context = requireContext()

        val phoneFingerprint = PhoneFingerprint(
            Fingerprint.getExternalStorageCapacity(context),
            Fingerprint.getKernelInformation(),
            Fingerprint.getCurrentWallpaper(context),
            Fingerprint.getRingtone(activity),
            Fingerprint.getRingtoneList(activity),
            Fingerprint.getInputMethods(activity),
            Fingerprint.getScreenTimeout(contentResolver),
            Fingerprint.getPasswordInputIsShown(contentResolver),
            Fingerprint.getLocationProviders(activity),
            Fingerprint.getWifiSleepingPolicy(contentResolver)
        )
        val review =  ReviewPost(
            binding.createReviewText.text.toString(),
            score,
            business.uid,
            phoneFingerprint
        )

        lifecycleScope.launch(Dispatchers.Main){
            val progress = ProgressDialog(activity)
            progress.setMessage("Masuk ...")
            progress.show()
            val wait = lifecycleScope.async {
                this@CreateReviewFragment.context?.let { context ->
                    Auth.getToken(context)?.let { token ->
                        Auth.getRefreshToken(context)?.let { refreshToken ->
                            response = try {
                                apiService.createReview(token, refreshToken, review).body()!!
                            }catch (e : Exception) {
                                BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                            }
                        }
                    }
                }
            }
            wait.await()

            if (response.meta.code == 200){
                val transaction = (requireActivity() as CreateReviewActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.create_business_frameLayout, ReviewCreatedFragment()).commit()
            }else{
                if (response.meta.code == 410) {
                    Auth.refreshUserToken(lifecycleScope, requireContext()){ refreshed ->
                        if (refreshed) createReview()
                        else requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
                    }
                }else{
                    UI.showSnackbarByResponseCode(response.meta, binding.createReviewText)
                }
            }
            progress.dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupListeners(){
        binding.createReviewSubmit.setOnClickListener{
            createReview()
        }
        binding.createReviewScore1.setOnClickListener {
            if (binding.createReviewScore1.isChecked) {
                score = 1
                with(binding){
                    createReviewScoreConnector1.visibility = View.GONE
                    createReviewScoreConnector2.visibility = View.GONE
                    createReviewScoreConnector3.visibility = View.GONE
                    createReviewScoreConnector4.visibility = View.GONE
                    createReviewScore2.isChecked = false
                    createReviewScore3.isChecked = false
                    createReviewScore4.isChecked = false
                    createReviewScore5.isChecked = false
                }
            }
        }
        binding.createReviewScore2.setOnClickListener {
            if (binding.createReviewScore2.isChecked) {
                score = 2
                with(binding){
                    createReviewScoreConnector1.visibility = View.VISIBLE
                    createReviewScoreConnector2.visibility = View.GONE
                    createReviewScoreConnector3.visibility = View.GONE
                    createReviewScoreConnector4.visibility = View.GONE
                    createReviewScore1.isChecked = true
                    createReviewScore3.isChecked = false
                    createReviewScore4.isChecked = false
                    createReviewScore5.isChecked = false
                }
            }
        }
        binding.createReviewScore3.setOnClickListener {
            if (binding.createReviewScore3.isChecked) {
                score = 3
                with(binding){
                    createReviewScoreConnector1.visibility = View.VISIBLE
                    createReviewScoreConnector2.visibility = View.VISIBLE
                    createReviewScoreConnector3.visibility = View.GONE
                    createReviewScoreConnector4.visibility = View.GONE
                    createReviewScore1.isChecked = true
                    createReviewScore2.isChecked = true
                    createReviewScore4.isChecked = false
                    createReviewScore5.isChecked = false
                }
            }
        }
        binding.createReviewScore4.setOnClickListener {
            if (binding.createReviewScore4.isChecked) {
                score = 4
                with(binding){
                    createReviewScoreConnector1.visibility = View.VISIBLE
                    createReviewScoreConnector2.visibility = View.VISIBLE
                    createReviewScoreConnector3.visibility = View.VISIBLE
                    createReviewScoreConnector4.visibility = View.GONE
                    createReviewScore1.isChecked = true
                    createReviewScore2.isChecked = true
                    createReviewScore3.isChecked = true
                    createReviewScore5.isChecked = false
                }
            }
        }
        binding.createReviewScore5.setOnClickListener {
            if (binding.createReviewScore5.isChecked) {
                score = 5
                with(binding){
                    createReviewScoreConnector1.visibility = View.VISIBLE
                    createReviewScoreConnector2.visibility = View.VISIBLE
                    createReviewScoreConnector3.visibility = View.VISIBLE
                    createReviewScoreConnector4.visibility = View.VISIBLE
                    createReviewScore1.isChecked = true
                    createReviewScore2.isChecked = true
                    createReviewScore3.isChecked = true
                    createReviewScore4.isChecked = true
                    createReviewScore5.isChecked = true
                }
            }
        }
    }

}