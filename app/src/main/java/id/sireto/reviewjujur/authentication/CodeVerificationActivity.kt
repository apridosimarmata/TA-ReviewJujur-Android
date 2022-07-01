package id.sireto.reviewjujur.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.databinding.ActivityCodeVerificationBinding
import id.sireto.reviewjujur.main.HomeActivity
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.CodeVerificationRequest
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception
import kotlin.properties.Delegates

class CodeVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCodeVerificationBinding
    private lateinit var whatsappNo: String
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit
    private var response = BaseResponse()
    private var type by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodeVerificationBinding.inflate(layoutInflater)

        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        setContentView(binding.root)

        setupListeners()

        whatsappNo = intent.getStringExtra("whatsappNo")!!
        type = intent.getIntExtra("type", 0)
    }

    private fun setupListeners(){
        binding.verifyVerify.setOnClickListener {
            verifyCode()
        }
    }

    private fun verifyCode() {

        val verifyCodeRequest = CodeVerificationRequest(
            whatsappNo, binding.codeVerificationCode.text.toString()
        )

        lifecycleScope.launch(Dispatchers.Main){
            val verifyCode = lifecycleScope.async {
                response = try {
                    if(type == Constants.FORGOT_PASSWORD){
                        apiService.authenticateUserWhatsApp(verifyCodeRequest, null).body()!!
                    }else{
                        apiService.authenticateUserWhatsApp(verifyCodeRequest, 1).body()!!
                    }
                }catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            verifyCode.await()

            if (response.meta.code == 200){
                if(type == Constants.FORGOT_PASSWORD){
                    response.meta.message?.let { UI.snackbar(binding.codeVerificationCode, it) }
                    val authenticationResponse = Converter.anyToAthenticationResponse(response.result as LinkedTreeMap<String, Any>)
                    Auth.saveTokenDetails(this@CodeVerificationActivity, authenticationResponse.token, authenticationResponse.refreshToken)
                    startActivity(Intent(this@CodeVerificationActivity, NewPasswordActivity::class.java))
                    SharedPref.saveToBooleanSharedPref(Constants.KEY_SET_PASSWORD_FINISHED, false)
                    this@CodeVerificationActivity.finish()
                }else{
                    response.meta.message?.let { UI.snackbar(binding.codeVerificationCode, it) }
                    val authenticationResponse = Converter.anyToAthenticationResponse(response.result as LinkedTreeMap<String, Any>)
                    Auth.saveTokenDetails(this@CodeVerificationActivity, authenticationResponse.token, authenticationResponse.refreshToken)
                    startActivity(Intent(this@CodeVerificationActivity, HomeActivity::class.java))
                    this@CodeVerificationActivity.finish()
                }
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.codeVerificationCode)
            }
        }
    }
}