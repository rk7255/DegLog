package jp.ryuk.deglog.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProfileDao {
    @Insert
    fun insert(profile: Profile)

    @Update
    fun update(profile: Profile)

    @Delete
    fun delete(profile: Profile)

    @Query("SELECT * FROM profile_table ORDER BY name")
    fun getProfiles() : List<Profile>

    @Query("SELECT * FROM profile_table WHERE name = :key")
    fun getProfile(key: String) : Profile

    @Query("SELECT name FROM profile_table")
    fun getNames() : List<String>

    @Query("DELETE FROM profile_table")
    fun clear()
}