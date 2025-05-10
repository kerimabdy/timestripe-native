package my.way.timestripe.task.presentation.task_list

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.presentation.task_list.component.CalendarPager
import my.way.timestripe.task.presentation.task_list.component.NewTaskInputItem
import my.way.timestripe.task.presentation.task_list.component.TaskListItem
import my.way.timestripe.task.presentation.task_list.component.TaskNavigationBar
import my.way.timestripe.task.presentation.util.LocalAnimatedVisibilityScope
import my.way.timestripe.task.presentation.util.LocalSharedTransitionScope
import my.way.timestripe.ui.theme.InterFontFamily
import my.way.timestripe.ui.theme.TimestripeTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale


// Column constants
private const val COLUMN_DAY = 1
private const val COLUMN_WEEK = 2
private const val COLUMN_MONTH = 3
private const val COLUMN_YEAR = 4
private const val COLUMN_LIFE = 5

private const val VIRTUAL_PAGE_COUNT = Int.MAX_VALUE
private const val START_INDEX = VIRTUAL_PAGE_COUNT / 2

fun getDateForPage(page: Int, startIndex: Int, referenceDate: LocalDate, column: Int): LocalDate {
    // Calculate the difference in days between the current page and the start index
    val dayOffset = when (column) {
        COLUMN_DAY -> page - startIndex
        COLUMN_WEEK -> (page - startIndex) * 7 // Multiply by 7 to get week offset in days
        COLUMN_MONTH -> (page - startIndex) * 30 // Multiply by 30 to get month offset in days
        COLUMN_YEAR -> (page - startIndex) * 365 // Multiply by 365 to get year offset in days
        else -> 0
    }


    // Add the offset to the reference date (today)
    return referenceDate.plusDays(dayOffset.toLong())
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TaskListScreen(
    state: TaskListUiState,
    modifier: Modifier = Modifier,
    onAction: (TaskListActions) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {

    CompositionLocalProvider(
        LocalSharedTransitionScope provides sharedTransitionScope,
        LocalAnimatedVisibilityScope provides animatedContentScope
    ) {
        Scaffold(
        modifier = modifier,
        containerColor = TimestripeTheme.colorScheme.secondaryBackground,
        contentColor = TimestripeTheme.colorScheme.labelPrimary,
        topBar = {
            TaskListAppBar(state.selectedDate, state.selectedColumn)
        },
        bottomBar = {
            val coroutineScope = rememberCoroutineScope()
            TaskNavigationBar(
                selectedColumn = state.selectedColumn,
                enabledColumns = state.enabledColumns,
                onColumnSelected = {
                    coroutineScope.launch {
                        onAction(TaskListActions.ChangeColumn(it))
                    }
                }
            )
        },
        floatingActionButton = {
            AddTaskFloatingActionButton(
                state.selectedDate,
                { onAction(TaskListActions.AddTask(it)) })
        }
    ) { innerPadding ->
        // Horizontal pager for date navigation
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.selectedColumn == COLUMN_LIFE) {
                // For LIFE view, just show tasks without paging
                TaskList(
                    tasks = state.tasksForLife,
                    newTask = state.newTask,
                    onTaskClicked = { onAction(TaskListActions.NavigateToTaskDetail(it.id)) },
                    onTaskChecked = { onAction(TaskListActions.ToggleTaskCompleted(it)) },
                    onNewTaskUpdate = { onAction(TaskListActions.UpdateNewTask(it)) },
                    onNewTaskCheckToggle = { onAction(TaskListActions.ToggleNewTaskCompleted) },
                    onSaveNewTask = { onAction(TaskListActions.SaveNewTask) },
                    onDeleteTask = { onAction(TaskListActions.DeleteTask(it)) },
                    isNewTaskFocused = state.isNewTaskShouldFocus,
                    onNewTaskFocusChanged = { onAction(TaskListActions.SetNewTaskShouldFocus(it)) },
                    selectedDate = state.selectedDate,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // For time-based columns, use horizontal pager

                val getNextDate = when (state.selectedColumn) {
                    COLUMN_DAY -> { date: LocalDate -> date.plusDays(1) }
                    COLUMN_WEEK -> { date: LocalDate -> date.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) }
                    COLUMN_MONTH -> { date: LocalDate -> date.plusMonths(1).withDayOfMonth(1) }
                    COLUMN_YEAR -> { date: LocalDate -> date.plusYears(1).withDayOfYear(1) }
                    else -> { date: LocalDate -> date }
                }
                val getPreviousDate = when (state.selectedColumn) {
                    COLUMN_DAY -> { date: LocalDate -> date.minusDays(1) }
                    COLUMN_WEEK -> { date: LocalDate  -> date.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) }
                    COLUMN_MONTH -> { date: LocalDate -> date.minusMonths(1).withDayOfMonth(1) }
                    COLUMN_YEAR -> { date: LocalDate -> date.minusYears(1).withDayOfYear(1) }
                    else -> { date: LocalDate -> date }
                }

                CalendarPager(
                    currentDate = state.selectedDate,
                    getNextDate = getNextDate,
                    getPreviousDate = getPreviousDate,
                    onPageChanged = { onAction(TaskListActions.SetSelectedDate(it)) },
                    itemContent = { date ->
                        val normalizedDate = normalizeDate(date, state.selectedColumn)
                        onAction(TaskListActions.LoadTasksForDate(normalizedDate))
                        val tasks = when (state.selectedColumn) {
                            COLUMN_DAY -> state.tasksForDay[normalizedDate] ?: emptyList()
                            COLUMN_WEEK -> state.tasksForWeek[normalizedDate] ?: emptyList()
                            COLUMN_MONTH -> state.tasksForMonth[normalizedDate] ?: emptyList()
                            COLUMN_YEAR -> state.tasksForYear[normalizedDate] ?: emptyList()
                            else -> emptyList()
                        }
                        TaskList(
                            tasks = tasks,
                            newTask = state.newTask,
                            onTaskClicked = { onAction(TaskListActions.NavigateToTaskDetail(it.id)) },
                            onTaskChecked = { onAction(TaskListActions.ToggleTaskCompleted(it)) },
                            onNewTaskUpdate = { onAction(TaskListActions.UpdateNewTask(it)) },
                            onNewTaskCheckToggle = { onAction(TaskListActions.ToggleNewTaskCompleted) },
                            onSaveNewTask = { onAction(TaskListActions.SaveNewTask) },
                            onDeleteTask = { onAction(TaskListActions.DeleteTask(it)) },
                            isNewTaskFocused = state.isNewTaskShouldFocus,
                            onNewTaskFocusChanged = {
                                onAction(
                                    TaskListActions.SetNewTaskShouldFocus(
                                        it
                                    )
                                )
                            },
                            selectedDate = date,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
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
    selectedColumn: Int
) {
    val formattedDate by remember(selectedDate, selectedColumn) {
        derivedStateOf {
            getFormattedDateForDisplay(selectedDate, selectedColumn)
        }
    }

    Column {
        CenterAlignedTopAppBar(
            title = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Primary date format based on column type
                    Text(
                        text = formattedDate.first,
                        style = TimestripeTheme.typography.body.copy(
                            fontFamily = InterFontFamily.Medium
                        ),
                        color = TimestripeTheme.colorScheme.labelPrimary
                    )

                    // Secondary date format if applicable
                    if (formattedDate.second.isNotEmpty()) {
                        Text(
                            text = formattedDate.second,
                            style = TimestripeTheme.typography.body.copy(
                                fontFamily = InterFontFamily.Medium
                            ),
                            color = TimestripeTheme.colorScheme.labelSecondary
                        )
                    }
                }
            },
            colors = TopAppBarColors(
                containerColor = TimestripeTheme.colorScheme.secondaryBackground,
                scrolledContainerColor = TimestripeTheme.colorScheme.secondaryBackground,
                navigationIconContentColor = TimestripeTheme.colorScheme.labelPrimary,
                titleContentColor = TimestripeTheme.colorScheme.labelPrimary,
                actionIconContentColor = TimestripeTheme.colorScheme.labelPrimary
            )
        )
        HorizontalDivider(
            color = TimestripeTheme.colorScheme.gray5
        )
    }
}

/**
 * Returns a formatted date string based on the column type
 */
fun getFormattedDateForDisplay(date: LocalDate?, column: Int): Pair<String, String> {
    // For LIFE or null date, return Life
    if (column == COLUMN_LIFE || date == null) {
        return Pair("Life", "")
    }

    return when (column) {
        COLUMN_DAY -> {
            val isToday = date.isEqual(LocalDate.now())
            val dayOfWeek = if (isToday) "Today" else date.format(DateTimeFormatter.ofPattern("EEEE"))
            val dateFormat = if (isToday) {
                date.format(DateTimeFormatter.ofPattern("EEE, d"))
            } else {
                date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
            }
            Pair(dayOfWeek, dateFormat)
        }

        COLUMN_WEEK -> {
            val weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val weekEnd = weekStart.plusDays(6)

            val startMonth = weekStart.format(DateTimeFormatter.ofPattern("MMM"))
            val endMonth = weekEnd.format(DateTimeFormatter.ofPattern("MMM"))

            val weekFormatted = if (startMonth == endMonth) {
                "$startMonth ${weekStart.dayOfMonth} - ${weekEnd.dayOfMonth}, ${weekStart.year}"
            } else {
                "$startMonth ${weekStart.dayOfMonth} - $endMonth ${weekEnd.dayOfMonth}, ${weekStart.year}"
            }
            val weekNumber = date.get(WeekFields.of(Locale.getDefault()).weekOfYear())
            Pair("Week $weekNumber", weekFormatted)
        }

        COLUMN_MONTH -> {
            val monthYear = date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
            Pair(monthYear, "")
        }

        COLUMN_YEAR -> {
            val year = date.format(DateTimeFormatter.ofPattern("yyyy"))
            Pair(year, "")
        }

        else -> Pair("Life", "") // Fallback that should not be reached
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
            items(tasks, key = { task -> task.id }) { task ->
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

private fun normalizeDate(date: LocalDate, column: Int): LocalDate {
    return when (column) {
        COLUMN_DAY -> date
        COLUMN_WEEK -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        COLUMN_MONTH -> date.withDayOfMonth(1)
        COLUMN_YEAR -> date.withDayOfYear(1)
        else -> date
    }
}