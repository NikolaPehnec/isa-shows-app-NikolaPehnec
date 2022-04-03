package com.nikolapehnec.viewModel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.NetworkChecker
import com.nikolapehnec.db.ShowsDatabase
import com.nikolapehnec.model.LoginResponse
import com.nikolapehnec.model.ShowEntity
import com.nikolapehnec.model.ShowResponse
import com.nikolapehnec.networking.ApiModule
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.concurrent.Executors


class ShowsViewModel(
    val database: ShowsDatabase,
    val context: Context
) : ViewModel() {

    private val showEntityLiveData: MutableLiveData<List<ShowEntity>> by lazy {
        MutableLiveData<List<ShowEntity>>()
    }

    private val showEntityLiveData2: MutableLiveData<List<ShowEntity>> by lazy {
        MutableLiveData<List<ShowEntity>>()
    }

    private val errorMessageLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getErrorMessageLiveData(): LiveData<String> {
        //inace se error pamtio i u onCreatu na povratku iz showDetaila pojavljivao
        val err: LiveData<String> = errorMessageLiveData
        errorMessageLiveData.value = null
        return err
    }

    fun getShowsEntityLiveData(): LiveData<List<ShowEntity>> {
        val networkChecker = NetworkChecker(context)
        if (networkChecker.isOnline()) {
            Log.v("load_iz", "API")
            return showEntityLiveData
        } else {
            Log.v("load_iz", "BAZA")
            showEntityLiveData.value = null
            return database.showsDao().getAllShows()
        }
    }

    fun getTopRatedShowsEntityLiveData(): LiveData<List<ShowEntity>> {
        return showEntityLiveData2
    }


    fun getShows() {
        val networkChecker = NetworkChecker(context)
        if (networkChecker.isOnline()) {

            ApiModule.retrofit.getShows()
                .enqueue(object : Callback<ShowResponse> {
                    override fun onResponse(
                        call: Call<ShowResponse>,
                        response: Response<ShowResponse>
                    ) {
                        showEntityLiveData.value = response.body()?.show?.map {
                            ShowEntity(
                                it.id,
                                it.title,
                                it.avgRating,
                                it.description,
                                it.imgUrl,
                                it.numOfReviews
                            )
                        }

                        Executors.newSingleThreadExecutor().execute {
                            response.body()?.show?.let {
                                database.showsDao().insertAllShows(it.map { show ->
                                    ShowEntity(
                                        show.id,
                                        show.title,
                                        show.avgRating,
                                        show.description,
                                        show.imgUrl,
                                        show.numOfReviews
                                    )
                                })
                            }
                        }

                    }

                    override fun onFailure(call: Call<ShowResponse>, t: Throwable) {
                        //errorMessageLiveData.value=t.message
                    }
                })
        } else {
            errorMessageLiveData.value = "No internet connection"
        }
    }

    fun getTopRatedShows() {
        ApiModule.retrofit.getTopRatedShows()
            .enqueue(object : Callback<ShowResponse> {
                override fun onResponse(
                    call: Call<ShowResponse>,
                    response: Response<ShowResponse>
                ) {
                    showEntityLiveData2.value = response.body()?.show?.map {
                        ShowEntity(
                            it.id,
                            it.title,
                            it.avgRating,
                            it.description,
                            it.imgUrl,
                            it.numOfReviews
                        )
                    }
                }

                override fun onFailure(call: Call<ShowResponse>, t: Throwable) {
                    errorMessageLiveData.value = t.message
                }
            })
    }


    fun sendPicture(
        imgPath: String?,
        sharedPreferences: SharedPreferences,
    ) {
        val file: File = File(imgPath)
        val requestFile: RequestBody =
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        var profilePic = MultipartBody.Part.createFormData("image", file.name, requestFile)


        val call: Call<LoginResponse> = ApiModule.retrofit.updateImage(profilePic)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                //mozes dodat boolean za obavijesti na ekranu

                with(sharedPreferences.edit()) {
                    this.putString("imgUrl", response.body()?.user?.imageUrl)
                    this.apply()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                with(sharedPreferences.edit()) {
                }
            }
        })
    }


}