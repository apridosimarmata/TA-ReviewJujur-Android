package id.sireto.reviewjujur.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.lifecycleScope
import id.sireto.reviewjujur.databinding.ActivityNewPasswordBinding
import id.sireto.reviewjujur.main.HomeActivity
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.models.UserPasswordRequest
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Auth
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujur.utils.SharedPref
import id.sireto.reviewjujur.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception

class NewPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewPasswordBinding
    private var response = BaseResponse()
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)

        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        passwordValidator()
        setupListeners()
        setContentView(binding.root)
    }

    private fun setupListeners(){
        binding.newPasswordSave.setOnClickListener{
            if(
                binding.newPasswordPassword.error.isNullOrEmpty() &&
                binding.newPasswordConfirmation.error.isNullOrEmpty()
            ){
                lifecycleScope.launch(Dispatchers.Main){
                    val updatePassword = lifecycleScope.async {
                        Auth.authUser(lifecycleScope, this@NewPasswordActivity){ auth ->
                            if(!auth){
                                startActivity(Intent(this@NewPasswordActivity, LoginActivity::class.java))
                                this@NewPasswordActivity.finish()
                            }
                        }

                        val token = Auth.getToken(this@NewPasswordActivity)
                        val refreshToken = Auth.getRefreshToken(this@NewPasswordActivity)

                        response = try {
                            apiService.updateUserPassword(token!!, refreshToken!!, UserPasswordRequest(
                                binding.newPasswordPassword.text.toString(),
                            )
                            ).body()!!
                        }catch (e : Exception){
                            BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                        }
                    }
                    updatePassword.await()

                    if (response.meta.code == 200){
                        SharedPref.saveToBooleanSharedPref(Constants.KEY_SET_PASSWORD_FINISHED, true)
                        response.meta.message?.let { UI.snackbarTop(binding.newPasswordPassword, it) }
                        delay(2000)
                        startActivity(Intent(this@NewPasswordActivity, HomeActivity::class.java))
                        this@NewPasswordActivity.finish()
                    }else{
                        UI.showSnackbarByResponseCode(response.meta, binding.newPasswordPassword)
                    }
                }
            }
        }
    }

    private fun passwordValidator(){
        binding.newPasswordPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().length < 8){
                    binding.newPasswordPassword.error = "Kata sandi terlalu pendek"
                }
            }

        })

        binding.newPasswordConfirmation.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString() != binding.newPasswordPassword.text.toString()){
                    binding.newPasswordConfirmation.error = "Kata sandi baru tidak sama"
                }
            }

        })
    }
}