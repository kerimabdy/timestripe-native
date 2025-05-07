package my.way.timestripe.task.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import my.way.timestripe.task.data.local.dao.TaskDao
import my.way.timestripe.task.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    
    companion object {
        // Migration from version 1 to 2: Adding column field
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new column field with default value 1 (DAY)
                database.execSQL("ALTER TABLE tasks ADD COLUMN `column` INTEGER NOT NULL DEFAULT 1")
            }
        }
    }
}

