package com.example.youtube_summary_native.core.di

import android.content.Context
import androidx.room.Room
import com.example.youtube_summary_native.core.data.local.AppDatabase
import com.example.youtube_summary_native.core.data.local.dao.SummaryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "youtube_summary_app_database"
        )
            .fallbackToDestructiveMigration() // 마이그레이션 실패 시 DB 재생성
            .build()
    }

    @Provides
    @Singleton
    fun provideSummaryDao(appDatabase: AppDatabase): SummaryDao {
        return appDatabase.summaryDao()
    }
}