package jp.ryuk.deglog.database

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
    fun getTodoList(name: String): List<Todo>

    @Query("SELECT * FROM todo_table")
    fun getAllTodo(): List<Todo>
}