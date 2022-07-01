package id.sireto.reviewjujur.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class ReviewRequest(
    @SerializedName("text")
    val text : String,

    @SerializedName("score")
    val score : Int,

    @SerializedName("businessUid")
    val businessUid : String,

    @SerializedName("fingerprint")
    val fingerprint: PhoneFingerprint
)
