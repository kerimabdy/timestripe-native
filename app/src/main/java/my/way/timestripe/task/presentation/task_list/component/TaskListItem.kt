package my.way.timestripe.task.presentation.task_list.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.ui.theme.TimestripeTheme
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun TaskListItem(
    task: Task,
    onClick: () -> Unit,
    onCheck: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
        ,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CheckmarkIcon(
            checked = task.isCompleted,
            onClick = onCheck,
            modifier = Modifier.size(20.dp)
        )
        val textColor = if (task.isCompleted) {
            TimestripeTheme.colorScheme.labelTertiary
        } else {
            TimestripeTheme.colorScheme.labelPrimary
        }
        Text(
            modifier = Modifier.weight(1f),
            text = task.title,
            style = TimestripeTheme.typography.subheadline,
            color = textColor,
        )
    }
}



@PreviewLightDark
@Composable
private fun TaskListItemPreview() {
    val task = Task(
        id = 1, title = "Task 1", isCompleted = false,
        description = "Description 1",
        dueDate = LocalDate.now(),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    TimestripeTheme {
        var checked by remember { mutableStateOf(true) }
        TaskListItem(
            task = task,
            onClick = {
                checked = !checked
            },
            onCheck = {
                checked = !checked
            },
            onDelete = {}
        )
    }
}