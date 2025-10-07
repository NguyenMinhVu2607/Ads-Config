package com.heaven.adsconfig.core

interface IRemoteConfigProvider {
    suspend fun fetchConfig(mode: AdsMode): AdsConfig?
}