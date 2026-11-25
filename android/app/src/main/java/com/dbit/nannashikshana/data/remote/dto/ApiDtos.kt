package com.dbit.nannashikshana.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    @SerializedName("student_code") val studentCode: String,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("passcode") val passcode: String,
    @SerializedName("name") val name: String,
    @SerializedName("village") val village: String,
    @SerializedName("school") val school: String,
    @SerializedName("preferred_lang") val preferredLang: String
)

data class LoginRequestDto(
    @SerializedName("student_code") val studentCode: String,
    @SerializedName("passcode") val passcode: String,
    @SerializedName("device_id") val deviceId: String
)

data class StudentDto(
    @SerializedName("id") val id: String,
    @SerializedName("student_code") val studentCode: String,
    @SerializedName("name") val name: String,
    @SerializedName("village") val village: String,
    @SerializedName("school") val school: String,
    @SerializedName("preferred_lang") val preferredLang: String,
    @SerializedName("total_xp") val totalXp: Int,
    @SerializedName("current_level") val currentLevel: String,
    @SerializedName("streak_count") val streakCount: Int,
    @SerializedName("streak_freezes_left") val streakFreezesLeft: Int,
    @SerializedName("last_active_date") val lastActiveDate: String
)

data class TokenResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("student") val student: StudentDto
)

data class PackVersionCheckRequestDto(
    @SerializedName("installed_versions") val installedVersions: Map<String, Int>
)

data class PackManifestEntryDto(
    @SerializedName("pack_code") val packCode: String,
    @SerializedName("package_name") val packageName: String,
    @SerializedName("version") val version: Int,
    @SerializedName("sha256") val sha256: String,
    @SerializedName("file_size_mb") val fileSizeMb: Double,
    @SerializedName("subject") val subject: String,
    @SerializedName("lesson_key") val lessonKey: String
)

data class PackVersionCheckResponseDto(
    @SerializedName("updates_available") val updatesAvailable: List<PackManifestEntryDto>,
    @SerializedName("up_to_date_codes") val upToDateCodes: List<String>
)

data class LeaderboardEntryDto(
    @SerializedName("rank") val rank: Int,
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("village") val village: String,
    @SerializedName("school") val school: String,
    @SerializedName("total_xp") val totalXp: Int,
    @SerializedName("streak") val streak: Int,
    @SerializedName("is_current_user") val isCurrentUser: Boolean
)

data class PaginatedLeaderboardDto(
    @SerializedName("items") val items: List<LeaderboardEntryDto>
)

data class SyncEventDto(
    @SerializedName("event_id") val eventId: String,
    @SerializedName("idempotency_key") val idempotencyKey: String,
    @SerializedName("event_type") val eventType: String,
    @SerializedName("client_timestamp") val clientTimestamp: String,
    @SerializedName("payload") val payload: Map<String, Any>
)

data class SyncPushRequestDto(
    @SerializedName("events") val events: List<SyncEventDto>
)

data class SyncEventResultDto(
    @SerializedName("event_id") val eventId: String,
    @SerializedName("idempotency_key") val idempotencyKey: String,
    @SerializedName("status") val status: String,
    @SerializedName("server_seq") val serverSeq: Int?
)

data class SyncPushResponseDto(
    @SerializedName("results") val results: List<SyncEventResultDto>,
    @SerializedName("latest_cursor") val latestCursor: Int,
    @SerializedName("total_xp") val totalXp: Int,
    @SerializedName("streak_count") val streakCount: Int
)

data class SyncPullResponseDto(
    @SerializedName("since_cursor") val sinceCursor: Int,
    @SerializedName("next_cursor") val nextCursor: Int,
    @SerializedName("has_more") val hasMore: Boolean,
    @SerializedName("server_state") val serverState: Map<String, Any>?
)
