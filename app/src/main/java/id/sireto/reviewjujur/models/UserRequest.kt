package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("name")
    var name : String,

    @SerializedName("email")
    var email : String,

    @SerializedName("whatsappNo")
    var whatsappNo : String,

    @SerializedName("password")
    var password : String
)
