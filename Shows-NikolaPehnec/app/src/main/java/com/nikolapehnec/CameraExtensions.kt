package com.nikolapehnec

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

fun Fragment.prepareCameraContract(onSuccess: () -> Unit) =
    registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
        if (result) {
            onSuccess()
        }
    }


