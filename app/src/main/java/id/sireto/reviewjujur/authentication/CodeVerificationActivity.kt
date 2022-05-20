package id.sireto.reviewjujur.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.sireto.reviewjujur.databinding.ActivityCodeVerificationBinding
import kotlin.properties.Delegates

class CodeVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCodeVerificationBinding
    private lateinit var whatsappNo: String
    private var type by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodeVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
        whatsappNo = intent.getStringExtra("whatsappNo")!!
        type = intent.getIntExtra("type", 0)
    }

    private fun setupListeners(){
        binding.verifyVerify.setOnClickListener {
            startActivity(Intent(this, NewPasswordActivity::class.java))
        }
    }
}