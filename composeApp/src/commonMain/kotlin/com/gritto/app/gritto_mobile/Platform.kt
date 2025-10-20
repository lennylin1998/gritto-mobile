package com.gritto.app.gritto_mobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform