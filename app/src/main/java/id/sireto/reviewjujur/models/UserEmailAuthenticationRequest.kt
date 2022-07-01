package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class UserEmailAuthenticationRequest(
    @SerializedName("email")
    val email : String,

    @SerializedName("password")
    val password : String
)
