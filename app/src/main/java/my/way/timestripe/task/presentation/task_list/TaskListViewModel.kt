package my.way.timestripe.task.presentation.task_list

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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

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


    // Column constants
    private val COLUMN_DAY = 1
    private val COLUMN_WEEK = 2
    private val COLUMN_MONTH = 3
    private val COLUMN_YEAR = 4
    private val COLUMN_LIFE = 5

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

            is TaskListActions.NavigateToTaskDetail -> Unit

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

            is TaskListActions.SaveNewTask -> {
                onSaveNewTask()
            }

            is TaskListActions.SetSelectedTask -> {
                onSetSelectedTask(action.task)
            }

            is TaskListActions.UnsetSelectedTask -> {
                onUnsetSelectedTask()
            }

            is TaskListActions.ChangeColumn -> {
                onChangeColumn(action.column)
            }


            is TaskListActions.SetSelectedDate -> {
                onSetSelectedDate(action.date)
            }

            is TaskListActions.LoadTasksForDate -> {
                onLoadTasksForDate(action.date)
            }

            is TaskListActions.UpdateSelectedTaskTitle -> {
                onUpdateSelectedTaskTitle(action.title)
            }

            is TaskListActions.UpdateSelectedTaskDescription -> {
                onUpdateSelectedTaskDescription(action.description)
            }

            is TaskListActions.UpdateSelectedTaskDueDate -> {
                onUpdateSelectedTaskDueDate(action.dueDate)
            }   

            is TaskListActions.DeleteSelectedTask -> {
                onDeleteSelectedTask()
            }

            TaskListActions.ToggleSelectedTaskCompleted -> onToggleSelectedTaskCompleted()
        }
    }

    private fun onSetSelectedTask(task: Task) {
        _uiState.update { it.copy(selectedTask = task) }
    }

    private fun onUpdateSelectedTaskTitle(title: String) {
        _uiState.update { it.copy(selectedTask = it.selectedTask?.copy(title = title) ?: it.selectedTask) }
    }

    private fun onUpdateSelectedTaskDescription(description: String) {
        _uiState.update { it.copy(selectedTask = it.selectedTask?.copy(description = description) ?: it.selectedTask) }
    }

    private fun onUpdateSelectedTaskDueDate(dueDate: LocalDate) {
        _uiState.update { it.copy(selectedTask = it.selectedTask?.copy(dueDate = dueDate) ?: it.selectedTask) }
    }

    private fun onToggleSelectedTaskCompleted() {
        _uiState.update { it.copy(selectedTask = it.selectedTask?.copy(isCompleted = !it.selectedTask.isCompleted) ?: it.selectedTask) }
    }

    private fun onDeleteSelectedTask() {
        viewModelScope.launch {
            taskRepository.deleteTask(state.value.selectedTask ?: return@launch)
            _uiState.update { it.copy(selectedTask = null) }
        }
    }
    private fun onUnsetSelectedTask() {
        viewModelScope.launch {
            taskRepository.updateTask(state.value.selectedTask ?: return@launch)
            _uiState.update { it.copy(selectedTask = null) }
        }

    }
    
    

    private fun onLoadTasksForDate(date: LocalDate) {
        viewModelScope.launch {
            val tasksFlow =
                taskRepository.getTasksByDateAndColumn(date, _uiState.value.selectedColumn)
            tasksFlow.collect { tasks ->
                when (_uiState.value.selectedColumn) {
                    COLUMN_DAY -> {
                        _uiState.update {
                            val updatedMap = HashMap(it.tasksForDay)
                            updatedMap[date] = tasks
                            it.copy(tasksForDay = updatedMap)
                        }
                    }

                    COLUMN_WEEK -> {
                        _uiState.update {
                            val updatedMap = HashMap(it.tasksForWeek)
                            updatedMap[date] = tasks
                            it.copy(tasksForWeek = updatedMap)
                        }
                    }

                    COLUMN_MONTH -> {
                        _uiState.update {
                            val updatedMap = HashMap(it.tasksForMonth)
                            updatedMap[date] = tasks
                            it.copy(tasksForMonth = updatedMap)
                        }
                    }

                    COLUMN_YEAR -> {
                        _uiState.update {
                            val updatedMap = HashMap(it.tasksForYear)
                            updatedMap[date] = tasks
                            it.copy(tasksForYear = updatedMap)
                        }
                    }

                    COLUMN_LIFE -> {
                        _uiState.update {
                            it.copy(tasksForLife = tasks)
                        }
                    }
                }
            }
        }
    }


    private fun onChangeColumn(newColumnType: Int) {
        if(_uiState.value.selectedColumn == newColumnType) onSetSelectedDate(LocalDate.now())
        else _uiState.update { it.copy(selectedColumn = newColumnType) }
    }


    private fun onLoadTasks() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//
//            val currentColumnType = _uiState.value.selectedColumn
//            val selectedDate = _uiState.value.selectedDate
//
//            // Query tasks based on the current column type and date
//            val tasks = when (currentColumnType) {
//                COLUMN_DAY -> {
//                    // For day view, get tasks for the specific day
//                    selectedDate?.let { taskRepository.getTasksByDateAndColumn(it, 1) }
//                }
//                COLUMN_WEEK -> {
//                    // For week view, get tasks for the week
//                    selectedDate?.let { taskRepository.getTasksByDateAndColumn(it, 2) }
//                }
//                COLUMN_MONTH -> {
//                    // For month view, get tasks for the month
//                    selectedDate?.let { taskRepository.getTasksByDateAndColumn(it, 3) }
//                }
//                COLUMN_YEAR -> {
//                    // For year view, get tasks for the year
//                    selectedDate?.let { taskRepository.getTasksByDateAndColumn(it, 4) }
//                }
//                else -> {
//                    // For life view, get all tasks
//                    taskRepository.getTasksByDateAndColumn(LocalDate.now(), 5)
//                }
//            }
//
//            _uiState.update {
//                it.copy(
//                    tasks = tasks,
//                    isLoading = false
//                )
//            }
//        }
    }


    private fun onUpdateNewTask(task: Task) {
        _uiState.update { it.copy(newTask = task) }
    }

    private fun onToggleNewTaskCompleted() {
        val currentTask = _uiState.value.newTask
        _uiState.update {
            it.copy(
                newTask = currentTask.copy(
                    isCompleted = !currentTask.isCompleted
                )
            )
        }
    }

    private fun onSaveNewTask() {
        val currentTask = _uiState.value.newTask
        val columnType = _uiState.value.selectedColumn

        // Only save if there's content in the title
        if (currentTask.title.isNotBlank()) {
            val date = when (columnType) {
                COLUMN_DAY -> _uiState.value.selectedDate
                COLUMN_WEEK -> _uiState.value.selectedDate.with(
                    TemporalAdjusters.previousOrSame(
                        DayOfWeek.MONDAY
                    )
                )

                COLUMN_MONTH -> _uiState.value.selectedDate.withDayOfMonth(1)
                COLUMN_YEAR -> _uiState.value.selectedDate.withDayOfYear(1)
                else -> null
            }

            val taskWithColumnAndDate = currentTask.copy(
                dueDate = date,
                column = columnType
            )

            onAddTask(taskWithColumnAndDate)

            // Reset the new task
            _uiState.update {
                it.copy(
                    newTask = Task(title = "")
                )
            }
        }
    }

    private fun onAddTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
            onLoadTasks()
        }
    }

    private fun onToggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(
                task.copy(isCompleted = !task.isCompleted)
            )
            onLoadTasks()
        }
    }

    private fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            onLoadTasks()
        }
    }

    private fun onSetSelectedDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }
}