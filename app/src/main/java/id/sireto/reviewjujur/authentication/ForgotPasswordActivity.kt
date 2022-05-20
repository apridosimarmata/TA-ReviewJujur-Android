package id.sireto.reviewjujur.authentication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.databinding.ActivityForgotPasswordBinding
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.Meta
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

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit
    private var response = BaseResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)

        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        setContentView(binding.root)
        setupListeners()
        setupValidators()
    }

    private fun setupValidators(){
        binding.forgotWhatsappNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(p0.toString()){
                    if (this.length < 9){
                        binding.forgotWhatsappNo.error = "Nomor terlalu pendek"
                    }else if (this.length > 14){
                        binding.forgotWhatsappNo.error = "Nomor terlalu panjang"
                    }
                }
            }

        })
    }

    private fun setupListeners(){
        binding.forgotSendCode.setOnClickListener {
            with(binding){
                if (forgotWhatsappNo.text.toString().isEmpty()) {
                    binding.forgotWhatsappNo.error = "Silakan isi nomor WhatsApp"
                } else {
                    sendVerificationCode()
                }
            }

        }
    }

    private fun sendVerificationCode(){
        lifecycleScope.launch(Dispatchers.Main){
            val progress = ProgressDialog(this@ForgotPasswordActivity)
            progress.setMessage("Mengirim kode ...")
            progress.show()
            val sendCode = lifecycleScope.async {
                response = try {
                    apiService.requestUserVerificationCode(binding.forgotWhatsappNo.text.toString()).body()!!
                }catch (e : Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }
            sendCode.await()
            progress.dismiss()

            if (response.meta.code == 200){
                startActivity(Intent(this@ForgotPasswordActivity, CodeVerificationActivity::class.java).putExtra("whatsappNo", binding.forgotWhatsappNo.text.toString()))
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.forgotWhatsappNo)
            }
        }
    }
}