package my.way.timestripe.task.presentation.task_list

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import my.way.timestripe.task.domain.model.Task
import java.time.LocalDate

class TaskListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        TaskListUiState(
            tasks = listOf(
                // Example tasks for preview/testing
                Task(id = "1", title = "Buy groceries", date = LocalDate.now(), isCompleted = false),
                Task(id = "2", title = "Read a book", date = LocalDate.now().plusDays(1), isCompleted = true)
            )
        )
    )
    val state = _uiState.asStateFlow()

    fun onAction(action: TaskListActions) {
        when (action) {
            is TaskListActions.LoadTasks -> {
                // TODO: Load tasks from repository
            }
            is TaskListActions.OpenTask -> {
                // TODO: Handle task selection
            }
            is TaskListActions.ToggleTaskCompleted -> {
                // TODO: Toggle completion
            }
            is TaskListActions.DeleteTask -> {
                // TODO: Delete task
            }
            is TaskListActions.AddTaskClicked -> {
                // TODO: Handle add task
            }
            is TaskListActions.ChangeDate -> {
                _uiState.value = _uiState.value.copy(selectedDate = action.date)
                // Optionally: load tasks for the new date
            }
            is TaskListActions.ChangeMode -> {
                _uiState.value = _uiState.value.copy(selectedMode = action.mode)
            }
        }
    }
}