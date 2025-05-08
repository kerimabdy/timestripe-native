package my.way.timestripe.task.domain.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.LocalDateTime

@Immutable
data class Task(
    val id: Long = 0,
    val title: String = "",
    val description: String? = null,
    val dueDate: LocalDate? = null,
    val column: Int = 1, // 1=Day, 2=Week, 3=Month, 4=Year, 5=Life
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)