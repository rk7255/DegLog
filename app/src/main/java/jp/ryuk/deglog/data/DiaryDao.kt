package jp.ryuk.deglog.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

    @Query("DELETE FROM diary_table WHERE name = :name")
    fun clear(name: String)

    @Query("SELECT date FROM diary_table WHERE (name = :name AND weight IS NOT NULL) ORDER BY date DESC LIMIT 1")
    fun getDateOfWeightLatest(name: String): Long

    @Query("SELECT date FROM diary_table WHERE (name = :name AND length IS NOT NULL) ORDER BY date DESC LIMIT 1")
    fun getDateOfLengthLatest(name: String): Long

    @Query("UPDATE diary_table SET name = :new WHERE name = :old")
    fun changeName(old: String, new: String)


    // テスト用
    @Query("SELECT * FROM diary_table ORDER BY date DESC")
    fun getAllDiaries() : LiveData<List<Diary>>

    @Query("SELECT DISTINCT name FROM diary_table ORDER BY name")
    fun getNamesInDiaryDB() : LiveData<List<String>>

    @Query("UPDATE diary_table SET success = :success WHERE id = :id")
    fun success(id: Long, success: Boolean)

    @Query("SELECT * FROM diary_table WHERE id = :id")
    fun getDiaryLive(id: Long) : LiveData<Diary?>

    @Query("SELECT * FROM diary_table WHERE name = :name ORDER BY date DESC")
    fun getDiariesLive(name: String) : LiveData<List<Diary>>


}