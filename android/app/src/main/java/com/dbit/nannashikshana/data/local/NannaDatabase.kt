package com.dbit.nannashikshana.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dbit.nannashikshana.data.local.dao.BadgeDao
import com.dbit.nannashikshana.data.local.dao.LeaderboardDao
import com.dbit.nannashikshana.data.local.dao.LessonDao
import com.dbit.nannashikshana.data.local.dao.QuestDao
import com.dbit.nannashikshana.data.local.dao.StudentDao
import com.dbit.nannashikshana.data.local.dao.SyncEventDao
import com.dbit.nannashikshana.data.local.entity.BadgeEntity
import com.dbit.nannashikshana.data.local.entity.DailyQuestEntity
import com.dbit.nannashikshana.data.local.entity.LeaderboardPeerEntity
import com.dbit.nannashikshana.data.local.entity.LessonPackEntity
import com.dbit.nannashikshana.data.local.entity.LessonProgressEntity
import com.dbit.nannashikshana.data.local.entity.PendingSyncEventEntity
import com.dbit.nannashikshana.data.local.entity.StudentProfileEntity

@Database(
    entities = [
        StudentProfileEntity::class,
        LessonPackEntity::class,
        LessonProgressEntity::class,
        DailyQuestEntity::class,
        BadgeEntity::class,
        LeaderboardPeerEntity::class,
        PendingSyncEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NannaDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun lessonDao(): LessonDao
    abstract fun questDao(): QuestDao
    abstract fun badgeDao(): BadgeDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun syncEventDao(): SyncEventDao
}
