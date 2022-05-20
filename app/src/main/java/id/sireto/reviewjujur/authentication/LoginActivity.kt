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
import id.sireto.reviewjujur.databinding.ActivityLoginBinding
import id.sireto.reviewjujur.main.HomeActivity
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.models.UserEmailAuthenticationPost
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Auth
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujur.utils.Converter
import id.sireto.reviewjujur.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private  var response =  BaseResponse()
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
        setupValidators()
    }

    private fun setupListeners(){
        binding.loginCreateAccount.setOnClickListener{
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        binding.loginForgotPassword.setOnClickListener{
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.loginLogin.setOnClickListener {
            with(binding){
                when{
                    loginEmail.text.toString().isEmpty() -> loginEmail.error = "Silakan isi email"
                    loginPassword.text.toString().isEmpty() -> loginPassword.error = "Silakan isi kata sandi"
                    else -> loginUser()
                }
            }
        }
    }

    private fun loginUser(){
        val user = UserEmailAuthenticationPost(
            binding.loginEmail.text.toString(),
            binding.loginPassword.text.toString()
        )

        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        lifecycleScope.launch(Dispatchers.Main){
            val progress = ProgressDialog(this@LoginActivity)
            progress.setMessage("Masuk ...")
            progress.show()

            val login = lifecycleScope.async {
                response = try {
                    apiService.authenticateUserByEmail(user).body()!!
                }catch (e : Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            login.await()
            progress.dismiss()

            if (response.meta.code == 200){
                response.meta.message?.let { UI.snackbar(binding.loginEmail, it) }
                var authenticationResponse = Converter.anyToAthenticationResponse(response.result as LinkedTreeMap<String, Any>)
                Auth.saveTokenDetails(this@LoginActivity, authenticationResponse.token, authenticationResponse.refreshToken)
                this@LoginActivity.finish()
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.loginEmail)
            }

        }
    }

    private fun setupValidators(){
        passwordValidator()
        emailValidator()
    }

    private fun emailValidator(){
        binding.loginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()){
                    binding.loginEmail.error = "Email tidak valid"
                }
            }

        })
    }

    private fun passwordValidator(){
        binding.loginPassword.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().length < 8){
                    binding.loginPassword.error = "Kata sandi terlalu pendek"
                }
            }

        })
    }
}