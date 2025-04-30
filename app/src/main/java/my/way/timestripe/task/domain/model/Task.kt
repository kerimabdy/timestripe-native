package my.way.timestripe.task.domain.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class Task(
    val id: String,
    val title: String,
    val date: LocalDate,
    val isCompleted: Boolean = false
)
