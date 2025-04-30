package my.way.timestripe.task.presentation.task_list

import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.presentation.task_list.component.TaskNavigationMode
import java.time.LocalDate

sealed class TaskListActions {
    object LoadTasks : TaskListActions()
    data class OpenTask(val task: Task) : TaskListActions()
    data class ToggleTaskCompleted(val task: Task) : TaskListActions()
    data class DeleteTask(val task: Task) : TaskListActions()
    object AddTaskClicked : TaskListActions()
    data class ChangeDate(val date: LocalDate) : TaskListActions()
    data class ChangeMode(val mode: TaskNavigationMode) : TaskListActions()
}