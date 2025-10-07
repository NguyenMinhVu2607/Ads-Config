package com.heaven.adsconfig.core

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * Repository quản lý toàn bộ config quảng cáo.
 * Có cache (SharedPreferences) + bộ nhớ tạm (in-memory).
 */
object AdsRepository {

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()
    private var currentConfig: AdsConfig? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("ads_repo_prefs", Context.MODE_PRIVATE)
        loadFromCache()
    }

    fun setConfig(config: AdsConfig) {
        currentConfig = config
        saveToCache(config)
    }

    fun getConfig(): AdsConfig? = currentConfig

    // ============================================================
    //  Các hàm sử dụng trực tiếp trong app
    // ============================================================

    /**
     * Lấy ID quảng cáo của một placement.
     * @return id hoặc null nếu không có.
     */
    fun getId(placementKey: String): String? {
        val config = currentConfig ?: return null
        val placement = config.placements[placementKey]
        return placement?.id
    }

    /**
     * Kiểm tra xem placement có được bật không (on/off).
     * @return true nếu bật.
     */
    fun isOn(placementKey: String): Boolean {
        val config = currentConfig ?: return false
        if (!config.global.adsOn) return false
        val placement = config.placements[placementKey]
        return placement?.on ?: false
    }

    /**
     * Trả về toàn bộ danh sách placement (debug, thống kê).
     */
    fun getAllPlacements(): Map<String, AdsPlacement>? {
        return currentConfig?.placements
    }

    /**
     * Xóa cache RC hiện tại.
     */
    fun clearCache() {
        prefs.edit().clear().apply()
        currentConfig = null
    }

    // ============================================================
    //  Private
    // ============================================================

    private fun saveToCache(config: AdsConfig) {
        prefs.edit().putString("ads_config_json", gson.toJson(config)).apply()
    }

    private fun loadFromCache() {
        val json = prefs.getString("ads_config_json", null) ?: return
        currentConfig = try {
            gson.fromJson(json, AdsConfig::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
