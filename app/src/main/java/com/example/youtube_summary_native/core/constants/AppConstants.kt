package com.example.youtube_summary_native.core.constants

object AppConstants {
    // App Basic Info
    const val APP_NAME = "YouTube Video Info App"

    // System Messages
    const val BACK_PRESS_EXIT_MESSAGE = "'뒤로' 버튼을 한번 더 누르시면 종료됩니다."
    const val LOAD_DATA_ERROR = "데이터를 불러오는 중 오류가 발생했습니다."
    const val CONTENT_ITEM_NULL = "Content item is null"
    const val DEFAULT_MESSAGE_CONTENT = "message not response"

    // UI Layout
    const val MOBILE_LAYOUT = "Mobile Layout"
    const val WEB_LAYOUT = "Web Layout"

    // Navigation & Labels
    const val TITLE = "title"
    const val HOME = "home"
    const val HOME_UPPER = "Home"
    const val FAVORITE_UPPER = "Favorite"
    const val ICON = "icon"
    const val CONTENT = "content"

    // Search Related
    const val HOME_SEARCH_BAR_TEXT = "요약할 영상의 URL을 입력하세요."

    // Action Labels
    const val DELETE_LABEL = "삭제"
    const val DELETE_ALL = "모두 삭제"
    const val SHARE_LABEL = "공유"
    const val COPY_COMPLETED = "복사되었습니다."
    const val COPY_FAILED = "복사에 실패했습니다."

    // Summary Related
    const val RECENT_SUMMARIZED = "최근 요약"
    const val RECENT_SUMMARIZED_MOVIES = "최근 요약한 영상"
    const val SUMMARY_SCRIPT = "요약 스크립트"
    val EMPTY_SUMMARY_SCRIPT = """
        ### ⚠️ 스크립트를 가져올 수 없습니다
        요청한 YouTube 영상이 스크립트를 지원하지 않는 경우일 수 있습니다.
        
        **확인 방법:**
        - 해당 영상이 자막을 제공하는지 확인해 주세요.
        - 다른 영상으로 시도해 보세요.
    """.trimIndent()

    // Sort Related
    const val LATEST = "최신순"
    const val OLDEST = "오래된순"

    // Database Related
    const val DATABASE_NAME = "database"
    const val NATIVE_DATABASE_NAME = "database.sqlite"

    const val OFFLINE_MODE = "오프라인 모드"

    const val NO_RECENT_SUMMARIES = "최근 요약이 없습니다."

    // Etc
    const val EMPTY_STRING = ""
    const val KOREAN = "korean"
    const val ID = "id"
    const val ERROR = "error"

    const val START_SUMMARY = "요약 시작"

    // External Resources
    const val LOTTIE_LOADING_ANIMATION = "https://lottie.host/470d6163-ac4f-4d94-99c4-d1778b0c0294/yFOqrUtuD2.json"
}