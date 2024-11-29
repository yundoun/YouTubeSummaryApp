package com.example.youtube_summary_native.core.domain.usecase.auth

import com.example.youtube_summary_native.core.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userId: Int) = repository.logout(userId)
}