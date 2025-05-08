package my.way.timestripe.task.presentation.task_detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import my.way.timestripe.R
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.presentation.task_list.component.CheckmarkIcon
import my.way.timestripe.ui.theme.InterFontFamily
import my.way.timestripe.ui.theme.TimestripeTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TaskCard(
    task: Task,
    onTaskTitleChanged: (String) -> Unit,
    onTaskDescriptionChanged: (String) -> Unit,
    onTaskCompletedToggle: () -> Unit,
    onTaskDueDateChanged: (LocalDate) -> Unit,
    onTaskDeleted: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    TimestripeTheme.colorScheme.gray6,
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 2.dp,
                        bottomEnd = 2.dp
                    )
                )
        ) {
            val date = task.dueDate?.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
            Text(
                text = date ?: "",
                style = TimestripeTheme.typography.subheadline.copy(
                    fontFamily = InterFontFamily.SemiBold,
                    color = TimestripeTheme.colorScheme.orange
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
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
                    value = task.title,
                    onValueChange = { onTaskTitleChanged(it) },
                    textStyle = TimestripeTheme.typography.title1.copy(
                        fontFamily = InterFontFamily.Bold,
                        color = TimestripeTheme.colorScheme.labelPrimary
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                        ) {
                            innerTextField()
                        }
                    }
                )
            }
            BasicTextField(
                value = task.description ?: "",
                onValueChange = { onTaskDescriptionChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp),
                textStyle = TimestripeTheme.typography.subheadline.copy(
                    color = TimestripeTheme.colorScheme.labelPrimary
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        innerTextField()
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = 2.dp,
                        topEnd = 2.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp
                    )
                )
                .background(
                    TimestripeTheme.colorScheme.gray6,
                )
                .padding(4.dp)
            ,
        ) {

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart),
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
                modifier
                    .background(
                        TimestripeTheme.colorScheme.fillSecondary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .align(Alignment.CenterEnd)
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

@Preview
@Composable
private fun TaskCardPreview() {
    TimestripeTheme {
        var task by remember {
            mutableStateOf(
                Task(
                    title = "Task 1",
                    description = "Description 1",
                    isCompleted = false,
                    dueDate = LocalDate.now()
                )
            )
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            TaskCard(
                task = task,
                onTaskTitleChanged = { task = task.copy(title = it) },
                onTaskDescriptionChanged = { task = task.copy(description = it) },
                onTaskCompletedToggle = { task = task.copy(isCompleted = !task.isCompleted) },
                onTaskDueDateChanged = { task = task.copy(dueDate = it) },
                onTaskDeleted = {},
            )
        }
    }
}