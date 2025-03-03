package com.jesushzc.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jesushzc.core.database.dao.AnalyticsDao
import com.jesushzc.core.database.dao.RunDao
import com.jesushzc.core.database.dao.RunPendingSyncDao
import com.jesushzc.core.database.entity.DeletedRunSyncEntity
import com.jesushzc.core.database.entity.RunEntity
import com.jesushzc.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        DeletedRunSyncEntity::class
    ],
    version = 4
)
abstract class RunDatabase: RoomDatabase() {

    abstract val runDao: RunDao
    abstract val runPendingSyncDao: RunPendingSyncDao
    abstract val analyticsDao: AnalyticsDao

}
