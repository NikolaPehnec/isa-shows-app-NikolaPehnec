package com.nikolapehnec

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nikolapehnec.databinding.FragmentRegisterBinding
import com.nikolapehnec.viewModel.RegistrationViewModel
import java.util.regex.Pattern

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var disabledButton: Boolean = true
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS

    private val viewModel: RegistrationViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getRegistrationResultLiveData()
            .observe(this.viewLifecycleOwner) { isRegisterSuccessful ->

                binding.progressCircular.isVisible = false
                activity?.getWindow()?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                if (isRegisterSuccessful) {
                    Toast.makeText(
                        context,
                        getString(R.string.registrationSuccesfulMess),
                        Toast.LENGTH_SHORT
                    ).show()
                    val sharedPref =
                        activity?.getPreferences(Context.MODE_PRIVATE)
                    with(sharedPref?.edit()) {
                        this?.putBoolean(getString(R.string.registerSuccessful), true)
                        this?.apply()
                    }
                    activity?.onBackPressed()
                } else {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(getString(R.string.registrationUnSuccesfulMess))
                    if (viewModel.getMessage() != null) {
                        builder.setMessage(viewModel.getMessage())
                    } else {
                        builder.setMessage(getString(R.string.registrationUnSuccesfulMess2))
                    }
                    builder.setPositiveButton(getString(R.string.Ok)) { _, _ ->
                    }

                    builder.show()
                }

            }

        initListeners()
        initEmailInputListeners()
        initPasswordInputListeners()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initListeners() {
        binding.apply {
            registerButton.setOnClickListener {
                val networkChecker = NetworkChecker(requireContext())
                if (!networkChecker.isOnline()) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(getString(R.string.notification))
                    builder.setMessage(getString(R.string.noInternet))

                    builder.setPositiveButton(getString(R.string.Ok)) { _, _ ->
                    }

                    builder.show()
                } else {
                    binding.progressCircular.isVisible = true
                    activity?.getWindow()?.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )

                    viewModel.register(
                        editEmailInput.text.toString(),
                        editPasswordInput.text.toString(),
                        editRepeatPasswordInput.text.toString()
                    )
                }
            }
        }
    }

    private fun initEmailInputListeners() {
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
    }

    private fun initPasswordInputListeners() {
        binding.editPasswordInput.doAfterTextChanged {
            if (disabledButton && binding.editPasswordInput.text.toString()
                    .trim().length > 5 && emailPattern.matcher(binding.editEmailInput?.text.toString())
                    .matches() && binding.editRepeatPasswordInput.text.toString().trim().length > 5
            ) {
                binding.registerButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background
                    )
                )
                binding.registerButton.isEnabled = true
                disabledButton = false
            } else if (binding.editPasswordInput.text.toString().trim().length > 5) {
                binding.passwordInput.error = null
            } else if (binding.editPasswordInput.text.toString().trim().length < 6) {
                binding.registerButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.registerButton.isEnabled = false
                binding.passwordInput.error = getString(R.string.passwordInvalid)
                disabledButton = true
            }
        }

        //repeat password input
        binding.editRepeatPasswordInput.doAfterTextChanged {
            if (disabledButton && binding.editPasswordInput.text.toString()
                    .trim().length > 5 && emailPattern.matcher(binding.editEmailInput?.text.toString())
                    .matches() && binding.editRepeatPasswordInput.text.toString().trim().length > 5
                && binding.editRepeatPasswordInput.text.toString()
                    .equals(binding.editPasswordInput.text.toString())
            ) {
                binding.registerButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background
                    )
                )
                binding.registerButton.isEnabled = true
                disabledButton = false
                binding.repeatPasswordInput.error = null
            } else if (!binding.editRepeatPasswordInput.text.toString()
                    .equals(binding.editPasswordInput.text.toString())
            ) {
                binding.registerButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.registerButton.isEnabled = false
                disabledButton = true
                binding.repeatPasswordInput.error = getString(R.string.passwordNotMatched)
                //samo dobar repeat passworda, ne omoguciti gumb
            } else if (binding.editRepeatPasswordInput.text.toString()
                    .equals(binding.editPasswordInput.text.toString())
            ) {
                binding.repeatPasswordInput.error = null
            }
        }
    }
}