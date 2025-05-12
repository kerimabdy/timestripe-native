package my.way.timestripe.task.presentation.new_task

import my.way.timestripe.task.domain.model.Task
import java.time.LocalDate

data class NewTaskUiState(
  val task: Task = Task(dueDate = LocalDate.now())
)