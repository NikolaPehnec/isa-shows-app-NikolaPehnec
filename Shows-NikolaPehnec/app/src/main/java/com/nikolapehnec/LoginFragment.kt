package com.nikolapehnec

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nikolapehnec.databinding.ActivityLoginBinding
import com.nikolapehnec.viewModel.LoginViewModel
import java.util.regex.Pattern

class LoginFragment : Fragment() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private var disabledButton: Boolean = true
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
    private var sharedPref: SharedPreferences? = null

    private val viewModel: LoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityLoginBinding.inflate(inflater, container, false)
        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disabledButton = true
        initListeners()
        initLoginListeners()
        checkIfSignedIn()
        checkIfRegisterSuccessful()

        viewModel.getloginResultLiveData().observe(this.viewLifecycleOwner) { isLoginSuccessful ->
            if (isLoginSuccessful) {
                Toast.makeText(context, "USPJEŠAN LOGIN", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.actionLoginToShows)
            } else {
                Toast.makeText(context, "NEUSPJEŠAN LOGIN", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initLoginListeners(){
        binding.apply {
            loginButton.setOnClickListener {

                sharedPref?.let { sharedPref ->
                    viewModel.login(
                        editEmailInput.text.toString(),
                        editPasswordInput.text.toString(),
                        sharedPref
                    )
                }

                with(sharedPref?.edit()) {
                    this?.putString(
                        getString(R.string.username),
                        editEmailInput.text.toString()
                    )
                    this?.putBoolean(getString(R.string.remember_me), rememberMeCB.isChecked)
                    this?.putBoolean(getString(R.string.registerSuccessful), false)
                    this?.apply()
                }
            }
        }
    }

    private fun checkIfSignedIn() {
        val sharedPref =
            activity?.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref?.getBoolean(getString(R.string.remember_me), false) == true) {
            findNavController().navigate(R.id.actionLoginToShows)
        }
    }

    private fun checkIfRegisterSuccessful() {
        val sharedPref =
            activity?.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref?.getBoolean(getString(R.string.registerSuccessful), false) == true) {
            binding.loginTitle.text = getString(R.string.registerSuccessful)
            binding.registerButton.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initListeners() {
        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.actionLoginToRegister)
        }

        binding.editEmailInput.doAfterTextChanged {
            if (disabledButton && emailPattern.matcher(binding.editEmailInput.text.toString())
                    .matches()
                && binding.editPasswordInput.text.toString().trim().length > 5
            ) {
                binding.loginButton.setBackgroundResource(R.drawable.ic_button_white)
                binding.loginButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background
                    )
                )
                binding.loginButton.isEnabled = true
                disabledButton = false
                binding.emailInput.error = null
            } else if (!disabledButton && !emailPattern.matcher(binding.editEmailInput.text.toString())
                    .matches()
            ) {
                binding.loginButton.setBackgroundResource(R.drawable.ic_button_gray)
                binding.loginButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
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
                    .trim().length > 5 && emailPattern.matcher(binding.editEmailInput?.text.toString())
                    .matches()
            ) {
                binding.loginButton.setBackgroundResource(R.drawable.ic_button_white)
                binding.loginButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background
                    )
                )
                binding.loginButton.isEnabled = true
                disabledButton = false
            } else if (binding.editPasswordInput.text.toString().trim().length < 6) {
                binding.loginButton.setBackgroundResource(R.drawable.ic_button_gray)
                binding.loginButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.loginButton.isEnabled = false
                disabledButton = true
            }
        }

    }
}