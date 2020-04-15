package jp.ryuk.deglog.data

import androidx.room.*

@Dao
interface DiaryDao {
    @Insert
    fun insert(diary: Diary)

    @Update
    fun update(diary: Diary)

    @Delete
    fun delete(diary: Diary)

    @Query("SELECT * FROM diary_table ORDER BY id DESC")
    fun getDiaries() : List<Diary>

    @Query("SELECT * FROM diary_table WHERE id = :key")
    fun getDiary(key: Long) : Diary

    @Query("SELECT * FROM diary_table WHERE name = :name ORDER BY id DESC")
    fun getDiariesAtName(name: String) : List<Diary>

    @Query("SELECT * FROM diary_table WHERE name = :name ORDER BY id DESC LIMIT 1")
    fun getDiaryAtNameLatest(name: String) : Diary

    @Query("SELECT DISTINCT name FROM diary_table ")
    fun getNames() : List<String>

    @Query("DELETE FROM diary_table")
    fun clear()
}