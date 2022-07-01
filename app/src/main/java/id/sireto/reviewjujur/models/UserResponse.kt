package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("name")
    var name : String,

    @SerializedName("email")
    var email : String,

    @SerializedName("whatsappNo")
    var whatsappNo : String
) {
    constructor() : this("","", "")
}
