package id.sireto.reviewjujur.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.databinding.ActivitySearchBinding
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.rv.adapters.BusinessCardAdapter
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Converter
import id.sireto.reviewjujur.utils.UI
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var query: Observable<String>
    private var page = 1
    private var response = BaseResponse()
    private lateinit var rvBusinesses : RecyclerView
    private lateinit var rvBusinessesAdapter: BusinessCardAdapter
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit
    private lateinit var locationUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        locationUid = intent.getStringExtra("locationUid").toString()
        query = createTextChangeObservable()
        retrofit = ApiClient.getApiClient()
        setupBusinessesRecyclerView()
        apiService = retrofit.create(ApiService::class.java)
        setupListeners()
        setContentView(binding.root)
    }

    private fun createTextChangeObservable(): Observable<String> {
        return Observable.create { emitter ->
            // 3
            val textWatcher = object : TextWatcher {

                override fun afterTextChanged(s: Editable?) = Unit

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                // 4
                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    s?.toString()?.let { emitter.onNext(it) }
                }
            }

            // 5
            binding.searchQuery.addTextChangedListener(textWatcher)

            // 6
            emitter.setCancellable {
                binding.searchQuery.removeTextChangedListener(textWatcher)
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun setupListeners(){
        binding.searchActivityBack.setOnClickListener{
            onBackPressed()
        }

        query
            .debounce(300, TimeUnit.MILLISECONDS)
            .subscribe {
                if(it.length > 2){
                    searchBusiness(it,true)
                    page = 1
                }
            }
    }

    private fun setupBusinessesRecyclerView(){
        rvBusinesses = binding.searchBusinessRecyclerView
        rvBusinessesAdapter = BusinessCardAdapter(this)
        rvBusinesses.adapter = rvBusinessesAdapter
        rvBusinesses.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun searchBusiness(query: String, clear: Boolean){

        if(clear){
            rvBusinessesAdapter.businesses.clear()
            lifecycleScope.launch(Dispatchers.Main){
                rvBusinessesAdapter.notifyDataSetChanged()
            }
        }

        lifecycleScope.launch(Dispatchers.Main){
            val search = lifecycleScope.async {
                response = try {
                    apiService.searchBusiness(null, page, locationUid, query, "total_score").body()!!
                } catch (e: Exception){
                    Log.d("GAGAGA", e.toString())
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            search.await()

            if (response.meta.code == 200){
                if(((response.result as LinkedTreeMap<*, *>)["rows"] as List<*>?)?.size == null){
                    UI.snackbarTop(binding.searchActivityBack, "Tidak ada data")
                }else{
                    ((response.result as LinkedTreeMap<*, *>)["rows"] as List<*>?)?.map {
                        rvBusinessesAdapter.businesses.add(Converter.anyToBusinessResponse(it as LinkedTreeMap<*, *>))
                        rvBusinessesAdapter.notifyDataSetChanged()
                    }
                    page += 1
                }
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.root)
            }
        }
    }

}