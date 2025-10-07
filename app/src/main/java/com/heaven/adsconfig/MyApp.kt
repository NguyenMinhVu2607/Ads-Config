package com.heaven.adsconfig

import android.app.Application
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.heaven.adsconfig.core.AdsConfigManager
import com.heaven.adsconfig.core.AdsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AdsRepository.init(this)
    }
}
