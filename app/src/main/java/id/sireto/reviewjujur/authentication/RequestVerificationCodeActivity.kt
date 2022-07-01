package id.sireto.reviewjujur.authentication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import id.sireto.reviewjujur.databinding.ActivityRequestVerificationCodeBinding
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujur.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception
import kotlin.properties.Delegates

class RequestVerificationCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestVerificationCodeBinding
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit
    private var response = BaseResponse()
    private var type by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = intent.getIntExtra("type", 0)

        binding = ActivityRequestVerificationCodeBinding.inflate(layoutInflater)

        if(type==Constants.FORGOT_PASSWORD){
            binding.requestCodeTitle.text = Constants.TITLE_FORGOT_PASSWORD
        }else{
            binding.requestCodeTitle.text = Constants.TITLE_VERIFY_ACCOUNT
        }

        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        setContentView(binding.root)
        setupListeners()
        setupValidators()
    }

    private fun setupValidators(){
        binding.requestVerificationCodePhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(p0.toString()){
                    if (this.length < 9){
                        binding.requestVerificationCodePhone.error = "Nomor terlalu pendek"
                    }else if (this.length > 14){
                        binding.requestVerificationCodePhone.error = "Nomor terlalu panjang"
                    }
                }
            }

        })
    }

    private fun setupListeners(){
        binding.requestVerificationSendCode.setOnClickListener {
            with(binding){
                if (requestVerificationCodePhone.text.toString().isEmpty()) {
                    binding.requestVerificationCodePhone.error = "Silakan isi nomor WhatsApp"
                } else {
                    sendVerificationCode()
                }
            }

        }
    }

    private fun sendVerificationCode(){
        lifecycleScope.launch(Dispatchers.Main){
            val progress = ProgressDialog(this@RequestVerificationCodeActivity)
            progress.setMessage("Mengirim kode ...")
            progress.show()
            val sendCode = lifecycleScope.async {
                response = try {
                    apiService.requestUserVerificationCode(binding.requestVerificationCodePhone.text.toString()).body()!!
                }catch (e : Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }
            sendCode.await()
            progress.dismiss()

            if (response.meta.code == 200){
                val intent = Intent(this@RequestVerificationCodeActivity, CodeVerificationActivity::class.java)
                intent.putExtra("whatsappNo", binding.requestVerificationCodePhone.text.toString())
                intent.putExtra("type", type)
                startActivity(intent)
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.requestVerificationCodePhone)
            }
        }
    }
}