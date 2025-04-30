package my.way.timestripe.task.presentation.task_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.presentation.task_list.component.TaskListItem
import my.way.timestripe.task.presentation.task_list.component.TaskNavigationBar
import my.way.timestripe.ui.theme.InterFontFamily
import my.way.timestripe.ui.theme.TimestripeTheme

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
        }
    ) { innerPadding ->
        TaskList(
            tasks = state.tasks,
            onTaskClicked = { actions(TaskListActions.OpenTask(it)) },
            onTaskChecked = { actions(TaskListActions.ToggleTaskCompleted(it)) },
            modifier = modifier.padding(innerPadding)
        )
    }
}


@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskClicked: (Task) -> Unit,
    onTaskChecked: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(tasks) { task ->
            TaskListItem(
                task = task,
                onClick = { onTaskClicked(task) },
                onCheck = { onTaskChecked(task) },
                modifier = Modifier
            )
        }
    }
}