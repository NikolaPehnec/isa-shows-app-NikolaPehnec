package com.nikolapehnec

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nikolapehnec.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    companion object {
        private const val EXTRA_USERNAME = "EXTRA_USERNAME"

        fun buildIntent(activity: Activity, username: String): Intent {
            val intent = Intent(activity, WelcomeActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, username)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val username = intent.extras?.getString(EXTRA_USERNAME)?.split("@")
        if (username != null) {
            binding.welcomeMessage.text = String.format(
                getString(R.string.welcome_mess), username[0]
            )
        }
    }
}