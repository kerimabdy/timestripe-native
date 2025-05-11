package my.way.timestripe.task.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import my.way.timestripe.task.data.local.entity.TaskEntity
import java.time.LocalDate

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE date(dueDate) = date(:date) ORDER BY createdAt DESC")
    fun getTasksByDate(date: LocalDate): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("SELECT * FROM tasks WHERE dueDate = :baseDate AND column = :column ORDER BY createdAt DESC")
    fun getTasksByDateAndColumn(baseDate: LocalDate, column: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE column = 5 ORDER BY createdAt DESC")
    fun getLifeTasks(): Flow<List<TaskEntity>>


    // getTasksByDateAndColumnForWidget
    @Query("SELECT * FROM tasks WHERE dueDate = :baseDate AND column = :column ORDER BY createdAt DESC")
    suspend fun getTasksByDateAndColumnForWidget(baseDate: LocalDate, column: Int): List<TaskEntity>
} 