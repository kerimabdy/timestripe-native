package my.way.timestripe.task.presentation.task_list.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import my.way.timestripe.R
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.ui.theme.TimestripeTheme
import java.time.LocalDate
import kotlin.Boolean

@Composable
fun TaskListItem(
    task: Task,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCheck: () -> Unit,
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
            modifier = Modifier
        )
        val textColor = if (task.isCompleted) {
            TimestripeTheme.colorScheme.labelPrimary
        } else {
            TimestripeTheme.colorScheme.labelTertiary
        }
        Text(
            modifier = Modifier.weight(1f),
            text = task.title,
            style = TimestripeTheme.typography.subheadline,
            color = textColor,
        )
    }
}

@Composable
private fun CheckmarkIcon(
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val backgroundColor by animateColorAsState(
        targetValue =
            if (checked) TimestripeTheme.colorScheme.gray
            else TimestripeTheme.colorScheme.gray.copy(
                alpha = 0f
            ),
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "clipAnimationValue"
    )

    Box(
        modifier
            .clip(RoundedCornerShape(6.dp))
            .size(20.dp)
            .border(
                1.dp,
                TimestripeTheme.colorScheme.gray,
                RoundedCornerShape(6.dp)
            )
            .drawBehind {
                drawRect(
                    color = backgroundColor,
                    size = Size(size.width, size.height)
                )
            }
            .clickable(onClick = onClick)
    ) {
        val clipAnimationValue by animateFloatAsState(
            targetValue = if (checked) 1f else 0f,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "clipAnimationValue"
        )
        Icon(
            painter = painterResource(R.drawable.checkmark),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .clip(
                    value = clipAnimationValue,
                ),
            tint = TimestripeTheme.colorScheme.secondaryBackground,
        )

    }

}

private fun Modifier.clip(
    value: Float,
) = graphicsLayer {
    clip = true
    shape = object : Shape {
        override fun createOutline(
            size: Size, layoutDirection: LayoutDirection, density: Density
        ) = Outline.Rectangle(
            Rect(
                offset = Offset(size.width * (value - 1f), 0f),
                size = Size(size.width, size.height)
            )
        )
    }
}




@PreviewLightDark
@Composable
private fun TaskListItemPreview() {
    val task = Task(
        id = "1", title = "Task 1", isCompleted = false,
        date = LocalDate.now()
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
        )
    }
}