package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.database.DiaryRepository
import jp.ryuk.deglog.database.ProfileRepository
import jp.ryuk.deglog.database.TodoRepository

class DashboardViewModelFactory(
    private val diaryRepository: DiaryRepository,
    private val profileRepository: ProfileRepository,
    private val todoRepository: TodoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DashboardViewModel(
            diaryRepository,
            profileRepository,
            todoRepository
        ) as T
    }

}