package com.anlarsinsoftware.first_kmp_project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform