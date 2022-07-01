package id.sireto.reviewjujur.main.business

import android.R
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.authentication.CodeVerificationActivity
import id.sireto.reviewjujur.authentication.LoginActivity
import id.sireto.reviewjujur.databinding.ActivityCreateBusinessBinding
import id.sireto.reviewjujur.models.*
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Auth
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujur.utils.Converter
import id.sireto.reviewjujur.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.lang.Exception

class CreateBusinessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateBusinessBinding
    private val provinces = arrayListOf<ProvinceResponse>()
    private val locations = arrayListOf<LocationResponse>()
    private var response = BaseResponse()
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit
    private var imageUri : Uri? = null
    private var businessPhoto : String? = null

    private var selectedLocationUid : String? = null
    private var selectedProvinceUid: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBusinessBinding.inflate(layoutInflater)
        setupListeners()
        setupProvinces()
        setupValidators()
        setContentView(binding.root)
    }

    private fun setupValidators(){
        nameValidator()
        addressValidator()
    }

    private fun nameValidator(){
        binding.createBusinessName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(p0.toString()){
                    if (this.length < 5){
                        binding.createBusinessName.error = "Nama toko terlalu pendek"
                    }else if (this.length > 30){
                        binding.createBusinessName.error = "Nama toko terlalu panjang"
                    }
                }
            }

        })
    }

    private fun addressValidator(){
        binding.createBusinessAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(p0.toString()){
                    if (this.length < 10){
                        binding.createBusinessAddress.error = "Alamat terlalu pendek"
                    }else if (this.length > 50){
                        binding.createBusinessAddress.error = "Alamat terlalu panjang"
                    }
                }
            }

        })
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
                UI.showSnackbarByResponseCode(response.meta, binding.createBusinessProvinceSpinner)
            }

        }

    }

    private fun setupListeners(){
        binding.createBusinessBack.setOnClickListener{
            super.onBackPressed()
        }

        binding.createBusinessCreate.setOnClickListener{

            if(
                binding.createBusinessName.error.isNullOrEmpty() &&
                       binding.createBusinessAddress.error.isNullOrEmpty() &&
                        !businessPhoto.isNullOrEmpty()
            ){
                if(selectedProvinceUid.isNullOrEmpty()){
                    UI.snackbarTop(binding.createBusinessProvinceSpinner, "Pilih provinsi")
                }else{
                    if(selectedLocationUid.isNullOrEmpty()){
                        UI.snackbarTop(binding.createBusinessProvinceSpinner, "Pilih lokasi")
                    }else{
                        createBusiness()
                    }
                }
            }
        }

        binding.createBusinessPhoto.setOnClickListener{
            openGalleryForImage()
        }
    }

    private fun createBusiness(){

        lifecycleScope.launch(Dispatchers.Main){
            var progress = ProgressDialog(this@CreateBusinessActivity)
            progress.setMessage("Tunggu sebentar ...")
            progress.show()
            val createBusiness = lifecycleScope.async {

                Auth.authUser(lifecycleScope, this@CreateBusinessActivity){ auth ->
                    if(!auth){
                        startActivity(Intent(this@CreateBusinessActivity, LoginActivity::class.java))
                        this@CreateBusinessActivity.finish()
                    }
                }

                val token = Auth.getToken(this@CreateBusinessActivity)
                val refreshToken = Auth.getRefreshToken(this@CreateBusinessActivity)

                response = try {
                    apiService.createBusiness(
                        token!!,
                        refreshToken!!,
                        BusinessRequest(
                            binding.createBusinessName.text.toString(),
                            binding.createBusinessAddress.text.toString(),
                            selectedLocationUid!!,
                            selectedProvinceUid!!,
                            businessPhoto!!
                        )
                    ).body()!!
                }catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }
            createBusiness.await()

            if (response.meta.code == 200){
                response.meta.message?.let { UI.snackbar(binding.root, it) }
                onBackPressed()
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.createBusinessName)
            }
            progress.dismiss()
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            data?.data.let {
                Glide.with(binding.createBusinessPhoto)
                    .load(it)
                    .centerCrop().into(binding.createBusinessPhoto)
                imageUri = it

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream)
                val byteArray: ByteArray = outputStream.toByteArray()

                val encodedString: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                businessPhoto = encodedString
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

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, provinceNames)

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        binding.createBusinessProvinceSpinner.adapter = adapter

        binding.createBusinessProvinceSpinner.onItemSelectedListener =
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
                    selectedProvinceUid = provinceHashMap[provinceName]!!
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
                    UI.snackbar(binding.createBusinessProvinceSpinner, Constants.LOCATIONS_EMPTY)
                }

            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.createBusinessProvinceSpinner)
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

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, locationNames)

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        binding.createBusinessLocationSpinner.adapter = adapter

        binding.createBusinessLocationSpinner.onItemSelectedListener =
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