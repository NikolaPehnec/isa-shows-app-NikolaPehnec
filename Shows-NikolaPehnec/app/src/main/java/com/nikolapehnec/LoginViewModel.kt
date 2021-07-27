package com.nikolapehnec

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.model.LoginRequest
import com.nikolapehnec.model.LoginResponse
import com.nikolapehnec.networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private val loginResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun getloginResultLiveData(): LiveData<Boolean> {
        return loginResultLiveData
    }

    fun login(email: String, password: String, sharedPref: SharedPreferences) {
        ApiModule.retrofit.login(LoginRequest(email, password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val accessToken = response.headers()["access-token"]
                    val client = response.headers()["client"]
                    val tokenType = response.headers()["token-type"]
                    val uid = response.headers()["uid"]

                    with(sharedPref?.edit()) {
                        this?.putString("loginSuccessful", accessToken)
                        this?.putString("tokentype", tokenType)
                        this?.putString("uid", uid)
                        this?.putString("client", client)
                        this?.putBoolean("registerSuccessful", false)
                        this?.apply()
                    }

                    loginResultLiveData.value = response.isSuccessful
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loginResultLiveData.value = false
                }

            })
    }
}