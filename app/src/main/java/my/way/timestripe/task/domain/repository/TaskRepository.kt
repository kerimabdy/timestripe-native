package my.way.timestripe.task.domain.repository

import kotlinx.coroutines.flow.Flow
import my.way.timestripe.task.domain.model.Task

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun deleteTaskById(taskId: Long)
} 