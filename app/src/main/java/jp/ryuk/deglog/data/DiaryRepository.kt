package jp.ryuk.deglog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Diary::class], version = 1, exportSchema = false)
abstract class DiaryRepository : RoomDatabase() {

    abstract val diaryDao: DiaryDao

    companion object {
        @Volatile private var INSTANCE: DiaryRepository? = null

        fun getInstance(context: Context): DiaryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiaryRepository::class.java,
                    "diary_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}