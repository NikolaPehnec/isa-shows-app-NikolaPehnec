package com.nikolapehnec

import android.content.Context
import android.content.SharedPreferences
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
import androidx.navigation.fragment.findNavController
import com.nikolapehnec.databinding.FragmentLoginBinding
import com.nikolapehnec.viewModel.LoginViewModel
import java.util.regex.Pattern


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disabledButton = true
        initListeners()
        checkIfSignedIn()
        checkIfRegisterSuccessful()

        viewModel.getloginResultLiveData().observe(this.viewLifecycleOwner) { isLoginSuccessful ->

            binding.progressCircular.isVisible = false
            activity?.getWindow()?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if (isLoginSuccessful) {
                Toast.makeText(context, getString(R.string.loginSuccesfulMess), Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.actionLoginToShows)
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.loginUnSuccesful))
                if (viewModel.getMessage() != null) {
                    builder.setMessage(viewModel.getMessage())
                } else {
                    builder.setMessage(getString(R.string.loginUnSuccesfulMess))
                }
                builder.setPositiveButton(getString(R.string.Ok)) { _, _ ->
                }

                builder.show()
            }
        }
    }


    private fun checkIfSignedIn() {
        if (sharedPref?.getBoolean(getString(R.string.remember_me), false) == true) {
            findNavController().navigate(R.id.actionLoginToShows)
        }
    }

    private fun checkIfRegisterSuccessful() {
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

        initLoginButtonListeners()
        initEmailInputListeners()
        initPasswordInputListeners()
    }

    private fun initLoginButtonListeners() {
        binding.apply {
            loginButton.setOnClickListener {
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
    }

    private fun initEmailInputListeners() {
        binding.editEmailInput.doAfterTextChanged {
            if (disabledButton && emailPattern.matcher(binding.editEmailInput.text.toString())
                    .matches()
                && binding.editPasswordInput.text.toString().trim().length > 5
            ) {
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
                binding.emailInput.error = getString(R.string.invalidEmail)
            } else {
                binding.emailInput.error = null
            }
        }
    }

    private fun initPasswordInputListeners() {
        binding.editPasswordInput.doAfterTextChanged {
            if (disabledButton && binding.editPasswordInput.text.toString()
                    .trim().length > 5 && emailPattern.matcher(binding.editEmailInput?.text.toString())
                    .matches()) {
                binding.loginButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background
                    )
                )
                binding.loginButton.isEnabled = true
                disabledButton = false
                binding.passwordInput.error=null
            } else if (binding.editPasswordInput.text.toString().trim().length < 6) {
                binding.loginButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.loginButton.isEnabled = false
                binding.passwordInput.error=getString(R.string.passwordInvalid)
                disabledButton = true
            } else if (binding.editPasswordInput.text.toString().trim().length >5){
                binding.passwordInput.error=null
            }
        }
    }
}