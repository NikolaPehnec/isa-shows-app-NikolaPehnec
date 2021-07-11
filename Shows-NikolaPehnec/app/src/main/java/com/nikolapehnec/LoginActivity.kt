package com.nikolapehnec

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.nikolapehnec.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var disabledButton: Boolean = true
    private val emailPattern = Regex("^[^\\s@]+@([^\\s@.,]+\\.)+[^\\s@.,]{2,}\$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoginButton()
        initListeners()

        /*if (intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME) != null && intent.type == "text/plain") {
            val intent2 = Intent()
            intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME)?.let { intent2.setClassName(this, it) }
            startActivity(intent2)
        }*/
    }

    private fun initLoginButton() {
        binding.loginButton.setOnClickListener {
            startActivity(WelcomeActivity.buildIntent(this, binding.editEmailInput.text.toString()))
        }

        /*binding.loginButton.setOnClickListener {
            val welcomeActivityIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_PACKAGE_NAME, "com.nikolapehnec.WelcomeActivity")
                type = "text/plain"
            }
            startActivity(welcomeActivityIntent)
        }*/
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getStringExtra(Intent.EXTRA_PACKAGE_NAME)
    }

    private fun initListeners() {
        binding.editEmailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (disabledButton && binding.editEmailInput.text.toString()
                        .matches(emailPattern) && binding.editPasswordInput.text.toString()
                        .trim().length > 5
                ) {
                    binding.loginButton.setBackgroundResource(R.drawable.ic_button_white)
                    binding.loginButton.setHintTextColor(resources.getColor(R.color.background))
                    binding.loginButton.isEnabled = true
                    disabledButton = false
                    binding.emailInput.error = null
                } else if (!disabledButton && !binding.editEmailInput.text.toString()
                        .matches(emailPattern)
                ) {
                    binding.loginButton.setBackgroundResource(R.drawable.ic_button_gray)
                    binding.loginButton.setHintTextColor(resources.getColor(R.color.white))
                    binding.loginButton.isEnabled = false
                    disabledButton = true
                }
                if (!binding.editEmailInput.text.toString().matches(emailPattern)) {
                    binding.emailInput.error = "Invalid email"
                } else {
                    binding.emailInput.error = null
                }
            }
        })

        binding.editPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (disabledButton && binding.editPasswordInput.text.toString()
                        .trim().length > 5 && binding.editEmailInput.text.toString()
                        .matches(emailPattern)
                ) {
                    binding.loginButton.setBackgroundResource(R.drawable.ic_button_white)
                    binding.loginButton.setHintTextColor(resources.getColor(R.color.background))
                    binding.loginButton.isEnabled = true
                    disabledButton = false
                } else if (binding.editPasswordInput.text.toString().trim().length < 6) {
                    binding.loginButton.setBackgroundResource(R.drawable.ic_button_gray)
                    binding.loginButton.setHintTextColor(resources.getColor(R.color.white))
                    binding.loginButton.isEnabled = false
                    disabledButton = true
                }
            }
        })
    }
}