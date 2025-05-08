package my.way.timestripe.task.presentation.task_detail

import java.time.LocalDate

sealed interface TaskDetailAction {
    data class ChangeTaskTitle(val title: String) : TaskDetailAction
    data class ChangeTaskDescription(val description: String) : TaskDetailAction
    data object ToggleTaskCompleted : TaskDetailAction
    data class ChangeTaskDueDate(val dueDate: LocalDate) : TaskDetailAction
    data object DeleteTask : TaskDetailAction
    data object SaveTask : TaskDetailAction
}