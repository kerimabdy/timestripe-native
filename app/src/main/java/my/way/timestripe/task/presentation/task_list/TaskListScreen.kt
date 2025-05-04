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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.presentation.task_list.component.NewTaskInputItem
import my.way.timestripe.task.presentation.task_list.component.TaskListItem
import my.way.timestripe.task.presentation.task_list.component.TaskNavigationBar
import my.way.timestripe.ui.theme.InterFontFamily
import my.way.timestripe.ui.theme.TimestripeTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    state: TaskListUiState,
    actions: (TaskListActions) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = TimestripeTheme.colorScheme.secondaryBackground,
        contentColor = TimestripeTheme.colorScheme.labelPrimary,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Today",
                                style = TimestripeTheme.typography.body.copy(
                                    fontFamily = InterFontFamily.SemiBold
                                )
                            )
                            Text(
                                text = "Tue, 29",
                                style = TimestripeTheme.typography.body,
                                color = TimestripeTheme.colorScheme.labelSecondary
                            )
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

        },
        bottomBar = {
            TaskNavigationBar(
                selectedMode = state.selectedMode,
                enabledModes = state.enabledModes,
                onModeSelected = { actions(TaskListActions.ChangeMode(it)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    actions(
                        TaskListActions.AddTask(
                            Task(
                                title = "New Task",
                                description = "New Task Description",
                                isCompleted = false,
                                dueDate = LocalDate.now()
                            )
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
    ) { innerPadding ->
        TaskList(
            tasks = state.tasks,
            onTaskClicked = { actions(TaskListActions.OpenTask(it)) },
            onTaskChecked = { actions(TaskListActions.ToggleTaskCompleted(it)) },
            newTask = state.newTask,
            onNewTaskUpdate = { actions(TaskListActions.UpdateNewTask(it)) },
            onNewTaskCheckToggle = { actions(TaskListActions.ToggleNewTaskCompleted) },
            onSaveNewTask = { actions(TaskListActions.SaveNewTask) },
            onDeleteTask = { actions(TaskListActions.DeleteTask(it)) },
            isNewTaskFocused = state.isNewTaskShouldFocus,
            onNewTaskFocusChanged = { actions(TaskListActions.SetNewTaskShouldFocus(it)) },
            modifier = modifier.padding(innerPadding)
        )
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
                            newTask.copy(title = it)
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