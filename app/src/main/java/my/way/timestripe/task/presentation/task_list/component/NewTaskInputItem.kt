package my.way.timestripe.task.presentation.task_list.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.ui.theme.TimestripeTheme
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.launch

@Composable
fun NewTaskInputItem(
    newTask: Task,
    onValueChange: (String) -> Unit,
    onOpenClick: () -> Unit,
    onNewTaskCheckToggle: () -> Unit,
    onSaveNewTask: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()


    BasicTextField(
        value = newTask.title,
        onValueChange = onValueChange,
        modifier = Modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            }
//            .focusRequester(focusRequester)
            .then(modifier),
        textStyle = TimestripeTheme.typography.body
            .copy(
                color = TimestripeTheme.colorScheme.labelPrimary
            ),
        interactionSource = interactionSource,
        singleLine = true,
        cursorBrush = SolidColor(TimestripeTheme.colorScheme.gray),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                if (newTask.title.isNotBlank()) {
                    onSaveNewTask()
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }

                } else {
                    focusManager.clearFocus()
                }
            }
        )
    ) { innerTextField ->
        Row(
            modifier = Modifier
                .background(TimestripeTheme.colorScheme.secondaryBackground)
                .then(
                    if (isFocused) {
                        Modifier
                            .border(
                                width = .75.dp,
                                color = TimestripeTheme.colorScheme.gray5,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .shadowWithClippingShadowLayer(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(14.dp)
                            )
                    } else {
                        Modifier
                    }
                )
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CheckmarkIcon(
                checked = newTask.isCompleted,
                onClick = onNewTaskCheckToggle,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        this.alpha = if (isFocused) 1f else .5f
                    }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                if (isFocused) {
                    innerTextField()
                } else {
                    Text(
                        text = "Add...",
                        style = TimestripeTheme.typography.body,
                        color = TimestripeTheme.colorScheme.labelTertiary
                    )
                }
            }

            if (isFocused) {
                OpenButton(
                    onClick = onOpenClick,
                    modifier = Modifier
                )
            }
        }
    }
}

private fun Modifier.shadowWithClippingShadowLayer(
    elevation: Dp,
    shape: Shape = CircleShape,
    spotColor: Color = DefaultShadowColor.copy(alpha = 0.25f), // More translucent spot shadow
): Modifier = this
    .drawWithCache {
        // Check if elevation is greater than 0
        if (elevation > 0.dp) {
            // Set the blur radius and offsets based on elevation
            val blurRadiusPx = elevation.toPx()  // Blur radius matches the elevation
            val dxPx = 0f                              // No horizontal offset
            val dyPx = (elevation * 0.7f).toPx() // Vertical offset is half the elevation

            // Create outline and path for the shape
            val outline = shape.createOutline(size, layoutDirection, this)
            val path = Path().apply { addOutline(outline) }

            // Create shadow paint with the calculated blur radius and offsets
            val shadowPaint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = false
                    setShadowLayer(blurRadiusPx, dxPx, dyPx, spotColor.toArgb())
                }
            }

            onDrawWithContent {
                // Clip the shadow, draw shadow, and then draw content
                clipPath(path, ClipOp.Difference) {
                    // Draw the shadow outside the shape
                    drawIntoCanvas { canvas ->
                        canvas.drawPath(path, shadowPaint)
                    }
                }

                // Draw the actual content inside the shape
                drawContent()
            }
        } else {
            // If elevation is 0.dp, just draw the content without the shadow
            onDrawWithContent {
                drawContent()
            }
        }
    }

@Composable
private fun OpenButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val alpha by animateFloatAsState(if (isPressed) 0.5f else 1f, label = "nav_alpha")
    Text(
        text = "Open",
        color = TimestripeTheme.colorScheme.labelSecondary,
        style = TimestripeTheme.typography.body,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .graphicsLayer { this.alpha = alpha }
    )
}

@PreviewLightDark
@Composable
private fun NewTaskInputItemPreview() {
    TimestripeTheme {
        Box(
            modifier = Modifier
                .background(TimestripeTheme.colorScheme.secondaryBackground)
                .padding(16.dp)
        ) {
            var newTask by remember {
                mutableStateOf(
                    Task(
                        title = "",
                    )
                )
            }

            NewTaskInputItem(
                newTask = newTask,
                onValueChange = { newTask = newTask.copy(title = it) },
                onOpenClick = { },
                onSaveNewTask = {},
                onNewTaskCheckToggle = { },
            )
        }
    }
}
