package com.nikolapehnec

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nikolapehnec.databinding.ActivityMainBinding
import com.nikolapehnec.networking.ApiModule
import com.nikolapehnec.viewModel.ShowDetailsViewModelFactory
import com.nikolapehnec.viewModel.ShowsDetailsSharedViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        ApiModule.initRetrofit(getPreferences(Context.MODE_PRIVATE))

        val isTablet = resources?.getBoolean(R.bool.isTablet)
        if (isTablet == true) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
    }
}