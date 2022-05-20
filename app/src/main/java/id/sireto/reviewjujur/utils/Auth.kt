package id.sireto.reviewjujur.utils

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.models.AuthenticationResponse
import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.Meta
import id.sireto.reviewjujur.services.api.ApiClient
import id.sireto.reviewjujur.services.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

object Auth {

    private var retrofit = ApiClient.getApiClient()
    private var apiService = retrofit.create(ApiService::class.java)
    private var response = BaseResponse()

    fun saveTokenDetails(context: Context, token : String, refreshToken : String){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        with(sharedPreferences.edit()){
            putString("token", token)
            putString("refresh_token", refreshToken)
                .commit()
        }
    }

    fun getToken(context : Context) : String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString("token", null)
    }

    fun getRefreshToken(context: Context) : String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString("refresh_token", null)
    }

    suspend fun authUser(lifecycleCoroutineScope: LifecycleCoroutineScope, context: Context, callback : (result : Boolean) -> Unit) {
        lifecycleCoroutineScope.launch(Dispatchers.IO){
            val auth = lifecycleCoroutineScope.async {
                response = try {
                    apiService.authorizeUser(getToken(context)!!, getRefreshToken(context)!!).body()!!
                }catch (e : Exception) {
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }
            auth.await()
            when(response.meta.code){
                200 -> callback(true)
                410 -> refreshUserToken(lifecycleCoroutineScope, context){
                    callback(it)
                }
                else -> callback(false)
            }
        }
    }

    suspend fun refreshUserToken(lifecycleCoroutineScope: LifecycleCoroutineScope, context: Context, callback : (result : Boolean) -> Unit){
        lifecycleCoroutineScope.launch(Dispatchers.IO){
            val refresh = lifecycleCoroutineScope.async {
                response = try {
                    apiService.refreshUserToken(getRefreshToken(context)!!).body()!!
                }catch (e : Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }
            refresh.await()
            when(response.meta.code){
                201 -> {
                    val authenticationResponse = Converter.anyToAthenticationResponse(response.result as LinkedTreeMap<String, Any>)
                    saveTokenDetails(context, authenticationResponse.token, authenticationResponse.refreshToken)
                    callback(true)
                }
                else -> callback(false)
            }
        }

    }
}