package my.way.timestripe.task.presentation.task_detail.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CardIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val opacity = if (isPressed.value) 0.5f else 1f
    val scale = if (isPressed.value) 0.95f else 1f
    val animatedScale = animateFloatAsState(targetValue = scale)
    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = opacity
                scaleX = animatedScale.value
                scaleY = animatedScale.value
            }
            .then(
            modifier
                .padding(2.dp)
                .size(32.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
        )
            ,
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}