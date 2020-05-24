package jp.ryuk.deglog.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Query("SELECT * FROM todo_table WHERE id = :id")
    fun getTodo(id: Long): Todo

    @Query("SELECT * FROM todo_table WHERE name = :name")
    fun getTodoList(name: String): LiveData<List<Todo>>

    @Query("SELECT * FROM todo_table")
    fun getAllTodo(): LiveData<List<Todo>>

    @Query("UPDATE todo_table SET done = :done WHERE id = :id")
    suspend fun done(id: Long, done: Boolean)
}