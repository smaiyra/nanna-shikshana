package com.dbit.nannashikshana.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dbit.nannashikshana.domain.model.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nanna_shikshana_prefs")

@Singleton
class PreferencesStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val LANG = stringPreferencesKey("preferred_lang")
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
        val STUDENT_CODE = stringPreferencesKey("student_code")
        val DEVICE_ID = stringPreferencesKey("device_id")
        val SYNC_CURSOR = intPreferencesKey("sync_cursor")
        val LAST_SYNCED_MS = longPreferencesKey("last_synced_ms")
    }

    val languageFlow: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        AppLanguage.fromCode(prefs[Keys.LANG])
    }

    val lastSyncedMsFlow: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[Keys.LAST_SYNCED_MS]
    }

    suspend fun setLanguage(lang: AppLanguage) {
        context.dataStore.edit { it[Keys.LANG] = lang.code }
    }

    suspend fun getJwtToken(): String? = context.dataStore.data.first()[Keys.JWT_TOKEN]

    suspend fun setJwtToken(token: String?) {
        context.dataStore.edit { prefs ->
            if (token == null) prefs.remove(Keys.JWT_TOKEN) else prefs[Keys.JWT_TOKEN] = token
        }
    }

    suspend fun getOrCreateDeviceId(): String {
        val current = context.dataStore.data.first()[Keys.DEVICE_ID]
        if (current != null) return current
        val created = "android-" + UUID.randomUUID().toString().take(12)
        context.dataStore.edit { it[Keys.DEVICE_ID] = created }
        return created
    }

    suspend fun getStudentCode(): String =
        context.dataStore.data.first()[Keys.STUDENT_CODE] ?: "STU_ANANDA_01"

    suspend fun setStudentCode(code: String) {
        context.dataStore.edit { it[Keys.STUDENT_CODE] = code }
    }

    suspend fun getSyncCursor(): Int = context.dataStore.data.first()[Keys.SYNC_CURSOR] ?: 0

    suspend fun setSyncCursor(cursor: Int, syncedAtMs: Long) {
        context.dataStore.edit {
            it[Keys.SYNC_CURSOR] = cursor
            it[Keys.LAST_SYNCED_MS] = syncedAtMs
        }
    }
}
