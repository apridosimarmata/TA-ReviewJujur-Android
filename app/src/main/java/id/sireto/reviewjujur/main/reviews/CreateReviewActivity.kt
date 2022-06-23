package id.sireto.reviewjujur.main.reviews

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.R
import id.sireto.reviewjujur.databinding.ActivityCreateReviewBinding
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.BusinessResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Converter
import id.sireto.reviewjujur.utils.SharedPref
import id.sireto.reviewjujur.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception

class CreateReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateReviewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateReviewBinding.inflate(layoutInflater)
        SharedPref.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(binding.root)
        setupCreateReviewFragment()
    }

    private fun setupCreateReviewFragment(){
        supportFragmentManager.beginTransaction()
            .add(R.id.create_business_frameLayout, CreateReviewFragment())
            .commit()
    }

}