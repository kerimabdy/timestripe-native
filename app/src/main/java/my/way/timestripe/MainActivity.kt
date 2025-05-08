package my.way.timestripe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import my.way.timestripe.navigation.Horizon
import my.way.timestripe.navigation.TaskDetail
import my.way.timestripe.task.presentation.task_detail.TaskDetailAction
import my.way.timestripe.task.presentation.task_detail.TaskDetailScreen
import my.way.timestripe.task.presentation.task_detail.TaskDetailViewModel
import my.way.timestripe.task.presentation.task_list.TaskListActions
import my.way.timestripe.task.presentation.task_list.TaskListScreen
import my.way.timestripe.task.presentation.task_list.TaskListViewModel
import my.way.timestripe.ui.theme.TimestripeTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimestripeTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Horizon) {
                    composable<Horizon>() {
                        val taskListViewModel = koinViewModel<TaskListViewModel>()
                        val taskListState by taskListViewModel.state.collectAsStateWithLifecycle()

                        TaskListScreen(
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
                            }
                        )
                    }
                    dialog<TaskDetail>(
                        dialogProperties = DialogProperties(
                            usePlatformDefaultWidth = false
                        )
                    ) {
                        val taskDetailViewModel = koinViewModel<TaskDetailViewModel>()
                        val taskDetailState by taskDetailViewModel.state.collectAsStateWithLifecycle()
                        TaskDetailScreen(
                            state = taskDetailState,
                            onAction = taskDetailViewModel::onAction
                        )
                    }
                }
            }
        }
    }
}