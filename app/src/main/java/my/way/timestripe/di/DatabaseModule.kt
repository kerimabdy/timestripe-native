package my.way.timestripe.di

import android.content.Context
import androidx.room.Room
import my.way.timestripe.task.data.local.dao.TaskDao
import my.way.timestripe.task.data.local.database.DATABASE_NAME
import my.way.timestripe.task.data.local.database.TaskDatabase
import my.way.timestripe.task.data.repository.TaskRepositoryImpl
import my.way.timestripe.task.domain.repository.TaskRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            TaskDatabase::class.java,
                DATABASE_NAME
        ).build()
    }

    single<TaskDao> {
        get<TaskDatabase>().taskDao()
    }

    single<TaskRepository> {
        TaskRepositoryImpl(get())
    }
} 