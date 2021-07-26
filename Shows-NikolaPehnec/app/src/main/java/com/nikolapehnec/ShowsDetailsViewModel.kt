package com.nikolapehnec

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.model.Review
import com.nikolapehnec.model.Show

class ShowsDetailsViewModel : ViewModel() {

    private val showLiveData: MutableLiveData<Show> by lazy {
        MutableLiveData<Show>()
    }

    fun getShowsLiveData(): LiveData<Show> {
        return showLiveData
    }

    fun initShow(showId: Int) {
        showLiveData.value = ShowsViewModel.ShowsResource.shows[showId]
    }

    fun addReview(review: Review) {
        showLiveData.value?.reviews = showLiveData.value?.reviews?.plus(review)!!
        //da se pozove observer
        showLiveData.value = showLiveData.value

    }

    fun calculateAverageGrade(): Float? =
        getShowsLiveData().value?.reviews?.map { r -> r.grade }?.average()?.toFloat()


}