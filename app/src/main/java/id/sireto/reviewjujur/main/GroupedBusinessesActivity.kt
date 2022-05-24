package id.sireto.reviewjujur.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.sireto.reviewjujur.databinding.ActivityGroupedBusinessesBinding

class GroupedBusinessesActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGroupedBusinessesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupedBusinessesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}