package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class CodeVerificationRequest(
    @SerializedName("whatsappNo")
    val whatsappNo : String,

    @SerializedName("code")
    val code : String
)
