package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class AuthenticationResponse (
    @SerializedName("token")
    val token : String,

    @SerializedName("refresh_token")
    val refreshToken : String
) {
    constructor() : this("", "")
}