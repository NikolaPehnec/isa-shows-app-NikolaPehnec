package com.nikolapehnec

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.model.LoginResponse
import com.nikolapehnec.model.Show
import com.nikolapehnec.model.ShowResponse
import com.nikolapehnec.networking.ApiModule
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ShowsViewModel : ViewModel() {

    private val showsLiveData: MutableLiveData<List<Show>> by lazy {
        MutableLiveData<List<Show>>()
    }

    fun getShowsLiveData(): LiveData<List<Show>> {
        return showsLiveData
    }

    fun getShows() {
        ApiModule.retrofit.getShows()
            .enqueue(object : Callback<ShowResponse> {
                override fun onResponse(
                    call: Call<ShowResponse>,
                    response: Response<ShowResponse>
                ) {
                    showsLiveData.value = response.body()?.show
                }

                override fun onFailure(call: Call<ShowResponse>, t: Throwable) {

                }
            })
    }

    fun sendPicture(id_user: String, email: String, imgPath: String) {
        val userId: RequestBody = id_user.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val fullName: RequestBody = email.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val file: File = File(imgPath)
        val requestFile: RequestBody =
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        var profilePic = MultipartBody.Part.createFormData("image", file.name, requestFile)


        val call: Call<LoginResponse> = ApiModule.retrofit.updateImage(userId, fullName, profilePic)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                //mozes dodat boolean za obavijesti na ekranu
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            }
        })
    }

}