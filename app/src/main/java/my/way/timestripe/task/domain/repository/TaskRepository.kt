package my.way.timestripe.task.domain.repository

import kotlinx.coroutines.flow.Flow
import my.way.timestripe.task.domain.model.Task
import java.time.LocalDate

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun deleteTaskById(taskId: Long)
    suspend fun getTasksByDateAndColumnForWidget(baseDate: LocalDate, column: Int): List<Task>
    fun getTasksByDate(date: LocalDate): Flow<List<Task>>
    fun getTasksByDateAndColumn(baseDate: LocalDate, column: Int): Flow<List<Task>>
    fun getLifeTasks(): Flow<List<Task>>
}