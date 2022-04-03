package com.nikolapehnec.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.Constants
import com.nikolapehnec.model.LoginRequest
import com.nikolapehnec.model.LoginResponse
import com.nikolapehnec.networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private var message: String? = ""

    private val loginResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun getloginResultLiveData(): LiveData<Boolean> {
        return loginResultLiveData
    }

    fun getMessage(): String? {
        return message
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

                    with(sharedPref.edit()) {
                        this.putString(Constants.LOGIN_SUCCESS, accessToken)
                        this.putString(Constants.TOKEN_TYPE, tokenType)
                        this.putString(Constants.UID, uid)
                        this.putString(Constants.CLIENT, client)
                        this.putBoolean(Constants.REGISTER_SUCCESS, false)
                        this.putString(Constants.EMAIL, response.body()?.user?.email)
                        this.putString(Constants.USER_ID, response.body()?.user?.id.toString())
                        this.putString(Constants.IMG_URL, response.body()?.user?.imageUrl)
                        this.apply()
                    }

                    if (!response.isSuccessful) {
                        message = response.errorBody()?.string()?.substringAfter("errors\":[\"")
                            ?.substringBeforeLast("\"]}")
                    }

                    loginResultLiveData.value = response.isSuccessful
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loginResultLiveData.value = false
                }
            })
    }


}