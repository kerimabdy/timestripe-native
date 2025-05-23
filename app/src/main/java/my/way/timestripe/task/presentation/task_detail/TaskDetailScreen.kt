package my.way.timestripe.task.presentation.task_detail

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import my.way.timestripe.task.presentation.task_detail.component.TaskCard
import my.way.timestripe.task.presentation.util.LocalAnimatedVisibilityScope
import my.way.timestripe.task.presentation.util.LocalSharedTransitionScope
import my.way.timestripe.ui.theme.TimestripeTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TaskDetailScreen(
    state: TaskDetailUiState,
    onAction: (TaskDetailAction) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    CompositionLocalProvider(
        LocalSharedTransitionScope provides sharedTransitionScope,
        LocalAnimatedVisibilityScope provides animatedContentScope
    ) {
        Column(
            modifier = modifier
                .background(TimestripeTheme.colorScheme.overlayDefault)
                .padding(16.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize()
                .padding(bottom = 64.dp)
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
    }

    DisposableEffect(Unit) {
        onDispose {
            onAction(TaskDetailAction.SaveTask)
        }
    }
}