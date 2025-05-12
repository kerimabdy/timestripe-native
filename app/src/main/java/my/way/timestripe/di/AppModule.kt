package my.way.timestripe.di

import my.way.timestripe.task.presentation.task_detail.TaskDetailViewModel
import my.way.timestripe.task.presentation.task_list.TaskListViewModel
import my.way.timestripe.task.presentation.new_task.NewTaskViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {
    viewModelOf(::TaskListViewModel)
    viewModelOf(::TaskDetailViewModel)
    viewModelOf(::NewTaskViewModel)
}