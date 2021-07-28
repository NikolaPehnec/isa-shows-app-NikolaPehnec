package com.nikolapehnec

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nikolapehnec.databinding.FragmentLoginBinding
import java.util.regex.Pattern

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var disabledButton: Boolean = true
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disabledButton = true
        initLoginButton()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initLoginButton() {
        binding.loginButton.setOnClickListener {
            val sharedPref =
                activity?.getPreferences(Context.MODE_PRIVATE)
            with(sharedPref?.edit()) {
                this?.putString(
                    getString(R.string.username),
                    binding.editEmailInput.text.toString().split("@")[0]
                )
                this?.apply()
            }


            findNavController().navigate(R.id.actionLoginToShows)
        }
    }

    private fun initListeners() {
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
                    .trim().length > 5 && emailPattern.matcher(binding.editEmailInput.text.toString())
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