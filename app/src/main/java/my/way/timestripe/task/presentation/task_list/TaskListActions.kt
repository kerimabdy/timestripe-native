package my.way.timestripe.task.presentation.task_list

import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.presentation.task_list.component.TaskNavigationMode
import java.time.LocalDate

sealed class TaskListActions {
    data class AddTask(val task: Task) : TaskListActions()
    data class UpdateNewTask(val task: Task) : TaskListActions()
    data object ToggleNewTaskCompleted : TaskListActions()
    data object LoadTasks : TaskListActions()
    data class OpenTask(val task: Task) : TaskListActions()
    data class ToggleTaskCompleted(val task: Task) : TaskListActions()
    data class DeleteTask(val task: Task) : TaskListActions()
    data object AddTaskClicked : TaskListActions()
    data class ChangeDate(val date: LocalDate) : TaskListActions()
    data class ChangeMode(val mode: TaskNavigationMode) : TaskListActions()
    data object SaveNewTask : TaskListActions()
    data class SetNewTaskShouldFocus(val shouldFocus: Boolean) : TaskListActions()
}