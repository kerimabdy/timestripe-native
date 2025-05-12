package my.way.timestripe.task.presentation.widget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import my.way.timestripe.task.presentation.new_task.NewTaskAction
import my.way.timestripe.task.presentation.new_task.NewTaskFromWidgetScreen
import my.way.timestripe.task.presentation.new_task.NewTaskViewModel
import my.way.timestripe.ui.theme.TimestripeTheme
import org.koin.androidx.compose.koinViewModel


class AddTaskActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {


            TimestripeTheme {
                Surface(
                    color = Color.Black.copy(alpha = 0f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        val viewModel = koinViewModel<NewTaskViewModel>()
                        val state by viewModel.uiState.collectAsStateWithLifecycle()


                        NewTaskFromWidgetScreen(
                            state = state,
                            onAction = {
                                when (it) {
                                    is NewTaskAction.DismissRequested -> {
                                        // close the activity
                                        viewModel.onAction(NewTaskAction.SaveTask)
                                        finish()
                                    }

                                    else -> viewModel.onAction(it)
                                }
                            }
                        )

                    }
                }
            }
        }
    }
}


