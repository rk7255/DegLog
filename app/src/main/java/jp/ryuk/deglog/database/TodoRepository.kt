package jp.ryuk.deglog.database

class TodoRepository private constructor(private val todoDao: TodoDao) {

    suspend fun insert(todo: Todo) = todoDao.insert(todo)

    fun getTodo(id: Long) = todoDao.getTodo(id)
    fun getTodoList(name: String) = todoDao.getTodoList(name)
    fun getAllTodo() = todoDao.getAllTodo()

    companion object {
        @Volatile private var instance: TodoRepository? = null
        fun getInstance(todoDao: TodoDao) =
            instance ?: synchronized(this) {
                instance ?: TodoRepository(todoDao).also { instance = it }
            }
    }
}