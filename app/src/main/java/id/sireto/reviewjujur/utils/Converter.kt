package id.sireto.reviewjujur.utils

import android.util.Log
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.models.AuthenticationResponse
import id.sireto.reviewjujur.models.LocationResponse
import id.sireto.reviewjujur.models.ProvinceResponse

object Converter {
    fun anyToAthenticationResponse(any : LinkedTreeMap<String, Any>) =
        AuthenticationResponse(any["token"].toString(), any["refresh_token"].toString())


    fun anyToProvinceResponse(any: LinkedTreeMap<String, Any>) =
        ProvinceResponse(any["uid"].toString(), any["name"].toString())

    fun anyToLocationResponse(any: LinkedTreeMap<String, Any>) =
        LocationResponse(any["uid"].toString(), any["province_uid"].toString() ,any["name"].toString())
}