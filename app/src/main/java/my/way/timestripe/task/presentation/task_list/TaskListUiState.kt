package my.way.timestripe.task.presentation.task_list

import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.presentation.task_list.component.TaskHorizon
import java.time.LocalDate
import androidx.compose.runtime.Immutable

@Immutable
data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val newTask: Task = Task(title = ""),
    val selectedDate: LocalDate? = LocalDate.now(),
    val selectedHorizon: TaskHorizon = TaskHorizon.DAY,
    val enabledHorizons: Set<TaskHorizon> = TaskHorizon.entries.toSet(),
    val isLoading: Boolean = false,
    val selectedTask: Task? = null,
    val isNewTaskShouldFocus: Boolean = false,
    
    // Properties for the horizontal pager
    val visibleDateRange: List<LocalDate> = emptyList(),
    val currentPage: Int = 0,
    val shouldRegeneratePages: Boolean = false
)