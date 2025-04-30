package my.way.timestripe.task.presentation.task_list

import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.presentation.task_list.component.TaskNavigationMode
import java.time.LocalDate

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedMode: TaskNavigationMode = TaskNavigationMode.DAY,
    val enabledModes: Set<TaskNavigationMode> = TaskNavigationMode.values().toSet(),
    val isLoading: Boolean = false,
    val selectedTask: Task? = null
)