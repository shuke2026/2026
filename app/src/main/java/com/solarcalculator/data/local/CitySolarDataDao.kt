package com.solarcalculator.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.solarcalculator.data.model.CitySolarData

@Dao
interface CitySolarDataDao {
    
    @Query("SELECT * FROM city_solar_data ORDER BY cityName")
    fun getAllCities(): LiveData<List<CitySolarData>>
    
    @Query("SELECT * FROM city_solar_data ORDER BY cityName")
    suspend fun getAllCitiesSync(): List<CitySolarData>
    
    @Query("SELECT * FROM city_solar_data WHERE cityName = :cityName LIMIT 1")
    suspend fun getCityByName(cityName: String): CitySolarData?
    
    @Query("SELECT * FROM city_solar_data WHERE province = :province ORDER BY cityName")
    suspend fun getCitiesByProvince(province: String): List<CitySolarData>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CitySolarData)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<CitySolarData>)
    
    @Delete
    suspend fun deleteCity(city: CitySolarData)
    
    @Query("DELETE FROM city_solar_data")
    suspend fun deleteAllCities()
    
    @Query("SELECT COUNT(*) FROM city_solar_data")
    suspend fun getCityCount(): Int
    
    @Query("SELECT * FROM city_solar_data WHERE cityName LIKE '%' || :query || '%' OR province LIKE '%' || :query || '%'")
    suspend fun searchCities(query: String): List<CitySolarData>
}
