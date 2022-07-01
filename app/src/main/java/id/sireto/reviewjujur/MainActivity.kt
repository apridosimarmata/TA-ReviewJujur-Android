package id.sireto.reviewjujur

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import id.sireto.reviewjujur.authentication.LoginActivity
import id.sireto.reviewjujur.authentication.NewPasswordActivity
import id.sireto.reviewjujur.main.HomeActivity
import id.sireto.reviewjujur.utils.Auth
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujur.utils.SharedPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPref.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if(SharedPref.getStringFromSharedPref(Constants.KEY_TOKEN) != null){
            authUser()
        }else{
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            this@MainActivity.finish()
        }
    }

    private fun authUser(){
        lifecycleScope.launch(Dispatchers.Main){
            val auth = lifecycleScope.async {
                Auth.authUser(lifecycleScope, this@MainActivity){
                    if(it){
                        if(SharedPref.getBooleanFromSharedPref(Constants.KEY_SET_PASSWORD_FINISHED)){
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            this@MainActivity.finish()
                        }else{
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            this@MainActivity.finish()
                        }

                    }else{
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        this@MainActivity.finish()
                    }
                }
            }
            auth.await()
        }
    }
}