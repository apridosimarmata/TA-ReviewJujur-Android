package id.sireto.reviewjujur

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import id.sireto.reviewjujur.authentication.LoginActivity
import id.sireto.reviewjujur.main.HomeActivity
import id.sireto.reviewjujur.utils.Auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(Auth.getToken(this) != null){
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
                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        this@MainActivity.finish()
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