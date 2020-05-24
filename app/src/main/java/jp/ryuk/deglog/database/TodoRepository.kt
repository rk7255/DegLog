package jp.ryuk.deglog.database

class TodoRepository private constructor(private val todoDao: TodoDao) {

    suspend fun insert(todo: Todo) = todoDao.insert(todo)
    suspend fun done(id: Long, done: Boolean) = todoDao.done(id, done)

    fun getAllTodo() = todoDao.getAllTodo()

    companion object {
        @Volatile private var instance: TodoRepository? = null
        fun getInstance(todoDao: TodoDao) =
            instance ?: synchronized(this) {
                instance ?: TodoRepository(todoDao).also { instance = it }
            }
    }
}