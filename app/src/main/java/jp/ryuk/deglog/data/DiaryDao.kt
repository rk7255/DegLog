package jp.ryuk.deglog.data

import androidx.room.*

@Dao
interface DiaryDao {
    @Insert
    fun insert(diary: Diary)

    @Update
    fun update(diary: Diary)

    @Query("DELETE FROM diary_table WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT * FROM diary_table ORDER BY date DESC")
    fun getDiaries() : List<Diary>

    @Query("SELECT * FROM diary_table WHERE id = :key")
    fun getDiary(key: Long) : Diary

    @Query("SELECT * FROM diary_table WHERE name = :name ORDER BY date DESC")
    fun getDiariesAtName(name: String) : List<Diary>

    @Query("SELECT * FROM diary_table WHERE name = :name ORDER BY date DESC LIMIT 1")
    fun getDiaryAtNameLatest(name: String) : Diary

    @Query("SELECT DISTINCT name FROM diary_table ORDER BY name")
    fun getNames() : List<String>

    @Query("DELETE FROM diary_table")
    fun clear()

    @Query("SELECT date FROM diary_table WHERE (name = :name AND weight IS NOT NULL) ORDER BY date DESC LIMIT 1")
    fun getDateOfWeightLatest(name: String): Long

    @Query("SELECT date FROM diary_table WHERE (name = :name AND length IS NOT NULL) ORDER BY date DESC LIMIT 1")
    fun getDateOfLengthLatest(name: String): Long

}