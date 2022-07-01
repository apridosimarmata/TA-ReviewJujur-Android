package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class BusinessRequest(
    @field:SerializedName("name")
    var name: String,

    @field:SerializedName("address")
    var address: String,

    @field:SerializedName("locationUid")
    var locationUid: String,

    @field:SerializedName("provinceUid")
    var provinceUid: String,

    @field:SerializedName("photo")
    var photo: String
) {
    constructor() : this("", "", "", "", "")
}
