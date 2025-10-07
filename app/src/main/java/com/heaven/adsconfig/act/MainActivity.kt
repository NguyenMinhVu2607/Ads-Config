package com.heaven.adsconfig.act


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.heaven.adsconfig.R
import com.heaven.adsconfig.core.AdsRepository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Giả sử RC đã load xong ở Splash → giờ chỉ cần gọi
        checkAndLoadAds()
    }

    private fun checkAndLoadAds() {
        // Ví dụ 1: inter_home
        if (AdsRepository.isOn("inter_splash")) {
            val interId = AdsRepository.getId("inter_splash")
            Log.d("MainActivity", "Interstitial Home enabled with ID: $interId")
            // TODO: gọi SDK ads thật, ví dụ:
            // AdmobLoader.loadInterstitial(this, interId)
        } else {
            Log.d("MainActivity", "Interstitial Home disabled")
        }

        // Ví dụ 2: banner_all
        if (AdsRepository.isOn("banner_all")) {
            val bannerId = AdsRepository.getId("banner_all")
            Log.d("MainActivity", "Banner enabled with ID: $bannerId")
            // TODO: load banner bằng SDK bạn đang dùng
        }

        // Ví dụ 3: debug toàn bộ placement
        val allPlacements = AdsRepository.getAllPlacements()
        allPlacements?.forEach { (key, placement) ->
            Log.d("AdsRepo", "$key → id=${placement.id}, on=${placement.on}")
        }
    }
}
