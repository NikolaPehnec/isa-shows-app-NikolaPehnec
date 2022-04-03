package com.nikolapehnec.viewModel

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


    private var message: String? = ""
    private val registrationResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun getRegistrationResultLiveData(): LiveData<Boolean> {
        return registrationResultLiveData
    }

    fun getMessage(): String? {
        return message
    }

    fun register(email: String, password: String, passwordConfirmation: String) {
        ApiModule.retrofit.register(RegisterRequest(email, password, passwordConfirmation))
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (!response.isSuccessful) {
                        message = response.errorBody()?.string()?.substringAfter("\"errors\":[\"")
                            ?.substringBefore("\"],\"image_url\"");
                    }

                    registrationResultLiveData.value = response.isSuccessful
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    registrationResultLiveData.value = false
                    message = t.message
                }

            })
    }
}