package com.gritto.app

import moe.tlaster.precompose.BuildConfig

actual object Env {
    actual val API_BASE_URL: String
        get() = BuildConfig.API_BASE_URL
}