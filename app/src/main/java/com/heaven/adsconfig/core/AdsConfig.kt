package com.heaven.adsconfig.core

data class AdsConfig(
    val global: GlobalConfig,
    val placements: Map<String, AdsPlacement>
) {
    data class GlobalConfig(
        val adsOn: Boolean
    )
}
