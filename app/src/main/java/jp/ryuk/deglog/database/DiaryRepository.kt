package jp.ryuk.deglog.database

class DiaryRepository private constructor(private val diaryDao: DiaryDao) {

    fun getDiary(id: Long) = diaryDao.getDiary(id)
    fun getDiaries(name: String) = diaryDao.getDiaries(name)
    fun getAllDiary() = diaryDao.getAllDiary()

    suspend fun insert(diary: Diary) = diaryDao.insert(diary)
    suspend fun deleteById(id: Long) = diaryDao.deleteById(id)
    suspend fun changeName(old: String, new: String) = diaryDao.changeName(old, new)

    companion object {
        @Volatile private var instance: DiaryRepository? = null

        fun getInstance(diaryDao: DiaryDao) =
            instance ?: synchronized(this) {
                instance ?: DiaryRepository(diaryDao).also { instance = it }
            }
    }
}