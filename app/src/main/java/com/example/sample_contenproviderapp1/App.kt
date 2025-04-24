package com.example.sample_contenproviderapp1

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class App : Application() {

    val applicationScope: CoroutineScope = CoroutineScope(
        Dispatchers.Main + SupervisorJob()
    )

}