package com.nikolapehnec

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat
import androidx.core.widget.doAfterTextChanged
import com.nikolapehnec.databinding.ActivityLoginBinding
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var disabledButton: Boolean = true
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoginButton()
        initListeners()

        /*if (intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME) != null && intent.type == "text/plain") {
            val intent2 = Intent()
            intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME)?.let {
            intent2.setClassName(this, it) }
            startActivity(intent2)
        }*/
    }

    private fun initLoginButton() {
        binding.loginButton.setOnClickListener {
            val sharedPref = applicationContext.getSharedPreferences("1",Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString(
                    getString(R.string.username),
                    binding.editEmailInput.text.toString().split("@")[0]
                )
                apply()
            }

            val intent = Intent(this, ShowsActivity::class.java)
            startActivity(intent)
        }

        //data ili action
        /*binding.loginButton.setOnClickListener {
            val welcomeActivityIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_PACKAGE_NAME, "com.nikolapehnec.WelcomeActivity")
                type = "text/plain"
            }
            startActivity(welcomeActivityIntent)
        }*/
    }

    private fun initListeners() {
        binding.editEmailInput.doAfterTextChanged {
            if (disabledButton && emailPattern.matcher(binding.editEmailInput.text.toString())
                    .matches()
                && binding.editPasswordInput.text.toString()
                    .trim().length > 5
            ) {
                binding.loginButton.setBackgroundResource(R.drawable.ic_button_white)
                binding.loginButton.setTextColor(ContextCompat.getColor(this, R.color.background))
                binding.loginButton.isEnabled = true
                disabledButton = false
                binding.emailInput.error = null
            } else if (!disabledButton && !emailPattern.matcher(binding.editEmailInput.text.toString())
                    .matches()
            ) {
                binding.loginButton.setBackgroundResource(R.drawable.ic_button_gray)
                binding.loginButton.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.loginButton.isEnabled = false
                disabledButton = true
            }
            if (!emailPattern.matcher(binding.editEmailInput.text.toString()).matches()) {
                binding.emailInput.error = "Invalid email"
            } else {
                binding.emailInput.error = null
            }
        }


        binding.editPasswordInput.doAfterTextChanged {
            if (disabledButton && binding.editPasswordInput.text.toString()
                    .trim().length > 5 && emailPattern.matcher(binding.editEmailInput.text.toString())
                    .matches()
            ) {
                binding.loginButton.setBackgroundResource(R.drawable.ic_button_white)
                binding.loginButton.setTextColor(ContextCompat.getColor(this, R.color.background))
                binding.loginButton.isEnabled = true
                disabledButton = false
            } else if (binding.editPasswordInput.text.toString().trim().length < 6) {
                binding.loginButton.setBackgroundResource(R.drawable.ic_button_gray)
                binding.loginButton.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.loginButton.isEnabled = false
                disabledButton = true
            }
        }

    }
}