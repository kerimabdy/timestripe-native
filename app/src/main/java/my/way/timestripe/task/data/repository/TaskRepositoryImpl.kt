package my.way.timestripe.task.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import my.way.timestripe.task.data.local.dao.TaskDao
import my.way.timestripe.task.data.local.entity.TaskEntity
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.domain.repository.TaskRepository
import java.time.LocalDate

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

    override fun getTasksByDate(date: LocalDate): Flow<List<Task>> {
        return taskDao.getTasksByDate(date).map { entities ->
            entities.map { it.toDomain() }
        }
    }


    override fun getTasksByDateAndColumn(baseDate: LocalDate, column: Int): Flow<List<Task>> {
        return taskDao.getTasksByDateAndColumn(baseDate, column).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getLifeTasks(): Flow<List<Task>> {
        return taskDao.getLifeTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun TaskEntity.toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            dueDate = dueDate,
            column = column,
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
            column = column,
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    override suspend fun getTasksByDateAndColumnForWidget(baseDate: LocalDate, column: Int): List<Task> {
        return taskDao.getTasksByDateAndColumnForWidget(baseDate, column).map { it.toDomain() }
    }

} 