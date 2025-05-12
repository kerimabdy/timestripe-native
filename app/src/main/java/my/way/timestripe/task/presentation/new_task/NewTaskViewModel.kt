package my.way.timestripe.task.presentation.new_task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import my.way.timestripe.task.domain.repository.TaskRepository
import java.time.LocalDate


class NewTaskViewModel(
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<NewTaskUiState>(NewTaskUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: NewTaskAction) {
        when (action) {
            is NewTaskAction.TaskTitleChanged -> onTaskTitleChanged(action.title)
            is NewTaskAction.TaskDescriptionChanged -> onTaskDescriptionChanged(action.description)
            is NewTaskAction.TaskDueDateChanged -> onTaskDueDateChanged(action.dueDate)
            is NewTaskAction.TaskCompletedToggle -> onTaskCompletedToggle()
            is NewTaskAction.SaveTask -> onTaskSaved()
            is NewTaskAction.DismissRequested -> Unit
        }
    }

    private fun onTaskTitleChanged(title: String) {
        _uiState.update { it.copy(task = it.task.copy(title = title)) }
    }

    private fun onTaskDescriptionChanged(description: String) {
        _uiState.update { it.copy(task = it.task.copy(description = description)) }
    }

    private fun onTaskDueDateChanged(dueDate: LocalDate) {
        _uiState.update { it.copy(task = it.task.copy(dueDate = dueDate)) }
    }

    private fun onTaskCompletedToggle() {
        _uiState.update { it.copy(task = it.task.copy(isCompleted = !it.task.isCompleted)) }
    }


    private fun onTaskSaved() {
        if (_uiState.value.task.title.isNotEmpty()) {
            viewModelScope.launch {
                taskRepository.insertTask(_uiState.value.task)
            }
        }
    }
}