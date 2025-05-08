package my.way.timestripe.task.presentation.task_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import my.way.timestripe.task.presentation.task_detail.component.TaskCard

@Composable
fun TaskDetailScreen(
    state: TaskDetailUiState,
    onAction: (TaskDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
           .padding(bottom = 64.dp)
           .statusBarsPadding()
           .navigationBarsPadding()
            .fillMaxSize()
    ) {
        TaskCard(
            modifier = Modifier,
            task = state.task,
            onTaskTitleChanged = { onAction(TaskDetailAction.ChangeTaskTitle(it)) },
            onTaskDescriptionChanged = { onAction(TaskDetailAction.ChangeTaskDescription(it)) },
            onTaskCompletedToggle = { onAction(TaskDetailAction.ToggleTaskCompleted) },
            onTaskDueDateChanged = { onAction(TaskDetailAction.ChangeTaskDueDate(it)) },
            onTaskDeleted = { onAction(TaskDetailAction.DeleteTask) },
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            onAction(TaskDetailAction.SaveTask)
        }
    }
}