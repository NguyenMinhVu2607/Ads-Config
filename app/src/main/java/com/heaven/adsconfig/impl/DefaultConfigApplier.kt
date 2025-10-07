package com.heaven.adsconfig.impl

import com.heaven.adsconfig.core.AdsConfig
import com.heaven.adsconfig.core.IConfigApplier

class DefaultConfigApplier : IConfigApplier {

    override fun apply(config: AdsConfig) {
        if (!config.global.adsOn) {
            println("⚠️ Ads globally disabled")
            return
        }

        config.placements.forEach { (name, placement) ->
            println("✅ Apply: $name -> id=${placement.id}, on=${placement.on}")
            // 👉 Bạn có thể xử lý thêm ở đây, ví dụ:
            // AdsController.setPlacementEnabled(name, placement.on)
        }
    }
}