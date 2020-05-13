package jp.ryuk.deglog.utilities

import android.content.Context
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.data.ProfileRepository
import jp.ryuk.deglog.ui.dashboard.DashboardViewModelFactory
import jp.ryuk.deglog.ui.diarydetail.DiaryDetailViewModelFactory
import jp.ryuk.deglog.ui.diarylist.DiaryListViewModelFactory
import jp.ryuk.deglog.ui.newdiary.NewDiaryViewModelFactory
import jp.ryuk.deglog.ui.profile.newprofile.NewProfileViewModelFactory
import jp.ryuk.deglog.ui.profile.profiles.ProfilesViewModelFactory

object InjectorUtil {

    private fun getDiaryDao(context: Context): DiaryDao {
        return DiaryRepository.getInstance(context).diaryDao
    }

    private fun getProfileDao(context: Context): ProfileDao {
        return ProfileRepository.getInstance(context).profileDao
    }

    fun provideDashboardViewModelFactory(context: Context): DashboardViewModelFactory {
        val diaryDao = getDiaryDao(context)
        val profileDao = getProfileDao(context)
        return DashboardViewModelFactory(diaryDao, profileDao)
    }

    fun provideDiaryDetailViewModelFactory(
        context: Context,
        id: Long,
        name: String
    ): DiaryDetailViewModelFactory {
        val diaryDao = getDiaryDao(context)
        val profileDao = getProfileDao(context)
        return DiaryDetailViewModelFactory(id, name, diaryDao, profileDao)
    }

    fun provideDiaryListViewModelFactory(
        context: Context,
        name: String
    ): DiaryListViewModelFactory {
        val diaryDao = getDiaryDao(context)
        val profileDao = getProfileDao(context)
        return DiaryListViewModelFactory(name, diaryDao, profileDao)
    }

    fun provideNewDiaryViewModelFactory(
        context: Context,
        id: Long,
        name: String
    ): NewDiaryViewModelFactory {
        val diaryDao = getDiaryDao(context)
        val profileDao = getProfileDao(context)
        return NewDiaryViewModelFactory(id, name, diaryDao, profileDao)
    }

    fun provideNewProfileViewModelFactory(
        context: Context,
        name: String
    ): NewProfileViewModelFactory {
        val diaryDao = getDiaryDao(context)
        val profileDao = getProfileDao(context)
        return NewProfileViewModelFactory(name, diaryDao, profileDao)
    }

    fun provideProfilesViewModelFactory(context: Context): ProfilesViewModelFactory {
        val profileDao = getProfileDao(context)
        return ProfilesViewModelFactory(profileDao)
    }
}