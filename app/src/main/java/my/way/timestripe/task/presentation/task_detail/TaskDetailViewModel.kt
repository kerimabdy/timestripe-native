package my.way.timestripe.task.presentation.task_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.domain.repository.TaskRepository
import java.time.LocalDate

class TaskDetailViewModel(
  savedStateHandle: SavedStateHandle,
  private val  taskRepository: TaskRepository,
): ViewModel() {

  val taskId = savedStateHandle.get<String>("taskId")

  private val _uiState = MutableStateFlow<TaskDetailUiState>(TaskDetailUiState(task = Task()))
  val state = _uiState.onStart {
    if (taskId != null) {
     viewModelScope.launch {
         _uiState.update {
             it.copy(task = taskRepository.getTaskById(taskId.toLong()) ?: Task())
         }
     }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskDetailUiState(task = Task()))


  fun onAction(action: TaskDetailAction) {
    when (action) {
      is TaskDetailAction.ChangeTaskTitle -> onTaskTitleChanged(action.title)
      is TaskDetailAction.ChangeTaskDescription -> onTaskDescriptionChanged(action.description)
      is TaskDetailAction.ToggleTaskCompleted -> onTaskCompletedToggle()
      is TaskDetailAction.ChangeTaskDueDate -> onTaskDueDateChanged(action.dueDate)
      is TaskDetailAction.DeleteTask -> onTaskDeleted()
      is TaskDetailAction.SaveTask -> onTaskSaved()
    }
  }

  private fun onTaskTitleChanged(title: String) {
    _uiState.update {
      it.copy(task = it.task.copy(title = title))
    }
  }

  private fun onTaskDescriptionChanged(description: String) {
    _uiState.update {
      it.copy(task = it.task.copy(description = description))
    }
  }

  private fun onTaskCompletedToggle() {
    _uiState.update {
      it.copy(task = it.task.copy(isCompleted = !it.task.isCompleted))
    }
  }

  private fun onTaskDueDateChanged(date: LocalDate) {
    _uiState.update {
      it.copy(task = it.task.copy(dueDate = date))
    }
  }

  private fun onTaskDeleted() {
    viewModelScope.launch { 
      taskRepository.deleteTask(state.value.task)
    }
  }

  private fun onTaskSaved() {
    viewModelScope.launch {
      taskRepository.updateTask(state.value.task)
    }
  }
  
  
}