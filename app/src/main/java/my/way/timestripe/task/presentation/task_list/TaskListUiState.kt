package my.way.timestripe.task.presentation.task_list

import my.way.timestripe.task.domain.model.Task
import java.time.LocalDate
import androidx.compose.runtime.Immutable

@Immutable
data class TaskListUiState(
    val tasksForDay: HashMap<LocalDate, List<Task>> = HashMap(),
    val tasksForWeek: HashMap<LocalDate, List<Task>> = HashMap(),
    val tasksForMonth: HashMap<LocalDate, List<Task>> = HashMap(),
    val tasksForYear: HashMap<LocalDate, List<Task>> = HashMap(),
    val tasksForLife: List<Task> = emptyList(),
    
    val newTask: Task = Task(title = ""),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedColumn: Int = 1, // 1=Day, 2=Week, 3=Month, 4=Year, 5=Life
    val enabledColumns: Set<Int> = setOf(1, 2, 3, 4, 5),
    val isLoading: Boolean = false,
    val selectedTask: Task? = null,
    val isNewTaskShouldFocus: Boolean = false,
    
    // Properties for the horizontal pager
    val visibleDateRange: List<LocalDate> = emptyList(),
    val currentPage: Int = 0,
    val shouldRegeneratePages: Boolean = false
)