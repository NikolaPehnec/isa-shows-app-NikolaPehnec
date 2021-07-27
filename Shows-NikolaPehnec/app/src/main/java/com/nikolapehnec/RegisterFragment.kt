package com.nikolapehnec

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nikolapehnec.databinding.ActivityRegisterBinding
import java.util.regex.Pattern

class RegisterFragment : Fragment() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private var disabledButton: Boolean = true
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS

    private val viewModel: RegistrationViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

        viewModel.getRegistrationResultLiveData()
            .observe(this.viewLifecycleOwner) { isRegisterSuccessful ->
                if (isRegisterSuccessful) {
                    Toast.makeText(context, "USPJEŠNA REGISTRACIJA", Toast.LENGTH_SHORT).show()
                    val sharedPref =
                        activity?.applicationContext?.getSharedPreferences(
                            "1",
                            Context.MODE_PRIVATE
                        )
                    with(sharedPref?.edit()) {
                        this?.putBoolean(getString(R.string.registerSuccessful), true)
                        this?.apply()
                    }
                    activity?.onBackPressed()
                } else {
                    Toast.makeText(context, "NEUSPJEŠNA REGISTRACIJA", Toast.LENGTH_SHORT).show()
                }
            }

        binding.apply {
            registerButton.setOnClickListener {
                viewModel.register(
                    editEmailInput.text.toString(),
                    editPasswordInput.text.toString(),
                    editRepeatPasswordInput.text.toString()
                )
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initListeners() {
        binding.editEmailInput.doAfterTextChanged {
            if (disabledButton && emailPattern.matcher(binding.editEmailInput.text.toString())
                    .matches()
                && binding.editPasswordInput.text.toString().trim().length > 5 &&
                binding.editRepeatPasswordInput.text.toString().trim().length > 5
            ) {
                binding.registerButton.setBackgroundResource(R.drawable.ic_button_white)
                binding.registerButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background
                    )
                )
                binding.registerButton.isEnabled = true
                disabledButton = false
                binding.emailInput.error = null
            } else if (!disabledButton && !emailPattern.matcher(binding.editEmailInput.text.toString())
                    .matches()
            ) {
                binding.registerButton.setBackgroundResource(R.drawable.ic_button_gray)
                binding.registerButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.registerButton.isEnabled = false
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
                    .matches() && binding.editRepeatPasswordInput.text.toString().trim().length > 5
            ) {
                binding.registerButton.setBackgroundResource(R.drawable.ic_button_white)
                binding.registerButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background
                    )
                )
                binding.registerButton.isEnabled = true
                disabledButton = false
            } else if (binding.editPasswordInput.text.toString().trim().length < 6) {
                binding.registerButton.setBackgroundResource(R.drawable.ic_button_gray)
                binding.registerButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.registerButton.isEnabled = false
                disabledButton = true
            }
        }

        binding.editRepeatPasswordInput.doAfterTextChanged {
            if (disabledButton && binding.editPasswordInput.text.toString()
                    .trim().length > 5 && emailPattern.matcher(binding.editEmailInput?.text.toString())
                    .matches() && binding.editRepeatPasswordInput.text.toString().trim().length > 5
            ) {
                binding.registerButton.setBackgroundResource(R.drawable.ic_button_white)
                binding.registerButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background
                    )
                )
                binding.registerButton.isEnabled = true
                disabledButton = false
            } else if (binding.editRepeatPasswordInput.text.toString().trim().length < 6) {
                binding.registerButton.setBackgroundResource(R.drawable.ic_button_gray)
                binding.registerButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.registerButton.isEnabled = false
                disabledButton = true
            }
        }

    }
}