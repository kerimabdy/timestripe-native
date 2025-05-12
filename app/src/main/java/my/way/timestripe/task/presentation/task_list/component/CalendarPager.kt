package my.way.timestripe.task.presentation.task_list.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import my.way.timestripe.ui.theme.TimestripeTheme
import java.time.LocalDate
import kotlin.math.abs

/**
 * A horizontal pager that displays LocalDate items, using a filmstrip model.
 *
 * @param modifier Modifier for this composable.
 * @param currentDate The current date to be displayed. This is the source of truth from the parent.
 * @param onPageChanged A callback invoked when the settled page changes due to a swipe.
 * @param itemContent The composable content for each page (LocalDate).
 */
@Composable
fun CalendarPager(
    modifier: Modifier = Modifier,
    currentDate: LocalDate, // Source of truth from parent
    getNextDate: (currentDate: LocalDate) -> LocalDate,
    getPreviousDate: (currentDate: LocalDate) -> LocalDate,
    onPageChanged: (newDate: LocalDate) -> Unit,
    itemContent: @Composable (pageData: LocalDate) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var internalSettledDate by remember { mutableStateOf(currentDate) }
    val offsetX = remember { Animatable(0f) } // Offset of the "camera" over the filmstrip
    var pageWidthPx by remember { mutableStateOf(0f) }
    var pagesInStrip by remember { mutableStateOf(listOf(currentDate)) }

    // Effect for handling programmatic changes to `currentDate` prop
    LaunchedEffect(currentDate, pageWidthPx) {
        if (pageWidthPx == 0f) return@LaunchedEffect

        if (currentDate == internalSettledDate) {
            if (pagesInStrip.size > 1 || offsetX.value != 0f) {
                if (!offsetX.isRunning) {
                    offsetX.snapTo(0f)
                    pagesInStrip = listOf(internalSettledDate)
                }
            }
            return@LaunchedEffect
        }

        offsetX.stop()

        val animationSpec = tween<Float>(durationMillis = 300)
        val targetDate = currentDate

        if (targetDate.isAfter(internalSettledDate)) {
            pagesInStrip = pagesInStrip + targetDate
            val targetOffsetX = -(pagesInStrip.size - 1) * pageWidthPx
            offsetX.animateTo(targetOffsetX, animationSpec)
        } else { // targetDate isBefore(internalSettledDate)
            // When prepending, the visual anchor is the page that *was* current (internalSettledDate).
            // It will shift to index 1 in the new pagesInStrip.
            pagesInStrip = listOf(targetDate) + pagesInStrip.toList()
            // To keep internalSettledDate (now at strip index 1) visually centered *before* animation,
            // the camera (offsetX) needs to point to its new position in the strip.
            // Its new position is 1 * pageWidthPx. So camera snaps to -(1 * pageWidthPx).
            offsetX.snapTo(-pageWidthPx)
            // Now, animate the camera to 0f to bring the new targetDate (at strip index 0) to the center.
            offsetX.animateTo(0f, animationSpec)
        }

        if (!offsetX.isRunning) {
            internalSettledDate = targetDate
            pagesInStrip = listOf(internalSettledDate)
            offsetX.snapTo(0f)
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth() // This Box measures the width
    ) {
        this
        val newWidth = constraints.maxWidth.toFloat()
        if (newWidth > 0f && newWidth != pageWidthPx) {
            pageWidthPx = newWidth
        }

        if (pageWidthPx == 0f) {
            // Avoid rendering or gestures if width is not determined
            return@BoxWithConstraints
        }

        // This inner Box handles gestures and contains the moving filmstrip
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(
                    internalSettledDate,
                    pageWidthPx,
                    currentDate
                ) { // Key gestures appropriately
                    detectDragGestures(
                        onDragStart = {
                            coroutineScope.launch {
                                offsetX.stop() // Stop any ongoing programmatic animation
                                pagesInStrip = listOf(
                                    getPreviousDate(internalSettledDate),
                                    internalSettledDate,
                                    getNextDate(internalSettledDate)
                                )
                                // internalSettledDate is at index 1 of the new 3-page strip.
                                // To keep it visually centered, camera (offsetX) must point to it.
                                // Position of item at index 1 in strip is (1 * pageWidthPx).
                                // Camera offset for this: -1 * pageWidthPx.
                                offsetX.snapTo(-pageWidthPx)
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount.x)
                            }
                        },
                        onDragEnd = {
                            val currentCameraOffset = offsetX.value
                            // Relative to the center of the 3-page strip (which is at -pageWidthPx for the camera)
                            val offsetFromCenterOfStrip = currentCameraOffset - (-pageWidthPx)
                            val swipeThresholdPx = pageWidthPx * 0.1f // User's threshold
                            var pageVisualDelta = 0

                            if (abs(offsetFromCenterOfStrip) > swipeThresholdPx) {
                                pageVisualDelta =
                                    if (offsetFromCenterOfStrip < -swipeThresholdPx) 1 else -1 // 1 for next, -1 for prev
                            }

                            // Target camera offset to center the new target page (or re-center current)
                            val targetCameraOffsetX =
                                (-pageWidthPx) + (pageVisualDelta * -pageWidthPx)
                            val animationSpec = tween<Float>(durationMillis = 300)

                            coroutineScope.launch {
                                offsetX.animateTo(targetCameraOffsetX, animationSpec)

                                val newTargetDate = when (pageVisualDelta) {
                                    1 -> getNextDate(internalSettledDate)
                                    -1 -> getPreviousDate(internalSettledDate)
                                    else -> internalSettledDate
                                }

                                if (internalSettledDate != newTargetDate) {
                                    internalSettledDate = newTargetDate
                                    // Only call onPageChanged if the user-swiped date is different from the prop
                                    if (currentDate != internalSettledDate) {
                                        onPageChanged(internalSettledDate)
                                    }
                                }
                                // Settle to a single page view
                                pagesInStrip = listOf(internalSettledDate)
                                offsetX.snapTo(0f) // Camera centered on the single page
                            }
                        }
                    )
                }
        ) {
            // This Box is the "camera" that moves over the filmstrip
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationX = offsetX.value }
            ) {
                // The filmstrip itself
                pagesInStrip.forEachIndexed { indexInStrip, dateInStrip ->
                    key(dateInStrip) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    // Each page is positioned horizontally within the filmstrip
                                    translationX = indexInStrip * pageWidthPx
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            itemContent(dateInStrip)
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun CalendarPagerPreview() {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    TimestripeTheme {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TimestripeTheme.colorScheme.primaryBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = currentDate.toString())

            Column(
                Modifier.weight(1f)
            ) {
                CalendarPager(
                    currentDate = currentDate,
                    getNextDate = { date -> date.plusDays(1) },
                    getPreviousDate = { date -> date.minusDays(1) },
                    onPageChanged = { currentDate = it },
                    itemContent = { date ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = date.toString())
                        }
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { currentDate = currentDate.minusDays(1) }) {
                    Text("Previous")
                }
                Button(onClick = { currentDate = LocalDate.now() }) {
                    Text("Today")
                }
                Button(onClick = { currentDate = currentDate.plusDays(1) }) {
                    Text("Next")
                }
            }
            
        }
    }
}