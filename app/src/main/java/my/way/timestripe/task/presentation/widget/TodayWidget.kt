package my.way.timestripe.task.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.background
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.domain.repository.TaskRepository
import my.way.timestripe.ui.theme.InterFontFamily
import org.koin.core.context.GlobalContext
import java.time.LocalDate

class TodayWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        var isError = false;
        var data: List<Task> = emptyList()
        try {
            val repository = GlobalContext.get().get<TaskRepository>()
            data = repository.getTasksByDateAndColumnForWidget(LocalDate.now(), 1)
            Log.d("TodayWidget", "Getting tasks for today")
        } catch (e: Exception) {
            Log.e("TodayWidget", "Error getting tasks for today", e)
            isError = true;
            //handleError
        }

        provideContent {
            // create your AppWidget here
            Log.d("TodayWidget", "Providing content")
            if(data.isNotEmpty()) {
                Log.d("TodayWidget", "Data is not empty")
                MyContent(
                     data
                )
            } else {
                Text("No data for today")
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun MyContent(
        tasks: List<Task>
    ) {
        Column(
            GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color.White))
                .padding(horizontal = 16.dp)
        ) {
            Header(
                count = tasks.size
            )
            LazyColumn(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight()
            ) {
                item {
                    Spacer(GlanceModifier.size(6.dp))
                }
                items(tasks, itemId = { task -> task.id }) {
                    Column {
                        Spacer(
                            GlanceModifier
                                .fillMaxWidth()
                                .height(0.75.dp)
                                .background(ColorProvider(Color.Black.copy(alpha = .2f)))
                        )
                        Text(
                            it.title,
                            modifier = GlanceModifier.padding(vertical = 8.dp),
                            style = TextStyle(
                                fontSize = TextUnit(14f, TextUnitType.Sp),
                                color = ColorProvider(Color.Black.copy(alpha = .8f)),
                                textDecoration = if(it.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                        )
                    }
                }
            }

            Column(
                modifier = GlanceModifier.fillMaxWidth()
                    .padding(top = 6.dp, bottom = 18.dp)
            ) {
                Text(
                    "${tasks.size} goals",
                    style = TextStyle(
                        fontSize = TextUnit(14f, TextUnitType.Sp),
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(Color.Black.copy(alpha = .4f))
                    )
                )
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    fun Header(
        modifier: GlanceModifier = GlanceModifier,
        count: Int = 0,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp)
            ,
            horizontalAlignment = Alignment.Horizontal.End
        ) {
            Text(
                "Today",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(22f, TextUnitType.Sp),
                    color = ColorProvider(Color.Black.copy(alpha = .8f))
                )
            )
            Spacer(GlanceModifier.defaultWeight())
            Text(
                "${LocalDate.now().dayOfYear} day",
                style = TextStyle(
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(Color.Black.copy(alpha = .5f))
                )
            )
        }
    }


//    override suspend fun provideGlance(context: Context, id: GlanceId) {
//        // Initialize Koin for the widget
//        val taskRepository = GlobalContext.get().get<TaskRepository>()
//
//        val todayTasks = taskRepository.getTasksByDate(LocalDate.now()).collect(
//            it
//        )
//
//        provideContent {
//            TimestripeTheme {
//                Column(
//                    modifier = GlanceModifier
//                        .fillMaxWidth()
//                        .background(TimestripeTheme.colorScheme.secondaryBackground)
//                        .padding(16.dp)
////                        .clickable(actionStartActivity<MainActivity>())
//                ) {
//                    Text(
//                        text = "Today's Tasks",
//                        style = TextStyle(
//                            fontSize = TextUnit(18f, TextUnitType.Sp),
//                            color = ColorProvider(Color.Black)
//                        )
//                    )
//
//                    if (todayTasks.isEmpty()) {
//                        Text(
//                            text = "No tasks for today",
//                            style = TextStyle(
//                                fontSize = TextUnit(14f, TextUnitType.SP),
//                                color = ColorProvider(android.graphics.Color.parseColor("#666666"))
//                            ),
//                            modifier = GlanceModifier.padding(top = 8.dp)
//                        )
//                    } else {
//                        todayTasks.forEach { task ->
//                            TaskItem(task)
//                        }
//                    }
//                }
//            }
//            }
//        }
//    }
}

//@Composable
//private fun TaskItem(task: Task) {
//    Row(
//        modifier = GlanceModifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp)
//    ) {
//        Text(
//            text = if (task.isCompleted) "✓" else "○",
//            style = TextStyle(
//                fontSize = TextUnit(16f, TextUnitType.SP),
//                color = ColorProvider(android.graphics.Color.parseColor("#666666"))
//            ),
//            modifier = GlanceModifier.padding(end = 8.dp)
//        )
//
//        Text(
//            text = task.title,
//            style = TextStyle(
//                fontSize = TextUnit(14f, TextUnitType.SP),
//                color = ColorProvider(
//                    if (task.isCompleted)
//                        android.graphics.Color.parseColor("#666666")
//                    else
//                        android.graphics.Color.parseColor("#000000")
//                )
//            )
//        )
//    }
//}

class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodayWidget()
}

