package my.way.timestripe.task.presentation.task_list

import my.way.timestripe.task.domain.model.Task
import java.time.LocalDate

sealed class TaskListActions {
    data class AddTask(val task: Task) : TaskListActions()
    data class UpdateNewTask(val task: Task) : TaskListActions()
    data object ToggleNewTaskCompleted : TaskListActions()
    data class SetSelectedDate(val date: LocalDate) : TaskListActions()
    data class SetSelectedTask(val task: Task) : TaskListActions()
    data class UpdateSelectedTaskTitle(val title: String) : TaskListActions()
    data class UpdateSelectedTaskDescription(val description: String) : TaskListActions()
    data object ToggleSelectedTaskCompleted : TaskListActions()
    data class UpdateSelectedTaskDueDate(val dueDate: LocalDate) : TaskListActions()
    data object DeleteSelectedTask : TaskListActions()
    data object UnsetSelectedTask : TaskListActions()
    data object LoadTasks : TaskListActions()
    data class LoadTasksForDate(val date: LocalDate) : TaskListActions()
    data class NavigateToTaskDetail(val taskId: Long) : TaskListActions()
    data class ToggleTaskCompleted(val task: Task) : TaskListActions()
    data class DeleteTask(val task: Task) : TaskListActions()
    data object AddTaskClicked : TaskListActions()
    data object SaveNewTask : TaskListActions()
    data class ChangeColumn(val column: Int) : TaskListActions()
}