package com.nikolapehnec

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nikolapehnec.databinding.ActivityMainBinding
import com.nikolapehnec.networking.ApiModule
import com.nikolapehnec.viewModel.ShowDetailsViewModelFactory
import com.nikolapehnec.viewModel.ShowsDetailsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val detailViewModel: ShowsDetailsViewModel by viewModels {
        ShowDetailsViewModelFactory(
            (application as ShowsApp).showsDatabase!!,
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        ApiModule.initRetrofit(applicationContext.getSharedPreferences("1", Context.MODE_PRIVATE))
    }
}