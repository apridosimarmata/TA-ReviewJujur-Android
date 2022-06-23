package id.sireto.reviewjujur.main.business

import android.content.AbstractThreadedSyncAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.databinding.ActivityBusinessDetailsBinding
import id.sireto.reviewjujur.main.scan.ScanActivity
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.BusinessResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.models.ReviewResponse
import id.sireto.reviewjujur.rv.adapters.BusinessReviewsCardAdapter
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujur.utils.Converter
import id.sireto.reviewjujur.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception

class BusinessDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBusinessDetailsBinding
    private lateinit var business: BusinessResponse
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit
    private var response = BaseResponse()
    private var reviews = arrayListOf<ReviewResponse>()
    private lateinit var reviewsRecyclerView : RecyclerView
    private lateinit var reviewsAdapter: BusinessReviewsCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
        business = intent.getSerializableExtra("business") as BusinessResponse
        setupBusinessDetails()
        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        setupReviewsRecyclerView()
        getBusinessReviews(null)
    }

    private fun getBusinessReviews(createdAt : Int?){
        lifecycleScope.launch(Dispatchers.Main){
            val getReviews = lifecycleScope.async {
                response = try {
                    if(createdAt != null){
                        apiService.getBusinessReviews(business.uid, createdAt).body()!!
                    }else {
                        apiService.getBusinessReviews(business.uid, null).body()!!
                    }
                }catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            getReviews.await()

            if (response.meta.code == 200){
                reviews.clear()
                (response.result as ArrayList<*>).map {
                    reviews.add(Converter.anyToReviewResponse(it as LinkedTreeMap<String, Any>))
                }
                if(reviews.size == 0){
                    UI.snackbarTop(binding.businessDetailsName, "Tidak ada review")
                }
                reviewsAdapter.reviews.addAll(reviews)
                reviewsAdapter.notifyDataSetChanged()
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.businessDetailsName)
            }
        }
    }

    private fun setupReviewsRecyclerView(){
        reviewsRecyclerView = binding.businessDetailsReviews
        reviewsAdapter = BusinessReviewsCardAdapter()
        reviewsRecyclerView.adapter = reviewsAdapter
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        reviewsRecyclerView.layoutManager = layoutManager
    }

    private fun setupBusinessDetails(){
        binding.businessDetailsNameHead.text = business.name
        binding.businessDetailsName.text = business.name
        binding.businessDetailsAddress.text = business.address
        with(binding.businessDetailsRating){
            if(business.reviewsCount > 0){
                this.text = "${business.reviewsCount/business.totalScore}"
            }else{
                this.text = "-"
            }
        }

        Glide.with(binding.businessDetailsPhoto)
            .load(business.photo)
            .centerCrop()
            .into(binding.businessDetailsPhoto)
    }

    private fun setupListeners(){
        binding.businessDetailsBack.setOnClickListener{
            super.onBackPressed()
        }

        binding.businessDetailsLoadMoreReviewBtn.setOnClickListener{
            reviewsAdapter.reviews.last().createdAt.let {
                getBusinessReviews(it)
            }
        }

        binding.businessDetailsScan.setOnClickListener{
            startActivity(Intent(this, ScanActivity::class.java))
        }
    }
}