package id.sireto.reviewjujur.utils

import android.util.Log
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.models.*

object Converter {
    fun anyToAthenticationResponse(any : LinkedTreeMap<String, Any>) =
        AuthenticationResponse(any["token"].toString(), any["refresh_token"].toString())


    fun anyToProvinceResponse(any: LinkedTreeMap<String, Any>) =
        ProvinceResponse(any["uid"].toString(), any["name"].toString())

    fun anyToLocationResponse(any: LinkedTreeMap<String, Any>) =
        LocationResponse(any["uid"].toString(), any["province_uid"].toString() ,any["name"].toString())

    private fun anyToBusinessResponse(any: LinkedTreeMap<String, Any>) =
        BusinessResponse(
            any["uid"].toString(),
            any["owner_uid"].toString(),
            any["location_uid"].toString(),
            any["province_uid"].toString(),
            any["name"].toString(),
            any["address"].toString(),
            any["photo"].toString(),
            any["created_at"].toString(),
            any["modified_at"].toString(),
        )


    fun anyToBusinessPagination(any: LinkedTreeMap<String, Any>) : BusinessPagination {
        val result = BusinessPagination(any["limit"].toString().split(".")[0].toInt(), any["page"].toString().split(".")[0].toInt(), any["sort"].toString(), any["search"].toString(), any["location_uid"].toString(), arrayListOf())
        (any["rows"] as List<Any>).map {
            result.rows.add(anyToBusinessResponse(it as LinkedTreeMap<String, Any>))
        }

        return result
    }
}