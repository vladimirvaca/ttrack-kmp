package com.rvladimir.ttrack

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform