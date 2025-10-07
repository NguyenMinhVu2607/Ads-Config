package com.heaven.adsconfig.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.heaven.adsconfig.core.AdsConfig
import com.heaven.adsconfig.core.AdsPlacement

object JsonParser {

    private const val TAG = "JsonParser"

    /**
     * Parse JSON string thành AdsConfig (toàn bộ config quảng cáo).
     *
     * @param json Chuỗi JSON từ Remote Config (Firebase RC).
     * @return AdsConfig hoặc null nếu lỗi parse.
     */
    fun parseAdsConfig(json: String): AdsConfig? {
        if (json.isBlank()) {
            Log.w(TAG, "⚠️ JSON trống, không thể parse.")
            return null
        }

        return try {
            val gson = Gson()
            val root = gson.fromJson(json, JsonObject::class.java)

            // ✅ Kiểm tra key "global" & "placements" có tồn tại không
            if (!root.has("global") || !root.has("placements")) {
                Log.e(TAG, "❌ JSON không hợp lệ: thiếu key 'global' hoặc 'placements'.")
                return null
            }

            val global = root.getAsJsonObject("global")
            val placements = root.getAsJsonObject("placements")

            val adsOn = global.get("ads_on")?.asBoolean ?: true // fallback (giá trị dự phòng)

            val map = mutableMapOf<String, AdsPlacement>()

            placements.entrySet().forEach { entry ->
                val key = entry.key
                val obj = entry.value.asJsonObject

                // Safe get từng field
                val id = obj.get("id")?.asString ?: ""
                val on = obj.get("on")?.asBoolean ?: false

                map[key] = AdsPlacement(id, on)
            }

            val config = AdsConfig(
                global = AdsConfig.GlobalConfig(adsOn),
                placements = map
            )

            // 🔍 Log ra toàn bộ config parse được
            Log.d(TAG, "✅ Parse RC JSON thành công: Global ads_on=$adsOn, Total=${map.size} placements.")
            map.forEach { (key, placement) ->
                Log.d(TAG, "📍 $key → id=${placement.id}, on=${placement.on}")
            }

            config

        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "❌ Lỗi cú pháp JSON (JsonSyntaxException): ${e.message}")
            null
        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi không xác định khi parse RC: ${e.message}", e)
            null
        }
    }
}
