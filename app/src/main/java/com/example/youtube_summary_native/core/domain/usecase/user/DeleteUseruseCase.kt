package com.example.youtube_summary_native.core.domain.usecase.user

import com.example.youtube_summary_native.core.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Int) = repository.deleteUser(userId)
}