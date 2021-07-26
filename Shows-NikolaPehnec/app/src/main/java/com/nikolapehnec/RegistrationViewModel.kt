package com.nikolapehnec

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.model.RegisterRequest
import com.nikolapehnec.model.RegisterResponse
import com.nikolapehnec.networking.ApiModule

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationViewModel : ViewModel() {

    private val registrationResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun getRegistrationResultLiveData(): LiveData<Boolean> {
        return registrationResultLiveData
    }

    fun register(email: String, password: String, passwordConfirmation: String) {
        ApiModule.retrofit.register(RegisterRequest(email, password, passwordConfirmation))
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    registrationResultLiveData.value = response.isSuccessful
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    registrationResultLiveData.value = false
                }

            })
    }
}