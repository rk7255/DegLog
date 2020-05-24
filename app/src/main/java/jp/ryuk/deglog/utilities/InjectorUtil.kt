package jp.ryuk.deglog.utilities

import android.content.Context
import jp.ryuk.deglog.database.*
import jp.ryuk.deglog.ui.viewmodels.ChartViewModelFactory
import jp.ryuk.deglog.ui.viewmodels.DashboardViewModelFactory
import jp.ryuk.deglog.ui.viewmodels.DiaryDetailViewModelFactory
import jp.ryuk.deglog.ui.viewmodels.DiaryListViewModelFactory
import jp.ryuk.deglog.ui.viewmodels.NewDiaryViewModelFactory
import jp.ryuk.deglog.ui.viewmodels.NewProfileViewModelFactory
import jp.ryuk.deglog.ui.viewmodels.ProfilesViewModelFactory

object InjectorUtil {

    private fun getDiaryRepository(context: Context): DiaryRepository {
        return DiaryRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).diaryDao()
        )
    }

    private fun getProfileRepository(context: Context): ProfileRepository {
        return ProfileRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).profileDao()
        )
    }

    private fun getTodoRepository(context: Context): TodoRepository {
        return TodoRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).todoDao()
        )
    }

    fun provideDashboardViewModelFactory(context: Context): DashboardViewModelFactory {
        val diaryRepository = getDiaryRepository(context)
        val profileRepository = getProfileRepository(context)
        val todoRepository = getTodoRepository(context)

        return DashboardViewModelFactory(
            diaryRepository,
            profileRepository,
            todoRepository
        )
    }

    fun provideDiaryDetailViewModelFactory(
        context: Context,
        id: Long,
        name: String
    ): DiaryDetailViewModelFactory {
        val diaryRepository = getDiaryRepository(context)
        val profileRepository = getProfileRepository(context)
        return DiaryDetailViewModelFactory(
            id,
            name,
            diaryRepository,
            profileRepository
        )
    }

    fun provideDiaryListViewModelFactory(
        context: Context,
        name: String
    ): DiaryListViewModelFactory {
        val diaryRepository = getDiaryRepository(context)
        val profileRepository = getProfileRepository(context)
        return DiaryListViewModelFactory(
            name,
            diaryRepository,
            profileRepository
        )
    }

    fun provideNewDiaryViewModelFactory(
        context: Context,
        id: Long,
        name: String
    ): NewDiaryViewModelFactory {
        val diaryRepository = getDiaryRepository(context)
        val profileRepository = getProfileRepository(context)
        return NewDiaryViewModelFactory(
            id,
            name,
            diaryRepository,
            profileRepository
        )
    }

    fun provideNewProfileViewModelFactory(
        context: Context,
        name: String
    ): NewProfileViewModelFactory {
        val diaryRepository = getDiaryRepository(context)
        val profileRepository = getProfileRepository(context)
        return NewProfileViewModelFactory(
            name,
            diaryRepository,
            profileRepository
        )
    }

    fun provideProfilesViewModelFactory(context: Context): ProfilesViewModelFactory {
        val profileRepository = getProfileRepository(context)
        return ProfilesViewModelFactory(
            profileRepository
        )
    }

    fun provideChartViewModelFactory(context: Context): ChartViewModelFactory {
        val diaryRepository = getDiaryRepository(context)
        val profileRepository = getProfileRepository(context)
        return ChartViewModelFactory(
            diaryRepository,
            profileRepository
        )
    }
}