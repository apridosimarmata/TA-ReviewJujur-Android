package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class BusinessResponse(
    @SerializedName("uid")
    val uid : String,

    @SerializedName("owner_uid")
    val onwerUid : String,

    @SerializedName("location_uid")
    val locationUid : String,

    @SerializedName("province_uid")
    val provinceUid : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("address")
    val address : String,

    @SerializedName("photo")
    val photo: String,

    @SerializedName("created_at")
    val createdAt : String,

    @SerializedName("modified_at")
    val modifiedAt : String
) {
    constructor() : this("", "", "", "", "", "", "", "", "")
}
