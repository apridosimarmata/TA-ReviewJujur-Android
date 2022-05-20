package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @SerializedName("uid")
    val uid : String,

    @SerializedName("province_uid")
    val provinceUid : String,

    @SerializedName("name")
    val name : String
)
