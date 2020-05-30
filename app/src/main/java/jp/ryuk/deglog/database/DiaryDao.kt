package jp.ryuk.deglog.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DiaryDao {

    @Query("SELECT * FROM diary_table WHERE id = :id")
    fun getDiary(id: Long): LiveData<Diary?>

    @Query("SELECT * FROM diary_table WHERE name = :name ORDER BY date DESC")
    fun getDiaries(name: String): LiveData<List<Diary>>

    @Query("SELECT * FROM diary_table ORDER BY date DESC")
    fun getAllDiary(): LiveData<List<Diary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diary: Diary)

    @Query("DELETE FROM diary_table WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM diary_table WHERE name = :name")
    suspend fun deleteAll(name: String)

    @Query("UPDATE diary_table SET name = :new WHERE name = :old")
    suspend fun changeName(old: String, new: String)

}