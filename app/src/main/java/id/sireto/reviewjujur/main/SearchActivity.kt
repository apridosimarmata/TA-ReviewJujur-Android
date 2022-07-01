package id.sireto.reviewjujur.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.sireto.reviewjujur.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setupListeners()
        setContentView(binding.root)
    }

    private fun setupListeners(){
        binding.searchActivityBack.setOnClickListener{
            super.onBackPressed()
        }
    }
}