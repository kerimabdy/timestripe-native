package my.way.timestripe.navigation

import kotlinx.serialization.Serializable

@Serializable
object Horizon

@Serializable
object Day

@Serializable
object Week

@Serializable
object Month

@Serializable
object Year

@Serializable
object Life

@Serializable
data class TaskDetail(
  val taskId: String
)

@Serializable
object NewTask
