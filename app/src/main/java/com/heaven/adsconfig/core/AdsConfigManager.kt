package com.heaven.adsconfig.core

import android.content.Context
import android.util.Log
import com.google.firebase.BuildConfig
import com.heaven.adsconfig.firebase.FirebaseRemoteConfigProvider
import com.heaven.adsconfig.utils.JsonParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Quản lý vòng đời load và apply cấu hình quảng cáo (AdsConfig).
 *
 * Nguyên tắc:
 * - Load từ Remote Config Firebase
 * - Parse JSON sang model [AdsConfig]
 * - Apply ngay vào AdsRepository (cache trong RAM hoặc SharedPreferences)
 */
object AdsConfigManager {

    private const val TAG = "AdsConfigManager"

    /**
     * Load Remote Config và apply ngay config mới.
     *
     * @param context Context app
     * @param dispatcher Cho phép inject dispatcher nếu cần test (mặc định IO)
     */
    suspend fun load(
        context: Context,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) = withContext(dispatcher) {
        Log.d(TAG, "🚀 Bắt đầu load Remote Config...")

        try {
            // 1️⃣ Lấy JSON config từ Firebase RC
            val json = FirebaseRemoteConfigProvider.fetchAdsConfigJson(AdsMode.FULL_ADS)

            if (json.isNullOrBlank()) {
                Log.w(TAG, "⚠️ Không nhận được JSON từ RC, giữ nguyên config cũ.")
                return@withContext
            }

            // 2️⃣ Parse JSON thành model
            val config = JsonParser.parseAdsConfig(json)
            if (config == null) {
                Log.e(TAG, "❌ Parse RC thất bại. Giữ nguyên cấu hình cũ.")
                return@withContext
            }

            // 3️⃣ Apply config vào Repository
            AdsRepository.setConfig(config)
            Log.i(TAG, "💾 Đã apply và cache config mới (${config.placements.size} placements).")

            // 4️⃣ Log chi tiết tất cả placement (nếu debug)
            if (BuildConfig.DEBUG) {
                config.placements.forEach { (key, placement) ->
                    Log.d(TAG, "📍 $key → id=${placement.id}, on=${placement.on}")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi trong quá trình load RC: ${e.message}", e)
        }
    }

    /**
     * Hàm gọi để refresh config thủ công (nếu muốn cập nhật giữa phiên)
     */
    suspend fun refresh(context: Context) {
        Log.d(TAG, "🔄 Thực hiện refresh AdsConfig...")
        load(context)
    }
}
