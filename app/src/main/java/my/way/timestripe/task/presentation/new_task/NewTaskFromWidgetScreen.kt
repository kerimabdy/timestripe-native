package my.way.timestripe.task.presentation.new_task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import my.way.timestripe.R
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.presentation.task_detail.component.CardIconButton
import my.way.timestripe.task.presentation.task_list.component.CheckmarkIcon
import my.way.timestripe.ui.theme.InterFontFamily
import my.way.timestripe.ui.theme.TimestripeTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskFromWidgetScreen(
    state: NewTaskUiState,
    onAction: (NewTaskAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = { onAction(NewTaskAction.DismissRequested) },
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize(),
        containerColor = Color.Transparent,
        sheetState = sheetState,
    ) {
        NewTaskSheetContent(
            task = state.task,
            onTaskTitleChanged = { onAction(NewTaskAction.TaskTitleChanged(it)) },
            onTaskDescriptionChanged = { onAction(NewTaskAction.TaskDescriptionChanged(it)) },
            onTaskCompletedToggle = { onAction(NewTaskAction.TaskCompletedToggle) },
            onTaskDueDateChanged = { onAction(NewTaskAction.TaskDueDateChanged(it)) },
            modifier = Modifier
        )
    }

}


@Composable
private fun NewTaskSheetContent(
    task: Task,
    onTaskTitleChanged: (String) -> Unit,
    onTaskDescriptionChanged: (String) -> Unit,
    onTaskCompletedToggle: () -> Unit,
    onTaskDueDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    // focus on title field
    val titleFocusRequester = remember { FocusRequester() }

    LaunchedEffect(titleFocusRequester) {
        titleFocusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .background(
                TimestripeTheme.colorScheme.secondaryBackground,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )

    ) {
        val date = task.dueDate?.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
        Text(
            text = date ?: "",
            style = TimestripeTheme.typography.subheadline.copy(
                fontFamily = InterFontFamily.SemiBold,
                color = TimestripeTheme.colorScheme.orange
            ),
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CheckmarkIcon(
                checked = task.isCompleted,
                onClick = { onTaskCompletedToggle() },
                modifier = Modifier
                    .padding(6.dp)
                    .size(22.dp)
            )

            BasicTextField(
                modifier = Modifier
                    .focusRequester(titleFocusRequester),
                value = task.title,
                onValueChange = { onTaskTitleChanged(it) },
                textStyle = TimestripeTheme.typography.title1.copy(
                    fontFamily = InterFontFamily.Bold,
                    color = TimestripeTheme.colorScheme.labelPrimary
                ),
                cursorBrush = SolidColor(TimestripeTheme.colorScheme.gray3),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                    ) {
                        if (task.title.isNullOrEmpty()) {
                            Text(
                                text = "Add a title",
                                style = TimestripeTheme.typography.title1.copy(
                                    fontFamily = InterFontFamily.Bold,
                                    color = TimestripeTheme.colorScheme.labelTertiary
                                )
                            )
                        } else {
                            innerTextField()
                        }
                    }
                }
            )
        }
        BasicTextField(
            value = task.description ?: "",
            onValueChange = { onTaskDescriptionChanged(it) },
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            textStyle = TimestripeTheme.typography.subheadline.copy(
                color = TimestripeTheme.colorScheme.labelPrimary
            ),
            cursorBrush = SolidColor(TimestripeTheme.colorScheme.gray3),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (task.description.isNullOrEmpty()) {
                        Text(
                            text = "Add a description",
                            style = TimestripeTheme.typography.subheadline.copy(
                                color = TimestripeTheme.colorScheme.labelTertiary
                            )
                        )
                    } else {
                        innerTextField()
                    }
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .padding(end = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CardIconButton(
                    onClick = {},
                ) {
                    Icon(
                        painter = painterResource(R.drawable.goal_events),
                        contentDescription = "Delete task",
                        tint = TimestripeTheme.colorScheme.labelTertiary
                    )
                }


                CardIconButton(
                    onClick = {},
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_circle),
                        contentDescription = "Delete task",
                        tint = TimestripeTheme.colorScheme.labelTertiary
                    )
                }

                CardIconButton(
                    onClick = {},
                ) {
                    Icon(
                        painter = painterResource(R.drawable.attachment),
                        contentDescription = "Delete task",
                        tint = TimestripeTheme.colorScheme.labelTertiary
                    )
                }

                CardIconButton(
                    onClick = {},
                ) {
                    Icon(
                        painter = painterResource(R.drawable.tag),
                        contentDescription = "Delete task",
                        tint = TimestripeTheme.colorScheme.labelTertiary
                    )
                }

                CardIconButton(
                    onClick = {},
                ) {
                    Icon(
                        painter = painterResource(R.drawable.comment),
                        contentDescription = "Delete task",
                        tint = TimestripeTheme.colorScheme.labelTertiary
                    )
                }
                CardIconButton(
                    onClick = {},
                ) {
                    Icon(
                        painter = painterResource(R.drawable.assign),
                        contentDescription = "Delete task",
                        tint = TimestripeTheme.colorScheme.labelTertiary
                    )
                }
            }

            CardIconButton(
                onClick = {},
                Modifier
                    .background(
                        TimestripeTheme.colorScheme.fillSecondary,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.more),
                    contentDescription = "Delete task",
                    tint = TimestripeTheme.colorScheme.labelPrimary
                )
            }
        }
    }
}