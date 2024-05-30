package com.jesushzc.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jesushzc.core.database.dao.RunDao
import com.jesushzc.core.database.entity.RunEntity

@Database(
    entities = [RunEntity::class],
    version = 1
)
abstract class RunDatabase: RoomDatabase() {

    abstract val runDao: RunDao

}
