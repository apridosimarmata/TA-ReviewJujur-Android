package id.sireto.reviewjujur.main.profile.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.R
import id.sireto.reviewjujur.authentication.LoginActivity
import id.sireto.reviewjujur.databinding.FragmentBusinessBinding
import id.sireto.reviewjujur.databinding.FragmentPasswordBinding
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.models.UserPasswordRequest
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.Auth
import id.sireto.reviewjujur.utils.Converter
import id.sireto.reviewjujur.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PasswordFragment(private var apiService: ApiService) : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentPasswordBinding
    private var response = BaseResponse()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordBinding.inflate(layoutInflater)
        setupValidators()
        setupListeners()
        return binding.root
    }

    private fun setupListeners(){

        binding.fragmentPasswordSave.setOnClickListener{
            if(
                binding.fragmentPasswordNewPassword.error.isNullOrEmpty() &&
                        binding.fragmentPasswordNewPassword.error.isNullOrEmpty() &&
                        binding.fragmentPasswordNewPasswordConfirmation.error.isNullOrEmpty()
            ){
                lifecycleScope.launch(Dispatchers.Main){

                    Auth.authUser(lifecycleScope, requireContext()){ auth ->
                        if(!auth){
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            requireActivity().finish()
                        }
                    }

                    val token = Auth.getToken(requireContext())
                    val refreshToken = Auth.getRefreshToken(requireContext())

                    val updatePassword = lifecycleScope.async {
                        response = try {
                            apiService.updateUserPassword(token!!, refreshToken!!, UserPasswordRequest(
                                binding.fragmentPasswordNewPassword.text.toString(),
                            )).body()!!
                        }catch (e : Exception){
                            BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                        }
                    }
                    updatePassword.await()

                    if (response.meta.code == 200){
                        response.meta.message?.let { UI.snackbarTop(binding.fragmentPasswordNewPassword, it) }
                    }else{
                        UI.showSnackbarByResponseCode(response.meta, binding.fragmentPasswordNewPassword)
                    }
                }
            }
        }
    }

    private fun setupValidators(){
        passwordValidator()
    }

    private fun passwordValidator(){
        binding.fragmentPasswordNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().length < 8){
                    binding.fragmentPasswordNewPassword.error = "Kata sandi terlalu pendek"
                }
            }

        })

        binding.fragmentPasswordNewPasswordConfirmation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString() != binding.fragmentPasswordNewPassword.text.toString()){
                    binding.fragmentPasswordNewPasswordConfirmation.error = "Kata sandi baru tidak sama"
                }
            }

        })
    }
}