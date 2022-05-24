package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class BusinessResponse(
    @SerializedName("uid")
    val uid : String,

    @SerializedName("reviews_count")
    val reviewsCount : Int,

    @SerializedName("total_score")
    val totalScore : Int,

    @SerializedName("owner_uid")
    val onwerUid : String,

    @SerializedName("location_uid")
    val locationUid : String,

    @SerializedName("province_uid")
    val provinceUid : String,

    @SerializedName("location")
    var location : String,

    @SerializedName("province")
    var province : String,

    @SerializedName("name")
    var name : String,

    @SerializedName("address")
    var address : String,

    @SerializedName("photo")
    val photo: String,

    @SerializedName("created_at")
    val createdAt : String,

    @SerializedName("modified_at")
    val modifiedAt : String
) {
    constructor() : this("", 0, 0,"", "", "", "", "", "", "", "", "", "")
}
