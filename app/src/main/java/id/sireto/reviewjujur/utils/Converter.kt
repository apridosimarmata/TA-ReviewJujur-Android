package id.sireto.reviewjujur.utils

import android.util.Log
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.models.AuthenticationResponse

object Converter {
    fun anyToAthenticationResponse(any : LinkedTreeMap<String, Any>) : AuthenticationResponse{
        return AuthenticationResponse(any["token"].toString(), any["refresh_token"].toString())
    }
}