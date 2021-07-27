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
        sharedPref = activity?.applicationContext?.getSharedPreferences("1", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        disabledButton = true
        //initLoginButton()
        initListeners()

        checkIfSignedIn()
        checkIfRegisterSuccessful()

        viewModel.getloginResultLiveData().observe(this.viewLifecycleOwner) { isLoginSuccessful ->
            if (isLoginSuccessful) {
                Toast.makeText(context, "USPJEŠAN LOGIN", Toast.LENGTH_SHORT).show()
                //activity?.onBackPressed()

                findNavController().navigate(R.id.actionLoginToShows)

            } else {
                Toast.makeText(context, "NIJE USPJEŠAN LOGIN", Toast.LENGTH_SHORT).show()
            }
        }

        binding.apply {
            loginButton.setOnClickListener {
                sharedPref?.let { it1 ->
                    viewModel.login(
                        editEmailInput.text.toString(), editPasswordInput.text.toString(),
                        it1
                    )
                }
            }
        }
    }

    private fun checkIfSignedIn() {
        val sharedPref =
            activity?.applicationContext?.getSharedPreferences("1", Context.MODE_PRIVATE)
        if (sharedPref?.getBoolean(getString(R.string.remember_me), false) == true) {
            findNavController().navigate(R.id.actionLoginToShows)
        }
    }

    private fun checkIfRegisterSuccessful() {
        val sharedPref =
            activity?.applicationContext?.getSharedPreferences("1", Context.MODE_PRIVATE)
        if (sharedPref?.getBoolean(getString(R.string.registerSuccessful), false) == true) {
            binding.loginTitle.text = getString(R.string.registerSuccessful)
            binding.registerButton.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initLoginButton() {
        binding.loginButton.setOnClickListener {
            val sharedPref =
                activity?.applicationContext?.getSharedPreferences("1", Context.MODE_PRIVATE)
            with(sharedPref?.edit()) {
                this?.putString(
                    getString(R.string.username),
                    binding.editEmailInput.text.toString().split("@")[0]
                )
                println(binding.rememberMeCB.isChecked)
                this?.putBoolean(getString(R.string.remember_me), binding.rememberMeCB.isChecked)
                this?.apply()
            }


            //findNavController().navigate(R.id.actionLoginToShows)
        }
    }

    private fun initListeners() {
        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.actionLoginToRegister)
        }

        _binding?.editEmailInput?.doAfterTextChanged {
            if (disabledButton && emailPattern.matcher(_binding?.editEmailInput?.text.toString())
                    .matches()
                && _binding?.editPasswordInput?.text.toString().trim().length > 5
            ) {
                _binding?.loginButton?.setBackgroundResource(R.drawable.ic_button_white)
                _binding?.loginButton?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background
                    )
                )
                _binding?.loginButton?.isEnabled = true
                disabledButton = false
                _binding?.emailInput?.error = null
            } else if (!disabledButton && !emailPattern.matcher(_binding?.editEmailInput?.text.toString())
                    .matches()
            ) {
                _binding?.loginButton?.setBackgroundResource(R.drawable.ic_button_gray)
                _binding?.loginButton?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                _binding?.loginButton?.isEnabled = false
                disabledButton = true
            }
            if (!emailPattern.matcher(_binding?.editEmailInput?.text.toString()).matches()) {
                _binding?.emailInput?.error = "Invalid email"
            } else {
                _binding?.emailInput?.error = null
            }
        }


        _binding?.editPasswordInput?.doAfterTextChanged {
            if (disabledButton && _binding?.editPasswordInput?.text.toString()
                    .trim().length > 5 && emailPattern.matcher(_binding?.editEmailInput?.text.toString())
                    .matches()
            ) {
                _binding?.loginButton?.setBackgroundResource(R.drawable.ic_button_white)
                _binding?.loginButton?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background
                    )
                )
                _binding?.loginButton?.isEnabled = true
                disabledButton = false
            } else if (_binding?.editPasswordInput?.text.toString().trim().length < 6) {
                _binding?.loginButton?.setBackgroundResource(R.drawable.ic_button_gray)
                _binding?.loginButton?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                _binding?.loginButton?.isEnabled = false
                disabledButton = true
            }
        }

    }
}