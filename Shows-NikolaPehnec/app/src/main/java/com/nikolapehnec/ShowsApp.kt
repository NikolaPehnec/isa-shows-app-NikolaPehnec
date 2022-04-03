package com.nikolapehnec

import android.app.Application
import com.nikolapehnec.db.ShowsDatabase
import dagger.hilt.android.HiltAndroidApp

class ShowsApp : Application() {

    //Instancira prvi put kad se referencira
    val showsDatabase by lazy {
        ShowsDatabase.getDatabase(this)
    }
}