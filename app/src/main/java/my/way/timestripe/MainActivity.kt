package my.way.timestripe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import my.way.timestripe.task.presentation.task_list.TaskListScreen
import my.way.timestripe.task.presentation.task_list.TaskListViewModel
import my.way.timestripe.ui.theme.TimestripeTheme
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimestripeTheme {
                val taskListViewModel = koinViewModel<TaskListViewModel>()
                val taskListState by taskListViewModel.state.collectAsStateWithLifecycle()
                TaskListScreen(
                    state = taskListState,
                    actions = taskListViewModel::onAction
                )
            }
        }
    }
}

@Composable
fun InfiniteCalendar() {
    val listState = rememberLazyListState()
    val days = remember { mutableStateListOf<LocalDate>() }

    val today = remember { LocalDate.now() }

    // Initially populate days
    LaunchedEffect(Unit) {
        val range = -30..30
        days.addAll(range.map { today.plusDays(it.toLong()) })
    }

    LazyColumn(state = listState) {
        items(days) { day ->
            CalendarDayView(day)
        }
    }

    // Infinite scroll logic
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collectLatest { index ->
                if (index < 5) {
                    val first = days.first()
                    val newDays = (1..10).map { first.minusDays(it.toLong()) }.reversed()
                    days.addAll(0, newDays)
                }

                if (index > days.size - 10) {
                    val last = days.last()
                    val newDays = (1..10).map { last.plusDays(it.toLong()) }
                    days.addAll(newDays)
                }
            }
    }
}

@Composable
fun CalendarDayView(date: LocalDate) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Header for the date
        Text(
            text = date.format(DateTimeFormatter.ofPattern("EEEE, MMM dd")),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dummy event cards
        repeat(3) { index ->
            EventCard(
                title = "Event ${index + 1}",
                time = "${8 + index}:00 AM - ${9 + index}:00 AM"
            )
        }
    }
}

@Composable
fun EventCard(title: String, time: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = time, style = MaterialTheme.typography.bodySmall)
        }
    }
}