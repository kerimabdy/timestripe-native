package my.way.timestripe

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import my.way.timestripe.navigation.Horizon
import my.way.timestripe.navigation.TaskDetail
import my.way.timestripe.task.presentation.task_detail.TaskDetailScreen
import my.way.timestripe.task.presentation.task_detail.TaskDetailViewModel
import my.way.timestripe.task.presentation.task_list.TaskListActions
import my.way.timestripe.task.presentation.task_list.TaskListScreen
import my.way.timestripe.task.presentation.task_list.TaskListViewModel
import my.way.timestripe.ui.theme.TimestripeTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimestripeTheme {
                Surface(
                    color = TimestripeTheme.colorScheme.secondaryBackground,
                    contentColor = TimestripeTheme.colorScheme.labelPrimary
                ) {
                    SharedTransitionLayout {
                        val navController = rememberNavController()

                        var imageBitmap: ImageBitmap? by remember { mutableStateOf(null) }
                        val graphicsLayer = rememberGraphicsLayer()
                        NavHost(navController = navController, startDestination = Horizon) {
                            composable<Horizon>(
                                enterTransition = { fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)) },
                                exitTransition = { fadeOut(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)) },
                                popEnterTransition = { fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)) },
                                popExitTransition = { fadeOut(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)) },
                            ) {
                                val taskListViewModel = koinViewModel<TaskListViewModel>()
                                val taskListState by taskListViewModel.state.collectAsStateWithLifecycle()

                                TaskListScreen(
                                    modifier = Modifier
                                        .drawWithContent {
                                            graphicsLayer.record {
                                                this@drawWithContent.drawContent()
                                            }
                                            drawLayer(graphicsLayer)
                                        },
                                    state = taskListState,
                                    onAction = {
                                        when (it) {
                                            is TaskListActions.NavigateToTaskDetail -> {
                                                navController.navigate(TaskDetail(taskId = it.taskId.toString()))
                                                taskListViewModel.onAction(it)
                                            }

                                            else -> {
                                                taskListViewModel.onAction(it)
                                            }
                                        }
                                    },
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedContentScope = this@composable
                                )

//                                LaunchedEffect(Unit) {
//                                    while (true) {
//                                        if (graphicsLayer.size.width != 0) {
//                                            imageBitmap = graphicsLayer.toImageBitmap()
//                                        }
//                                        delay(1000)
//
//                                    }
//                                }
                            }
                            composable<TaskDetail>(
                                enterTransition = { fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)) },
                                exitTransition = { fadeOut(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)) },
                                popEnterTransition = { fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)) },
                                popExitTransition = { fadeOut(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)) },
                            ) {
                                val taskDetailViewModel = koinViewModel<TaskDetailViewModel>()
                                val taskDetailState by taskDetailViewModel.state.collectAsStateWithLifecycle()
                                val image = painterResource(id = R.drawable.abstract_08)

                                TaskDetailScreen(
                                    modifier = Modifier
                                        .drawBehind {
                                            with(image) {
                                                draw(size = this@drawBehind.size)
                                            }
                                        },
                                    state = taskDetailState,
                                    onAction = taskDetailViewModel::onAction,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedContentScope = this@composable
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}