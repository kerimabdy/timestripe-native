package my.way.timestripe.task.presentation.task_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.domain.repository.TaskRepository

class TaskListViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TaskListUiState()
    )
    val state = _uiState.onStart {
        onLoadTasks()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TaskListUiState()
    )

    fun onAction(action: TaskListActions) {
        when (action) {
            is TaskListActions.LoadTasks -> {
                onLoadTasks()
            }

            is TaskListActions.UpdateNewTask -> {
                onUpdateNewTask(action.task)
            }

            is TaskListActions.ToggleNewTaskCompleted -> {
                onToggleNewTaskCompleted()
            }

            is TaskListActions.OpenTask -> {
                // TODO: Handle task selection
            }

            is TaskListActions.ToggleTaskCompleted -> {
                onToggleTaskCompleted(action.task)
            }

            is TaskListActions.DeleteTask -> {
                onDeleteTask(action.task)
            }

            is TaskListActions.AddTaskClicked -> {
                // TODO: Handle add task
            }

            is TaskListActions.AddTask -> {
                onAddTask(action.task)
            }

            is TaskListActions.ChangeDate -> {
                _uiState.value = _uiState.value.copy(selectedDate = action.date)
                // Optionally: load tasks for the new date
            }

            is TaskListActions.ChangeMode -> {
                _uiState.value = _uiState.value.copy(selectedMode = action.mode)
            }

            TaskListActions.SaveNewTask -> {
                onSaveNewTask()
            }
            
            is TaskListActions.SetNewTaskShouldFocus -> {
                _uiState.update {
                    it.copy(isNewTaskShouldFocus = action.shouldFocus)
                }
            }
        }
    }

    private fun onAddTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }
    }

    private fun onLoadTasks() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            taskRepository.getAllTasks().collect { tasks ->
                _uiState.update {
                    it.copy(isLoading = false, tasks = tasks)
                }
            }
        }
    }

    private fun onToggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    private fun onUpdateNewTask(task: Task) {
        _uiState.update {
            it.copy(newTask = task)
        }   
    }

    private fun onToggleNewTaskCompleted() {
        _uiState.update {
            it.copy(newTask = it.newTask.copy(isCompleted = !it.newTask.isCompleted))
        }
    }

    private fun onSaveNewTask() {
        val currentNewTask = _uiState.value.newTask
        if (currentNewTask.title.isNotBlank()) {
            viewModelScope.launch {
                taskRepository.insertTask(currentNewTask)
                _uiState.update {
                    it.copy(newTask = Task(title = ""))
                }
            }
        }
    }

    private fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}