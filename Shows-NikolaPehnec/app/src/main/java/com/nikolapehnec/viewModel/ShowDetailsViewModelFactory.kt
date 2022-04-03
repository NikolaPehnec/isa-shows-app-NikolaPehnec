package com.nikolapehnec.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nikolapehnec.db.ShowsDatabase


class ShowDetailsViewModelFactory(
    val database: ShowsDatabase,
    val context:Context
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowsDetailsSharedViewModel::class.java)) {
            return ShowsDetailsSharedViewModel(database,context) as T
        }

        throw IllegalArgumentException("Radi samo sa ShowsDetailViewModel klasama")
    }

}
