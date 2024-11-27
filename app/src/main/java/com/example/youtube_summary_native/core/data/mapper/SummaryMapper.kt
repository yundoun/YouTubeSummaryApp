package com.example.youtube_summary_native.core.data.mapper

import com.example.youtube_summary_native.core.data.local.entity.SummaryEntity
import com.example.youtube_summary_native.core.data.remote.dto.AllSummariesDto
import com.example.youtube_summary_native.core.data.remote.dto.ScriptItemDto
import com.example.youtube_summary_native.core.data.remote.dto.SummaryInfoDto
import com.example.youtube_summary_native.core.data.remote.dto.SummaryResponseDto
import com.example.youtube_summary_native.core.domain.model.summary.AllSummaries
import com.example.youtube_summary_native.core.domain.model.summary.ScriptItem
import com.example.youtube_summary_native.core.domain.model.summary.SummaryInfo
import com.example.youtube_summary_native.core.domain.model.summary.SummaryResponse

// DTO to Domain
fun SummaryResponseDto.toDomain(): SummaryResponse {
    return SummaryResponse(
        summaryInfo = summaryInfo.toDomain(),
        status = status,
        orderNumber = orderNumber,
        errorCode = errorCode,
        message = message
    )
}

fun AllSummariesDto.toDomain(): AllSummaries {
    return AllSummaries(
        summaryList = summaryList?.map { it.toDomain() } ?: emptyList(),
        status = status,
        errorCode = errorCode,
        message = message
    )
}

fun SummaryInfoDto.toDomain(): SummaryInfo {
    return SummaryInfo(
        videoId = videoId,
        title = title,
        summary = summary,
        rawScript = rawScript,  // 파싱된 script 사용
        thumbnailUrl = thumbnailUrl,
        status = status,
        createdAt = createdAt
    )
}

fun ScriptItemDto.toDomain(): ScriptItem {
    return ScriptItem(
        text = text,
        start = start,
        duration = duration
    )
}

// Entity to Domain
fun SummaryEntity.toDomain(): SummaryInfo {
    return SummaryInfo(
        videoId = videoId,
        title = title,
        summary = summary,
        rawScript = rawScript,
        thumbnailUrl = thumbnailUrl,
        status = status,
        createdAt = createdAt
    )
}

// Domain to Entity
fun SummaryInfo.toEntity(): SummaryEntity {
    return SummaryEntity(
        videoId = videoId,
        title = title,
        summary = summary,
        rawScript = rawScript,
        thumbnailUrl = thumbnailUrl,
        status = status,
        createdAt = createdAt
    )
}