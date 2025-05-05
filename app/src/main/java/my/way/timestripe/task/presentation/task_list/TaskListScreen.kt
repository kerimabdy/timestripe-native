package my.way.timestripe.task.presentation.task_list

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.presentation.task_list.component.NewTaskInputItem
import my.way.timestripe.task.presentation.task_list.component.TaskHorizon
import my.way.timestripe.task.presentation.task_list.component.TaskListItem
import my.way.timestripe.task.presentation.task_list.component.TaskNavigationBar
import my.way.timestripe.ui.theme.InterFontFamily
import my.way.timestripe.ui.theme.TimestripeTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    state: TaskListUiState,
    actions: (TaskListActions) -> Unit,
    modifier: Modifier = Modifier
) {
    // If date range is empty, initialize the pager
    LaunchedEffect(state.visibleDateRange.isEmpty()) {
        if (state.visibleDateRange.isEmpty()) {
            actions(TaskListActions.InitializePager())
        }
    }
    
    // Create pager state or exit early if we don't have dates yet
    val visibleDates = state.visibleDateRange
    if (visibleDates.isEmpty()) return
    
    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { visibleDates.size }
    )
    
    // Track page changes and update the view model
    val coroutineScope = rememberCoroutineScope()
    
    // Sync pager with view model
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { page ->
                if (page != state.currentPage) {
                    actions(TaskListActions.PageChanged(page))
                }
            }
    }
    
    // If we need to regenerate pages, do it
    LaunchedEffect(state.shouldRegeneratePages) {
        if (state.shouldRegeneratePages) {
            actions(TaskListActions.RegeneratePages)
        }
    }
//
//    // When visible date range changes, update pager position
//    LaunchedEffect(state.visibleDateRange, state.currentPage) {
//        if (pagerState.currentPage != state.currentPage) {
//            pagerState.scrollToPage(state.currentPage)
//        }
//    }
//
//    // When selected horizon changes, ensure pager is initialized
//    LaunchedEffect(state.selectedHorizon) {
//        actions(TaskListActions.InitializePager())
//    }
    
    // Format dates for display
    val formattedDate by remember(state.selectedDate, state.selectedHorizon) {
        derivedStateOf {
            getFormattedDateForDisplay(state.selectedDate, state.selectedHorizon)
        }
    }

    val selectedDate = state.selectedDate ?: LocalDate.now()
    
    Scaffold(
        containerColor = TimestripeTheme.colorScheme.secondaryBackground,
        contentColor = TimestripeTheme.colorScheme.labelPrimary,
        topBar =  {
            TaskListAppBar(selectedDate, state.selectedHorizon)
        },
        bottomBar = {
            TaskNavigationBar(
                selectedMode = state.selectedHorizon,
                enabledModes = state.enabledHorizons,
                onModeSelected = { actions(TaskListActions.ChangeHorizon(it)) }
            )
        },
        floatingActionButton = {
            AddTaskFloatingActionButton(selectedDate, { actions(TaskListActions.AddTask(it)) })
        }
    ) { innerPadding ->
        // Horizontal pager for date navigation
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.selectedHorizon == TaskHorizon.LIFE) {
                // For LIFE view, just show tasks without paging
                TaskList(
                    tasks = state.tasks,
                    newTask = state.newTask,
                    onTaskClicked = { actions(TaskListActions.OpenTask(it)) },
                    onTaskChecked = { actions(TaskListActions.ToggleTaskCompleted(it)) },
                    onNewTaskUpdate = { actions(TaskListActions.UpdateNewTask(it)) },
                    onNewTaskCheckToggle = { actions(TaskListActions.ToggleNewTaskCompleted) },
                    onSaveNewTask = { actions(TaskListActions.SaveNewTask) },
                    onDeleteTask = { actions(TaskListActions.DeleteTask(it)) },
                    isNewTaskFocused = state.isNewTaskShouldFocus,
                    onNewTaskFocusChanged = { actions(TaskListActions.SetNewTaskShouldFocus(it)) },
                    selectedDate = state.selectedDate,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // For time-based horizons, use horizontal pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    // Show task list for the current page
                    TaskList(
                        tasks = state.tasks,
                        newTask = state.newTask,
                        onTaskClicked = { actions(TaskListActions.OpenTask(it)) },
                        onTaskChecked = { actions(TaskListActions.ToggleTaskCompleted(it)) },
                        onNewTaskUpdate = { actions(TaskListActions.UpdateNewTask(it)) },
                        onNewTaskCheckToggle = { actions(TaskListActions.ToggleNewTaskCompleted) },
                        onSaveNewTask = { actions(TaskListActions.SaveNewTask) },
                        onDeleteTask = { actions(TaskListActions.DeleteTask(it)) },
                        isNewTaskFocused = state.isNewTaskShouldFocus,
                        onNewTaskFocusChanged = { actions(TaskListActions.SetNewTaskShouldFocus(it)) },
                        selectedDate = state.selectedDate,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskListAppBar(
    selectedDate: LocalDate?,
    selectedHorizon: TaskHorizon
) {
    val formattedDate by remember(selectedDate, selectedHorizon) {
        derivedStateOf {
            getFormattedDateForDisplay(selectedDate, selectedHorizon)
        }
    }
    
    Column {
        CenterAlignedTopAppBar(
            title = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Primary date format based on horizon
                    Text(
                        text = formattedDate.first,
                        style = TimestripeTheme.typography.body.copy(
                            fontFamily = InterFontFamily.SemiBold
                        )
                    )
                    
                    // Secondary date info if applicable
                    if (formattedDate.second.isNotEmpty()) {
                        Text(
                            text = formattedDate.second,
                            style = TimestripeTheme.typography.body,
                            color = TimestripeTheme.colorScheme.labelSecondary
                        )
                    }
                }
            },
            colors = TopAppBarColors(
                containerColor = TimestripeTheme.colorScheme.secondaryBackground,
                scrolledContainerColor = TimestripeTheme.colorScheme.secondaryBackground,
                navigationIconContentColor = TimestripeTheme.colorScheme.labelSecondary,
                titleContentColor = TimestripeTheme.colorScheme.labelPrimary,
                actionIconContentColor = TimestripeTheme.colorScheme.labelSecondary,
            ),
        )

        HorizontalDivider(
            thickness = .33.dp,
            color = TimestripeTheme.colorScheme.separatorOpaque
        )
    }
}

// Regular function (not composable) to format dates based on the selected horizon
private fun getFormattedDateForDisplay(date: LocalDate?, horizon: TaskHorizon): Pair<String, String> {
    // For LIFE or null date, return placeholder
    if (horizon == TaskHorizon.LIFE || date == null) {
        return Pair("Life", "")
    }
    
    return when (horizon) {
        TaskHorizon.DAY -> {
            val primaryFormat = DateTimeFormatter.ofPattern("EEEE")
            val secondaryFormat = DateTimeFormatter.ofPattern("MMM d")
            Pair(
                date.format(primaryFormat),
                date.format(secondaryFormat)
            )
        }
        TaskHorizon.WEEK -> {
            val weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val weekEnd = weekStart.plusDays(6)
            val primaryFormat = DateTimeFormatter.ofPattern("'Week' w")
            val secondaryStartFormat = DateTimeFormatter.ofPattern("MMM d")
            val secondaryEndFormat = DateTimeFormatter.ofPattern("MMM d")
            Pair(
                date.format(primaryFormat),
                "${weekStart.format(secondaryStartFormat)} - ${weekEnd.format(secondaryEndFormat)}"
            )
        }
        TaskHorizon.MONTH -> {
            val primaryFormat = DateTimeFormatter.ofPattern("MMMM")
            val secondaryFormat = DateTimeFormatter.ofPattern("yyyy")
            Pair(
                date.format(primaryFormat),
                date.format(secondaryFormat)
            )
        }
        TaskHorizon.YEAR -> {
            val primaryFormat = DateTimeFormatter.ofPattern("yyyy")
            Pair(
                date.format(primaryFormat),
                ""
            )
        }
        TaskHorizon.LIFE -> Pair("Life", "") // Fallback that should not be reached
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    newTask: Task,
    onNewTaskUpdate: (Task) -> Unit,
    onNewTaskCheckToggle: () -> Unit,
    onTaskClicked: (Task) -> Unit,
    onTaskChecked: (Task) -> Unit,
    onSaveNewTask: () -> Unit,
    onDeleteTask: (Task) -> Unit,
    isNewTaskFocused: Boolean,
    onNewTaskFocusChanged: (Boolean) -> Unit,
    selectedDate: LocalDate?,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onSaveNewTask()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onNewTaskFocusChanged(false)
                    }
                )
            }
    ) {
        LazyColumn(
            modifier = Modifier,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(tasks) { task ->
                TaskListItem(
                    task = task,
                    onClick = { onTaskClicked(task) },
                    onCheck = { onTaskChecked(task) },
                    onDelete = { onDeleteTask(task) },
                    modifier = Modifier
                )
            }

            item {
                NewTaskInputItem(
                    newTask = newTask,
                    shouldFocus = isNewTaskFocused,
                    onValueChange = {
                        onNewTaskUpdate(
                            newTask.copy(title = it, dueDate = selectedDate)
                        )
                    },
                    onOpenClick = { },
                    onSaveNewTask = onSaveNewTask,
                    onNewTaskCheckToggle = { onNewTaskCheckToggle() },
                    onShouldFocusChanged = onNewTaskFocusChanged,
                )
            }
        }
    }
}

@Composable
private fun AddTaskFloatingActionButton(
    selectedDate: LocalDate?,
    onAddTask: (Task) -> Unit
) {
    FloatingActionButton(
        onClick = {
            onAddTask(
                Task(
                    title = "New Task",
                    description = "New Task Description",
                    isCompleted = false,
                    dueDate = selectedDate
                )
            )
        }
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Task"
        )
    }
}