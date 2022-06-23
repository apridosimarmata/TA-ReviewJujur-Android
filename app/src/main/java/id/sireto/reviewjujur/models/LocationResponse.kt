package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @SerializedName("uid")
    val uid : String,

    @SerializedName("provinceUid")
    val provinceUid : String,

    @SerializedName("name")
    val name : String
)
