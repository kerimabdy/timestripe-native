package my.way.timestripe.task.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import my.way.timestripe.MainActivity
import my.way.timestripe.R
import my.way.timestripe.task.domain.model.Task
import my.way.timestripe.task.domain.repository.TaskRepository
import org.koin.core.context.GlobalContext
import java.time.LocalDate
import java.util.Locale

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
            if (data.isNotEmpty()) {
                Log.d("TodayWidget", "Data is not empty")
                TodayWidgetContent(
                    data
                )
            } else {
                Text("No data for today")
            }
        }
    }


//    @SuppressLint("RestrictedApi")
//    @Composable
//    private fun MyContent(
//        tasks: List<Task>,
//    ) {
//        Column(
//            GlanceModifier
//                .fillMaxSize()
//                .background(ColorProvider(Color.White))
//                .padding(horizontal = 16.dp)
//        ) {
//            Header(
//                count = tasks.size
//            )
//            LazyColumn(
//                modifier = GlanceModifier.fillMaxWidth().defaultWeight()
//            ) {
//                item {
//                    Spacer(GlanceModifier.size(6.dp))
//                }
//                items(tasks, itemId = { task -> task.id }) {
//                    Column {
//                        Spacer(
//                            GlanceModifier
//                                .fillMaxWidth()
//                                .height(0.75.dp)
//                                .background(ColorProvider(Color.Black.copy(alpha = .2f)))
//                        )
//                        Text(
//                            it.title,
//                            modifier = GlanceModifier.padding(vertical = 8.dp),
//                            style = TextStyle(
//                                fontSize = TextUnit(14f, TextUnitType.Sp),
//                                color = ColorProvider(Color.Black.copy(alpha = .8f)),
//                                textDecoration = if (it.isCompleted) TextDecoration.LineThrough else TextDecoration.None
//                            )
//                        )
//                    }
//                }
//            }
//
//            Column(
//                modifier = GlanceModifier.fillMaxWidth()
//                    .padding(top = 6.dp, bottom = 18.dp)
//            ) {
//                Text(
//                    "${tasks.size} goals",
//                    style = TextStyle(
//                        fontSize = TextUnit(14f, TextUnitType.Sp),
//                        fontWeight = FontWeight.Medium,
//                        color = ColorProvider(Color.Black.copy(alpha = .4f))
//                    )
//                )
//            }
//        }
//    }
//
//    @SuppressLint("RestrictedApi")
//    @Composable
//    fun Header(
//        modifier: GlanceModifier = GlanceModifier,
//        count: Int = 0,
//    ) {
//        Row(
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(vertical = 18.dp),
//            horizontalAlignment = Alignment.Horizontal.End
//        ) {
//            Text(
//                "Today",
//                style = TextStyle(
//                    fontWeight = FontWeight.Bold,
//                    fontSize = TextUnit(22f, TextUnitType.Sp),
//                    color = ColorProvider(Color.Black.copy(alpha = .8f))
//                )
//            )
//            Spacer(GlanceModifier.defaultWeight())
//            Text(
//                "${LocalDate.now().dayOfYear} day",
//                style = TextStyle(
//                    fontSize = TextUnit(16f, TextUnitType.Sp),
//                    fontWeight = FontWeight.Medium,
//                    color = ColorProvider(Color.Black.copy(alpha = .5f))
//                )
//            )
//        }
//    }


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


@SuppressLint("RestrictedApi")
@Composable
fun TodayWidgetContent(
    tasks: List<Task>,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color.White))
            .padding(16.dp)
            .clickable(actionStartActivity<AddTaskActivity>())
    ) {
        DayInfo(
            modifier = GlanceModifier.fillMaxHeight().padding(end = 40.dp)
        )
        Column(
            GlanceModifier.fillMaxHeight().defaultWeight()
        ) {
            TodayTaskList(tasks, GlanceModifier.defaultWeight())
            BottomBar(tasks.size)
        }
    }
}


@SuppressLint("RestrictedApi")
@Composable
private fun DayInfo(
    modifier: GlanceModifier = GlanceModifier,
) {
    val today = LocalDate.now()
    val dayOfWeek =
        today.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()).uppercase()
    val dayOfMonth = today.dayOfMonth.toString()
    Column(
        modifier
    ) {
        Text(
            "Today",
            style = TextStyle(
                fontSize = TextUnit(22f, TextUnitType.Sp),
                fontWeight = FontWeight.Bold,
                color = ColorProvider(Color.Black),
            ),
        )

        Column(
            modifier = GlanceModifier
                .defaultWeight(),
            verticalAlignment = Alignment.Vertical.Bottom
        ) {
            // week day WED
            Text(
                dayOfWeek,
                style = TextStyle(
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = ColorProvider(Color(0xFFFF9500)),
                ),
            )
            Text(
                dayOfMonth,
                style = TextStyle(
                    fontSize = TextUnit(34f, TextUnitType.Sp),
                    color = ColorProvider(Color.Black),
                )
            )
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun TodayTaskListItem(
    index: Int,
    task: Task,
) {
    val color = colors[index % colors.size]
    Row(
        GlanceModifier.padding(bottom = 6.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Spacer(
            GlanceModifier.width(3.dp).height(32.dp).cornerRadius(2.dp)
                .background(ColorProvider(color))
        )
        Spacer(GlanceModifier.size(6.dp))
        Text(
            task.title,
            style = TextStyle(
                fontSize = TextUnit(12f, TextUnitType.Sp),
                fontWeight = FontWeight.Medium,
                color = if (task.isCompleted) ColorProvider(Color.Black.copy(alpha = .4f)) else ColorProvider(Color.Black),
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
        )
    }
}


@SuppressLint("RestrictedApi")
@Composable
private fun TodayTaskList(
    tasks: List<Task>,
    modifier: GlanceModifier = GlanceModifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(tasks, itemId = { task -> task.id }) {
            TodayTaskListItem(
                tasks.indexOf(it),
                it
            )
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun BottomBar(
    taskCount: Int,
    modifier: GlanceModifier = GlanceModifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Vertical.Bottom
    ) {
        Text(
            "$taskCount more goals",
            style = TextStyle(
                fontSize = TextUnit(12f, TextUnitType.Sp),
                fontWeight = FontWeight.Medium,
                color = ColorProvider(Color.Black.copy(alpha = .4f))
            ),
            modifier = GlanceModifier.defaultWeight()
        )

        Box(
            modifier = GlanceModifier
                .size(28.dp)
                .cornerRadius(14.dp)
                .background(ColorProvider(Color.Black))
                .clickable(actionStartActivity<AddTaskActivity>())
            ,
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(R.drawable.add_20px), // Replace with your icon resource
                contentDescription = "My Icon Description",
                modifier = GlanceModifier.size(20.dp) // Optional: Set size
            )
        }
    }
}

val Orange = Color(0xFFFF9500)
val Blue = Color(0xFF007AFF)
val Green = Color(0xFF34C759)
val Indigo = Color(0xFF5856D6)
val Pink = Color(0xFFFF2D55)
val Purple = Color(0xFFAF52DE)
val Red = Color(0xFFFF3B30)
val Teal = Color(0xFF30B0C7)
val Yellow = Color(0xFFFFCC00)
val Brown = Color(0xFFA2845E)

val colors = listOf(Orange, Blue, Green, Indigo, Orange, Pink, Purple, Red, Teal, Yellow, Brown)

class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodayWidget()
}

