package my.way.timestripe.task.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import my.way.timestripe.task.data.local.dao.TaskDao
import my.way.timestripe.task.data.local.entity.TaskEntity
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.domain.repository.TaskRepository

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    override suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    override suspend fun deleteTaskById(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }

    private fun TaskEntity.toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            dueDate = dueDate,
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun Task.toEntity(): TaskEntity {
        return TaskEntity(
            id = id,
            title = title,
            description = description,
            dueDate = dueDate,
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 