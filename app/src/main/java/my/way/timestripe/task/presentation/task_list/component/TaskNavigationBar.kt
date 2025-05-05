package my.way.timestripe.task.presentation.task_list.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import my.way.timestripe.R
import my.way.timestripe.ui.theme.InterFontFamily
import my.way.timestripe.ui.theme.TimestripeTheme

@Composable
fun TaskNavigationBar(
    selectedMode: TaskHorizon,
    enabledModes: Set<TaskHorizon>,
    modifier: Modifier = Modifier,
    onModeSelected: (TaskHorizon) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(
                state = rememberScrollState(),
            )
            .background(
                TimestripeTheme.colorScheme.gray5
            )
            .navigationBarsPadding()
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        enabledModes.forEach { mode ->
            val label = when (mode) {
                TaskHorizon.DAY -> stringResource(R.string.nav_day)
                TaskHorizon.WEEK -> stringResource(R.string.nav_week)
                TaskHorizon.MONTH -> stringResource(R.string.nav_month)
                TaskHorizon.YEAR -> stringResource(R.string.nav_year)
                TaskHorizon.LIFE -> stringResource(R.string.nav_life)
            }

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val alpha by animateFloatAsState(if (isPressed) 0.5f else 1f, label = "nav_alpha")
            Text(
                text = label,
                color = if (mode == selectedMode) TimestripeTheme.colorScheme.labelPrimary else TimestripeTheme.colorScheme.labelTertiary,
//                fontWeight = if (mode == selectedMode) FontWeight.Bold else FontWeight.Normal,
                style = TimestripeTheme.typography.body.copy(
                    fontFamily = if (mode == selectedMode) InterFontFamily.Bold else InterFontFamily.Normal
                ),
                modifier = Modifier
                    .padding(top = 18.dp, bottom = 24.dp)
                    .padding(horizontal = 4.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onModeSelected(mode) },
                        onClickLabel = null,
                    )
                    .graphicsLayer { this.alpha = alpha }
            )
        }
    }
}

@Preview
@Composable
fun TaskNavigationBarPreview() {
    var selectedMode by remember { mutableStateOf(TaskHorizon.DAY) }
    TimestripeTheme {
        TaskNavigationBar(
            selectedMode = selectedMode,
            enabledModes = TaskHorizon.entries.toSet(),
            modifier = Modifier,
            onModeSelected = {
                selectedMode = it
            }
        )
    }
}

enum class TaskHorizon {
    DAY, WEEK, MONTH, YEAR, LIFE
}