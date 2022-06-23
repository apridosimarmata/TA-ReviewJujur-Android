package id.sireto.reviewjujur.models

import com.google.gson.annotations.SerializedName

data class PhoneFingerprint(
    @field:SerializedName("sd_card_capacity")
    val externalStorageCapacity : Int,

    @field:SerializedName("kernel_information")
    val kernelInformation : String,

    @field:SerializedName("wallpaper_info")
    val wallpaperInformation : String,

    @field:SerializedName("ringtone")
    val ringtone : String,

    @field:SerializedName("ringtone_list")
    val ringtoneList : List<String>,

    @field:SerializedName("input_methods")
    val inputMethods : List<String>,

    @field:SerializedName("screen_timeout")
    val screenTimeOut : Int,

    @field:SerializedName("password_is_shown")
    val passwordInputIsShown : String,

    @field:SerializedName("location_providers")
    val locationProviders : List<String>,

    @field:SerializedName("wifi_sleeping_policy")
    val wifiSleepingPolicy : String
)