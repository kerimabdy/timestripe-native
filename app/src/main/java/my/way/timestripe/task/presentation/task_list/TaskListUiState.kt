package my.way.timestripe.task.presentation.task_list

import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.presentation.task_list.component.TaskNavigationMode
import java.time.LocalDate
import androidx.compose.runtime.Immutable

@Immutable
data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val newTask: Task = Task(title = ""),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedMode: TaskNavigationMode = TaskNavigationMode.DAY,
    val enabledModes: Set<TaskNavigationMode> = TaskNavigationMode.entries.toSet(),
    val isLoading: Boolean = false,
    val selectedTask: Task? = null,
    val isNewTaskShouldFocus: Boolean = false
)