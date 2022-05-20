package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class ProvinceResponse(
    @SerializedName("uid")
    val uid : String,

    @SerializedName("name")
    val name : String
)
