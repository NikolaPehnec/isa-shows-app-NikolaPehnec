package com.nikolapehnec

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
    private var accessToken: String? = null
    private var client: String? = null
    private var tokenType: String? = null
    private var uid: String? = null

    fun getloginResultLiveData(): LiveData<Boolean> {
        return loginResultLiveData
    }

    fun getAccessToken(): String? {
        return accessToken
    }

    fun getClient(): String? {
        return client
    }

    fun getTokenType(): String? {
        return tokenType
    }

    fun getUid(): String? {
        return uid
    }

    fun login(email: String, password: String) {
        ApiModule.retrofit.login(LoginRequest(email, password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    accessToken = response.headers()["access-token"]
                    client = response.headers()["client"]
                    tokenType = response.headers()["token-type"]
                    uid = response.headers()["uid"]
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loginResultLiveData.value = false
                }

            })
    }
}