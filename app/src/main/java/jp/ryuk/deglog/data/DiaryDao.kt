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

    @Query("DELETE FROM diary_table WHERE name = :name")
    fun deleteAll(name: String)

    @Query("SELECT * FROM diary_table WHERE id = :id")
    fun getDiary(id: Long) : LiveData<Diary?>

    @Query("SELECT * FROM diary_table WHERE name = :name ORDER BY date DESC")
    fun getDiaries(name: String) : LiveData<List<Diary>>

    @Query("SELECT * FROM diary_table ORDER BY date DESC")
    fun getAllDiaries() : LiveData<List<Diary>>

    @Query("SELECT DISTINCT name FROM diary_table ORDER BY name")
    fun getNames() : LiveData<List<String>>

    @Query("UPDATE diary_table SET success = :success WHERE id = :id")
    fun success(id: Long, success: Boolean)

    @Query("UPDATE diary_table SET name = :new WHERE name = :old")
    fun changeName(old: String, new: String)

}