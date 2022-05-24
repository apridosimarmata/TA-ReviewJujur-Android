package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class BusinessPagination(
    @SerializedName("limit")
    var limit : Int?,

    @SerializedName("page")
    var page : Int?,

    @SerializedName("sort")
    var sort : String?,

    @SerializedName("search")
    var search : String?,

    @SerializedName("location_uid")
    var locationUid : String,

    @SerializedName("rows")
    var rows : ArrayList<BusinessResponse>,

    @SerializedName("location")
    val location : String,

    @SerializedName("province")
    val province : String,
)
