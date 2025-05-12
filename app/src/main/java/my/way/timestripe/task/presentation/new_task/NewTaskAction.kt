package my.way.timestripe.task.presentation.new_task

import java.time.LocalDate

sealed interface NewTaskAction {
    data class TaskTitleChanged(val title: String) : NewTaskAction
    data class TaskDescriptionChanged(val description: String) : NewTaskAction
    data class TaskDueDateChanged(val dueDate: LocalDate) : NewTaskAction
    data object TaskCompletedToggle : NewTaskAction
    data object SaveTask : NewTaskAction
    data object DismissRequested : NewTaskAction
  
}
