package id.sireto.reviewjujur.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.sireto.reviewjujur.databinding.ActivityNewPasswordBinding

class NewPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}