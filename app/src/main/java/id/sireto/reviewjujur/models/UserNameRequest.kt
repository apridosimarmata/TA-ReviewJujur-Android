package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class UserNameRequest (
    @field:SerializedName("name")
    var name: String,
)