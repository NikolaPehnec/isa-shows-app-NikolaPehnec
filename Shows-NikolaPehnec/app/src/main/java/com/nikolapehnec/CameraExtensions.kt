package com.nikolapehnec

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

fun Fragment.prepareCameraContract(onSuccess: () -> Unit) =
    registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
        if (result) {
            onSuccess()
        }
    }

fun Fragment.prepareGalleryContract(onUri: (uri: Uri?) -> Unit) =
    registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
        onUri(result)
    }


