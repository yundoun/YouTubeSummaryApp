package com.example.youtube_summary_native.core.constants

object ApiConstants {
    // Content
    const val DEFAULT_YOUTUBE_URL = "https://www.youtube.com"
    const val SUMMARY_CONTENT_ENDPOINT = "/summary/content"
    const val SUMMARY_CONTENT_ALL_ENDPOINT = "/summary/content/all"
    const val SUMMARY_WEBSOCKET_ENDPOINT = "/summary/ws/summary"

    // Users
    const val SUMMARY_USERS_ENDPOINT = "/summary/users"
    const val SUMMARY_USERS_ALL_ENDPOINT = "/summary/users/all"

    // Ping
    const val SUMMARY_PING_ENDPOINT = "/summary/ws/ping"

    // Auth
    const val AUTH_LOGIN_ENDPOINT = "/summary/auth/login"
    const val AUTH_REFRESH_ENDPOINT = "/summary/auth/refresh"
    const val AUTH_LOGOUT_ENDPOINT = "/summary/auth/logout"
}