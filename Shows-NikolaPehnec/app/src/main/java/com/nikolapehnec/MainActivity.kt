package com.nikolapehnec

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nikolapehnec.databinding.ActivityMainBinding
import com.nikolapehnec.networking.ApiModule

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        ApiModule.initRetrofit(applicationContext.getSharedPreferences("1", Context.MODE_PRIVATE))
    }
}