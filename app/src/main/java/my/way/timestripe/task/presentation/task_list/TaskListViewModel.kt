package my.way.timestripe.task.presentation.task_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.domain.repository.TaskRepository
import my.way.timestripe.task.presentation.task_list.component.TaskHorizon
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import kotlin.math.min

class TaskListViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TaskListUiState()
    )
    val state = _uiState.onStart {
        onInitializePager()
        onLoadTasks()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TaskListUiState()
    )

    // Constants for horizon encoding
    private val DAY_OFFSET = 0L
    private val WEEK_OFFSET = 1L
    private val MONTH_OFFSET = 2L
    private val YEAR_OFFSET = 3L

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

            is TaskListActions.SaveNewTask -> {
                onSaveNewTask()
            }
            
            is TaskListActions.SetNewTaskShouldFocus -> {
                _uiState.update {
                    it.copy(isNewTaskShouldFocus = action.shouldFocus)
                }
            }

            is TaskListActions.ChangeHorizon -> {
                onChangeHorizon(action.horizon)
            }
            
            is TaskListActions.PageChanged -> {
                onPageChanged(action.newPage)
            }
            
            is TaskListActions.RegeneratePages -> {
                onRegeneratePages()
            }
            
            is TaskListActions.InitializePager -> {
                onInitializePager(action.centerOnDate)
            }
            
            is TaskListActions.UpdateVisibleDateRange -> {
                _uiState.update { it.copy(visibleDateRange = action.dateRange) }
            }
        }
    }

    private fun onInitializePager(centerOnDate: LocalDate? = null) {
        val selectedDate = centerOnDate ?: _uiState.value.selectedDate ?: LocalDate.now()
        val horizon = _uiState.value.selectedHorizon
        
        // Generate the visible date range
        val dateRange = generateVisibleDateRange(selectedDate, horizon)
        
        // Set the initial page to the center of the range (except for LIFE)
        val initialPage = if (horizon == TaskHorizon.LIFE) 0 else dateRange.size / 2
        
        _uiState.update { 
            it.copy(
                selectedDate = if (horizon == TaskHorizon.LIFE) null else selectedDate,
                visibleDateRange = dateRange,
                currentPage = initialPage
            )
        }
    }
    
    private fun onChangeHorizon(newHorizon: TaskHorizon) {
        val oldHorizon = _uiState.value.selectedHorizon
        val oldDate = _uiState.value.selectedDate
        
        // Calculate the date to center on in the new horizon
        val centerDate = when {
            // Going to LIFE mode - no date needed
            newHorizon == TaskHorizon.LIFE -> null
            
            // Coming from LIFE mode - use today
            oldHorizon == TaskHorizon.LIFE -> LocalDate.now()
            
            // Otherwise use the current selected date
            else -> oldDate ?: LocalDate.now()
        }
        
        _uiState.update { it.copy(selectedHorizon = newHorizon) }
        
        // Initialize the pager with the new horizon
        onInitializePager(centerDate)
        
        // Load tasks for the new horizon and date
        onLoadTasks()
    }
    
    private fun onPageChanged(newPage: Int) {
        val currentState = _uiState.value
        val dateRange = currentState.visibleDateRange
        val horizon = currentState.selectedHorizon
        
        // Skip for LIFE mode
        if (horizon == TaskHorizon.LIFE) return
        
        // Make sure the page exists
        if (newPage in dateRange.indices) {
            // Get the date for this page
            val pageDate = dateRange[newPage]
            
            // Update the selected date based on horizon
            val newSelectedDate = when (horizon) {
                TaskHorizon.DAY -> pageDate
                
                TaskHorizon.WEEK -> {
                    // Keep day of week consistent when changing weeks
                    val currentDate = currentState.selectedDate
                    val dayOfWeek = currentDate?.dayOfWeek ?: DayOfWeek.MONDAY
                    pageDate.plusDays(dayOfWeek.value.toLong() - 1)
                }
                
                TaskHorizon.MONTH -> {
                    // Try to keep same day of month when changing months
                    val currentDate = currentState.selectedDate
                    val dayOfMonth = currentDate?.dayOfMonth ?: 1
                    val maxDays = YearMonth.of(pageDate.year, pageDate.month).lengthOfMonth()
                    pageDate.withDayOfMonth(min(dayOfMonth, maxDays))
                }
                
                TaskHorizon.YEAR -> {
                    // Try to keep same month and day when changing years
                    val currentDate = currentState.selectedDate
                    val month = currentDate?.monthValue ?: 1
                    val dayOfMonth = currentDate?.dayOfMonth ?: 1
                    val maxDays = YearMonth.of(pageDate.year, month).lengthOfMonth()
                    pageDate.withMonth(month).withDayOfMonth(min(dayOfMonth, maxDays))
                }
                
                TaskHorizon.LIFE -> null // Should not reach here
            }
            
            // Check if we need to regenerate pages (close to boundaries)
            val shouldRegenerate = newPage < 15 || newPage > dateRange.size - 15
            
            // Update the UI state
            _uiState.update { 
                it.copy(
                    selectedDate = newSelectedDate,
                    currentPage = newPage,
                    shouldRegeneratePages = shouldRegenerate
                )
            }
            
            // Regenerate pages if needed
            if (shouldRegenerate) {
                onRegeneratePages()
            }
            
            // Load tasks for the new date
            onLoadTasks()
        }
    }
    
    private fun onRegeneratePages() {
        val currentState = _uiState.value
        val currentDate = currentState.selectedDate ?: LocalDate.now()
        val horizon = currentState.selectedHorizon
        
        // Generate new date range centered on current date
        val newDateRange = generateVisibleDateRange(currentDate, horizon)
        
        // Find the normalized date for the current page
        val normalizedCurrentDate = getNormalizedDate(currentDate, horizon)
        
        // Find the index of the normalized date in the new range
        val newIndex = newDateRange.indexOf(normalizedCurrentDate)
        
        // Update the UI state
        _uiState.update { 
            it.copy(
                visibleDateRange = newDateRange,
                currentPage = newIndex,
                shouldRegeneratePages = false
            )
        }
    }
    
    private fun generateVisibleDateRange(date: LocalDate, horizon: TaskHorizon): List<LocalDate> {
        // For LIFE mode, we only need one page
        if (horizon == TaskHorizon.LIFE) {
            return listOf(LocalDate.now())
        }
        
        // Calculate normalized date (reference point for current view)
        val normalizedDate = getNormalizedDate(date, horizon)
        
        // Generate appropriate number of items for current mode
        return when (horizon) {
            TaskHorizon.DAY -> {
                // Add 60 days on each side (total 121 days)
                (-60..60).map { normalizedDate.plusDays(it.toLong()) }
            }
            TaskHorizon.WEEK -> {
                // Add 40 weeks on each side (total 81 weeks)
                (-40..40).map { normalizedDate.plusWeeks(it.toLong()) }
            }
            TaskHorizon.MONTH -> {
                // Add 24 months on each side (total 49 months)
                (-24..24).map { normalizedDate.plusMonths(it.toLong()) }
            }
            TaskHorizon.YEAR -> {
                // Add 20 years on each side (total 41 years)
                (-20..20).map { normalizedDate.plusYears(it.toLong()) }
            }
            TaskHorizon.LIFE -> {
                // Should not reach here
                listOf(LocalDate.now())
            }
        }
    }
    
    private fun getNormalizedDate(date: LocalDate, horizon: TaskHorizon): LocalDate {
        return when (horizon) {
            TaskHorizon.DAY -> date // Keep day as is
            TaskHorizon.WEEK -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) // Week starts on Monday
            TaskHorizon.MONTH -> date.withDayOfMonth(1) // First day of month
            TaskHorizon.YEAR -> date.withDayOfYear(1) // First day of year
            TaskHorizon.LIFE -> LocalDate.now() // Not used
        }
    }

    private fun onLoadTasks() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            
            val currentHorizon = _uiState.value.selectedHorizon
            val currentDate = _uiState.value.selectedDate
            
            // Load tasks for the current view
            val tasksFlow = when (currentHorizon) {
                TaskHorizon.DAY -> {
                    // Get all day tasks for this date
                    val date = currentDate ?: LocalDate.now()
                    taskRepository.getTasksForHorizon(date, DAY_OFFSET)
                }
                TaskHorizon.WEEK -> {
                    // Get all week tasks for this week
                    val date = currentDate ?: LocalDate.now()
                    val weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    taskRepository.getTasksForHorizon(weekStart, WEEK_OFFSET)
                }
                TaskHorizon.MONTH -> {
                    // Get all month tasks for this month
                    val date = currentDate ?: LocalDate.now()
                    val monthStart = date.withDayOfMonth(1)
                    taskRepository.getTasksForHorizon(monthStart, MONTH_OFFSET)
                }
                TaskHorizon.YEAR -> {
                    // Get all year tasks for this year
                    val date = currentDate ?: LocalDate.now()
                    val yearStart = date.withDayOfYear(1)
                    taskRepository.getTasksForHorizon(yearStart, YEAR_OFFSET)
                }
                TaskHorizon.LIFE -> {
                    // Get all life tasks
                    taskRepository.getTasksForLife()
                }
            }
            
            tasksFlow.collect { tasks ->
                _uiState.update {
                    it.copy(isLoading = false, tasks = tasks)
                }
            }
        }
    }

    private fun onAddTask(task: Task) {
        val currentState = _uiState.value
        val horizon = currentState.selectedHorizon
        val selectedDate = currentState.selectedDate ?: LocalDate.now()
        
        // Get base date for the horizon
        val baseDate = when (horizon) {
            TaskHorizon.DAY -> selectedDate
            TaskHorizon.WEEK -> selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            TaskHorizon.MONTH -> selectedDate.withDayOfMonth(1)
            TaskHorizon.YEAR -> selectedDate.withDayOfYear(1)
            TaskHorizon.LIFE -> null
        }
        
        // Add offset to encode the horizon
        val encodedDate = when (horizon) {
            TaskHorizon.DAY -> baseDate?.plusDays(DAY_OFFSET)
            TaskHorizon.WEEK -> baseDate?.plusDays(WEEK_OFFSET)
            TaskHorizon.MONTH -> baseDate?.plusDays(MONTH_OFFSET)
            TaskHorizon.YEAR -> baseDate?.plusDays(YEAR_OFFSET)
            TaskHorizon.LIFE -> null
        }
        
        val taskToSave = task.copy(dueDate = encodedDate)
        
        viewModelScope.launch {
            taskRepository.insertTask(taskToSave)
            onLoadTasks()
        }
    }

    private fun onToggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = !task.isCompleted))
            // Refresh the task list
            onLoadTasks()
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
                // Set the due date based on current horizon and selected date
                val currentState = _uiState.value
                val taskToSave = when {
                    currentState.selectedHorizon == TaskHorizon.LIFE -> 
                        currentNewTask.copy(dueDate = null)
                    currentState.selectedDate != null -> 
                        currentNewTask.copy(dueDate = currentState.selectedDate)
                    else -> 
                        currentNewTask
                }
                
                taskRepository.insertTask(taskToSave)
                
                // Reset the newTask field
                _uiState.update {
                    it.copy(newTask = Task(title = ""))
                }
                
                // Refresh the task list
                onLoadTasks()
            }
        }
    }

    private fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            // Refresh the task list
            onLoadTasks()
        }
    }
}