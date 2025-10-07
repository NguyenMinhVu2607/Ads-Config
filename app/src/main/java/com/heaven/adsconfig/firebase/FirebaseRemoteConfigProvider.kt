package com.heaven.adsconfig.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.heaven.adsconfig.core.AdsMode
import kotlinx.coroutines.tasks.await

/**
 * Provider chịu trách nhiệm fetch và cung cấp JSON config từ Firebase Remote Config.
 * Hỗ trợ cả 2 chế độ: FULL_ADS và NORMAL_ADS.
 */
object FirebaseRemoteConfigProvider {

    private const val TAG = "FirebaseRCProvider"

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        Firebase.remoteConfig.apply {
            // Tùy chọn: thiết lập interval fetch thấp hơn trong quá trình dev
            setConfigSettingsAsync(
                com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(0)
                    .build()
            )
        }
    }

    /**
     * Lấy JSON từ Firebase Remote Config theo mode.
     *
     * @param mode AdsMode (FULL_ADS hoặc NORMAL_ADS)
     * @return Chuỗi JSON hoặc null nếu lỗi.
     */
    suspend fun fetchAdsConfigJson(mode: AdsMode): String? {
        try {
            // 1️⃣ Xác định key theo mode
            val key = when (mode) {
                AdsMode.FULL_ADS -> "ads_config_full_ads"
                AdsMode.NORMAL_ADS -> "ads_config_normal"
            }

            Log.d(TAG, "🚀 Bắt đầu fetch Remote Config cho key: $key")

            // 2️⃣ Fetch + Activate
            val updated = remoteConfig.fetchAndActivate().await()
            Log.d(TAG, "🔁 FetchAndActivate completed. Updated: $updated")

            // 3️⃣ Log toàn bộ RC keys
            val allKeys = remoteConfig.all
            Log.d(TAG, "📦 Remote Config Data Dump (${allKeys.size} keys):")
            for ((k, v) in allKeys) {
                Log.d(TAG, "➡️ Key: $k | Value: ${v.asString()}")
            }

            // 4️⃣ Lấy JSON từ key tương ứng
            var json = remoteConfig.getString(key)
            if (json.isNullOrEmpty() || json == "{}") {
                Log.w(TAG, "⚠️ RC key '$key' rỗng, thử fallback sang 'ads_config_normal'")
                json = remoteConfig.getString("ads_config_normal")
            }

            // 5️⃣ Log kết quả cuối cùng
            Log.d(TAG, "✅ RC JSON ($key): $json")
            return json

        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi khi fetch Remote Config: ${e.message}", e)
        }
        return null
    }
}
