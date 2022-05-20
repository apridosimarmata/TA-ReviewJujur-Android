package id.sireto.reviewjujur.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPref {
    lateinit var sharedPreferences : SharedPreferences

    fun saveToSharedPref(context : Context, key : String, value : String){
        with(sharedPreferences.edit()){
            putString(key, value)
                .commit()
        }
    }

    fun getFromSharedPref(context : Context, key : String) : String?{
        return sharedPreferences.getString(key, null)
    }

}