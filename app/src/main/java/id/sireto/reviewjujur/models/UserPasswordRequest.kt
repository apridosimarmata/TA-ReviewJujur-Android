package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class UserPasswordRequest (
    @field:SerializedName("newPassword")
    var newPassword : String
    )