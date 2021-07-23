package com.nikolapehnec

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.nikolapehnec.databinding.ActivityLoginBinding
import java.util.regex.Pattern

class LoginFragment : Fragment() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private var disabledButton: Boolean = true
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fm: FragmentManager? = fragmentManager
        for (entry in 0 until fm!!.getBackStackEntryCount()) {
            Log.i(TAG, "Found fragment: " + fm.getBackStackEntryAt(entry).getId())
        }

        disabledButton=true
        initLoginButton()
        initListeners()

        checkIfSignedIn()


    }

    private fun checkIfSignedIn(){
        val sharedPref =
            activity?.applicationContext?.getSharedPreferences("1", Context.MODE_PRIVATE)
        if(sharedPref?.getBoolean(getString(R.string.remember_me),false)==true){
            findNavController().navigate(R.id.actionLoginToShows)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /* override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         //supportActionBar?.hide()

         _binding = ActivityLoginBinding.inflate(layoutInflater)
         setContentView(_binding?.root)

         initLoginButton()
         initListeners()

         if (intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME) != null && intent.type == "text/plain") {
             val intent2 = Intent()
             intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME)?.let {
             intent2.setClassName(this, it) }
             startActivity(intent2)
         }
     }*/

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
                this?.putBoolean(getString(R.string.remember_me),binding.rememberMeCB.isChecked)
                this?.apply()
            }


            findNavController().navigate(R.id.actionLoginToShows)
        }
    }

    private fun initListeners() {
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