package id.sireto.reviewjujur.authentication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import id.sireto.reviewjujur.databinding.ActivityRegistrationBinding
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.models.UserRequest
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujur.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private  var response =  BaseResponse()
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupValidators()
        setupClickListeners()
    }

    private fun setupClickListeners(){
        binding.registrationRegister.setOnClickListener {
            with(binding){
                when {
                    registrationName.text.toString().isEmpty() -> registrationName.error = "Silakan isi nama"
                    registrationEmail.text.toString().isEmpty() -> registrationEmail.error = "Silakan isi email"
                    registrationWhatsappNumber.text.toString().isEmpty() -> registrationWhatsappNumber.error = "Silakan isi nomor WhatsApp"
                    registrationPassword.text.toString().isEmpty() -> registrationPassword.error = "Silakan isi kata sandi"
                    else -> registerUser()
                }
            }
        }
    }

    private fun registerUser(){
        val user = UserRequest(
            binding.registrationName.text.toString(),
            binding.registrationEmail.text.toString(),
            binding.registrationWhatsappNumber.text.toString(),
            binding.registrationPassword.text.toString()
        )

        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        lifecycleScope.launch(Dispatchers.Main){
            val progress = ProgressDialog(this@RegistrationActivity)
            progress.setMessage("Mendaftarkan akun...")
            progress.show()

            val register = lifecycleScope.async {
                response = try {
                    apiService.registerUser(user).body()!!
                } catch (e : Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            register.await()
            progress.dismiss()

            if (response.meta.code == 200){
                response.meta.message?.let { UI.snackbar(binding.registrationName, it) }
                val intent = Intent(this@RegistrationActivity, CodeVerificationActivity::class.java)
                intent.putExtra("whatsappNo", binding.registrationWhatsappNumber.text.toString())
                intent.putExtra("type", Constants.VERIFY_ACCOUNT)
                startActivity(intent)
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.registrationName)
            }

        }

    }

    private fun setupValidators(){
        nameValidator()
        passwordValidator()
        whatsappNoValidator()
        emailValidator()
    }

    private fun emailValidator(){
        binding.registrationEmail.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()){
                    binding.registrationEmail.error = "Email tidak valid"
                }
            }

        })
    }

    private fun whatsappNoValidator(){
        binding.registrationWhatsappNumber.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(p0.toString()){
                    if (this.length < 9){
                        binding.registrationWhatsappNumber.error = "Nomor terlalu pendek"
                    }else if (this.length > 14){
                        binding.registrationWhatsappNumber.error = "Nomor terlalu panjang"
                    }
                }
            }
        })
    }

    private fun passwordValidator(){
        binding.registrationPassword.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().length < 8){
                    binding.registrationPassword.error = "Kata sandi terlalu pendek"
                }
            }

        })
    }

    private fun nameValidator(){
        binding.registrationName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(p0.toString()){
                    if (this.length < 5){
                        binding.registrationName.error = "Nama terlalu pendek"
                    }else if (this.length > 30){
                        binding.registrationName.error = "Nama terlalu panjang"
                    }
                }
            }
        })
    }
}