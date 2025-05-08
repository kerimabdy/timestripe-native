package my.way.timestripe.task.presentation.task_list

import my.way.timestripe.task.domain.model.Task
import java.time.LocalDate

sealed class TaskListActions {
    data class AddTask(val task: Task) : TaskListActions()
    data class UpdateNewTask(val task: Task) : TaskListActions()
    data object ToggleNewTaskCompleted : TaskListActions()
    data class SetSelectedDate(val date: LocalDate) : TaskListActions()
    data object LoadTasks : TaskListActions()
    data class LoadTasksForDate(val date: LocalDate) : TaskListActions()
    data class OpenTask(val task: Task) : TaskListActions()
    data class ToggleTaskCompleted(val task: Task) : TaskListActions()
    data class DeleteTask(val task: Task) : TaskListActions()
    data object AddTaskClicked : TaskListActions()
    data object SaveNewTask : TaskListActions()
    data class SetNewTaskShouldFocus(val shouldFocus: Boolean) : TaskListActions()
    data class ChangeColumn(val column: Int) : TaskListActions()
}