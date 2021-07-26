package com.nikolapehnec

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nikolapehnec.networking.ApiModule
import com.nikolapehnec.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        ApiModule.initRetrofit(getPreferences(Context.MODE_PRIVATE))
    }
}