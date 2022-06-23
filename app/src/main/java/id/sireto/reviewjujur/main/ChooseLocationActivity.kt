package id.sireto.reviewjujur.main

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.databinding.ActivityChooseLocationBinding
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.LocationResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.models.ProvinceResponse
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujur.utils.Converter
import id.sireto.reviewjujur.utils.SharedPref
import id.sireto.reviewjujur.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import retrofit2.Retrofit
import java.lang.Exception


class ChooseLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLocationBinding
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit
    private var response = BaseResponse()
    private val provinces = arrayListOf<ProvinceResponse>()
    private val locations = arrayListOf<LocationResponse>()

    private var selectedLocationUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        SharedPref.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(binding.root)
        setupListeners()
        setupProvinces()
    }

    private fun setupListeners(){
        binding.chooseLocationBack.setOnClickListener {
            super.onBackPressed()
        }
        binding.chooseLocationSave.setOnClickListener {
            selectedLocationUid?.let {
                SharedPref.saveToSharedPref(this, Constants.KEY_SELECTED_LOCATION, selectedLocationUid!!)
                super.onBackPressed()
            }
        }
    }

    private fun setupProvinces(){
        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        lifecycleScope.launch(Dispatchers.Main){
            val getProvinces = lifecycleScope.async {
                response = try {
                    apiService.getAllProvinces().body()!!
                }catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            getProvinces.await()

            if (response.meta.code == 200){
                (response.result as ArrayList<*>).map {
                    provinces.add(Converter.anyToProvinceResponse(it as LinkedTreeMap<String, Any>))
                }
                setupProvinceSpinner()
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.chooseLocationProvince)
            }

        }

    }

    private fun setupProvinceSpinner(){
        val provinceHashMap = HashMap<String, String>()

        provinces.map {
            provinceHashMap[it.name] = it.uid
        }

        val provinceNames = arrayListOf<String>()

        provinceHashMap.mapKeys {
            provinceNames.add(it.key)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provinceNames)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.chooseLocationProvince.adapter = adapter

        binding.chooseLocationProvince.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val provinceName = parent.selectedItem as String
                    locations.clear()
                    setupLocations(provinceHashMap[provinceName]!!)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setupLocations(provinceUid : String){

        lifecycleScope.launch(Dispatchers.Main){
            val getLocations = lifecycleScope.async {
                response = try {
                    apiService.getLocationsByProvinceUid(provinceUid).body()!!
                }catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            getLocations.await()
            if (response.meta.code == 200){
                (response.result as ArrayList<*>).map {
                    locations.add(Converter.anyToLocationResponse(it as LinkedTreeMap<String, Any>))
                }
                if(locations.size > 0){
                    setupLocationSpinner()
                }else{
                    UI.snackbar(binding.chooseLocationProvince, Constants.LOCATIONS_EMPTY)
                }

            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.chooseLocationProvince)
            }
        }
    }

    private fun setupLocationSpinner(){
        val locationHashMap = HashMap<String, String>()

        locations.map {
            locationHashMap[it.name] = it.uid
        }

        val locationNames = arrayListOf<String>()

        locationHashMap.mapKeys {
            locationNames.add(it.key)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locationNames)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.chooseLocationLocation.adapter = adapter

        binding.chooseLocationLocation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val locationName = parent.selectedItem as String

                    selectedLocationUid = try {
                        locationHashMap[locationName]!!
                    }catch (e : Exception) {
                        Constants.DEFAULT_LOCATION_UID
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

    }
}