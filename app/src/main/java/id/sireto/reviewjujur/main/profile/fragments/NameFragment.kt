package id.sireto.reviewjujur.main.profile.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.authentication.LoginActivity
import id.sireto.reviewjujur.databinding.FragmentNameBinding
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.models.UserNameRequest
import id.sireto.reviewjujur.models.UserResponse
import id.sireto.reviewjujur.services.api.ApiService
import id.sireto.reviewjujur.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NameFragment(private val apiService: ApiService) : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentNameBinding
    private var response = BaseResponse()
    private var userResponse = UserResponse()

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
    ): View {
        binding = FragmentNameBinding.inflate(layoutInflater)
        setupUserDetails()
        setupListeners()
        return binding.root
    }

    private fun setupUserDetails(){
        val token = Auth.getToken(requireContext())
        val refreshToken = Auth.getRefreshToken(requireContext())
        lifecycleScope.launch(Dispatchers.Main){
            val getUserDetails = lifecycleScope.async {
                response = try {
                    apiService.getUserDetails(token!!, refreshToken!!).body()!!
                }catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            getUserDetails.await()

            if (response.meta.code == 200){
                userResponse = Converter.anyToUserResponse(response.result as LinkedTreeMap<String, Any>)
                showUserDetails()
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.fragmentNameName)
            }
        }
    }

    private fun showUserDetails(){
        binding.fragmentNameName.setText(userResponse.name)

        SharedPref.getStringFromSharedPref(Constants.KEY_NAME)?.let {
            binding.fragmentNameName.setText(it)
        }

        binding.fragmentNameEmail.text = userResponse.email
        binding.fragmentNameWhatsappNo.text = userResponse.whatsappNo

    }

    private fun setupListeners(){
        binding.fragmentNameSave.setOnClickListener{
            if(binding.fragmentNameName.error.isNullOrEmpty()){
                updateUserName()
            }
        }

        binding.fragmentNameName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(p0.toString()){
                    if (this != userResponse.name && this != SharedPref.getStringFromSharedPref(
                            Constants.KEY_NAME
                        )){
                        binding.fragmentNameSave.visibility = View.VISIBLE
                        if (this.length < 5){
                            binding.fragmentNameName.error = "Nama terlalu pendek"
                        }else if (this.length > 30){
                            binding.fragmentNameName.error = "Nama terlalu panjang"
                        }
                    }else{
                        binding.fragmentNameSave.visibility = View.INVISIBLE
                    }
                }
            }

        })

        binding.fragmentNameLogout.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Yakin ingin keluar?")
                .setCancelable(false)
                .setPositiveButton("Ya") { _, _ ->
                    SharedPref.removeAccessTokens()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun updateUserName(){
        lifecycleScope.launch(Dispatchers.Main){
            val updateUserName = lifecycleScope.async {

                Auth.authUser(lifecycleScope, requireContext()){ auth ->
                    if(!auth){
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        requireActivity().finish()
                    }
                }

                val token = Auth.getToken(requireContext())
                val refreshToken = Auth.getRefreshToken(requireContext())

                response = try {
                    apiService.updateUserName(token!!, refreshToken!!, UserNameRequest(binding.fragmentNameName.text.toString())).body()!!
                }catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            updateUserName.await()

            if (response.meta.code == 200){
                response.meta.message?.let { UI.snackbarTop(binding.fragmentNameName, it) }
                SharedPref.saveToStringSharedPref(
                    Constants.KEY_NAME,
                    binding.fragmentNameName.text.toString()
                )
                binding.fragmentNameSave.visibility = View.INVISIBLE
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.fragmentNameName)
            }
        }
    }
}