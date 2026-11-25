package com.dbit.nannashikshana.di

import android.content.Context
import androidx.room.Room
import com.dbit.nannashikshana.BuildConfig
import com.dbit.nannashikshana.data.local.NannaDatabase
import com.dbit.nannashikshana.data.local.PreferencesStore
import com.dbit.nannashikshana.data.local.dao.BadgeDao
import com.dbit.nannashikshana.data.local.dao.LeaderboardDao
import com.dbit.nannashikshana.data.local.dao.LessonDao
import com.dbit.nannashikshana.data.local.dao.QuestDao
import com.dbit.nannashikshana.data.local.dao.StudentDao
import com.dbit.nannashikshana.data.local.dao.SyncEventDao
import com.dbit.nannashikshana.data.remote.NannaApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NannaDatabase =
        Room.databaseBuilder(
            context,
            NannaDatabase::class.java,
            "nanna_shikshana.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideStudentDao(db: NannaDatabase): StudentDao = db.studentDao()

    @Provides
    fun provideLessonDao(db: NannaDatabase): LessonDao = db.lessonDao()

    @Provides
    fun provideQuestDao(db: NannaDatabase): QuestDao = db.questDao()

    @Provides
    fun provideBadgeDao(db: NannaDatabase): BadgeDao = db.badgeDao()

    @Provides
    fun provideLeaderboardDao(db: NannaDatabase): LeaderboardDao = db.leaderboardDao()

    @Provides
    fun provideSyncEventDao(db: NannaDatabase): SyncEventDao = db.syncEventDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(preferencesStore: PreferencesStore): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { preferencesStore.getJwtToken() }
            val req = if (!token.isNullOrBlank()) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(req)
        }
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNannaApiService(retrofit: Retrofit): NannaApiService =
        retrofit.create(NannaApiService::class.java)
}
