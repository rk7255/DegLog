package jp.ryuk.deglog.database

class ProfileRepository private constructor(private val profileDao: ProfileDao) {

    fun getProfile(name: String) = profileDao.getProfile(name)
    fun getAllProfile() = profileDao.getAllProfile()
    fun getNames() = profileDao.getNames()

    suspend fun insert(profile: Profile) = profileDao.insert(profile)
    suspend fun deleteByName(name: String) = profileDao.deleteByName(name)

    companion object {
        @Volatile private var instance: ProfileRepository? = null
        fun getInstance(profileDao: ProfileDao) =
            instance ?: synchronized(this) {
                instance ?: ProfileRepository(profileDao).also { instance = it }
            }
    }
}