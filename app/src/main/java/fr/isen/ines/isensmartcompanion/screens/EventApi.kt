package fr.isen.ines.isensmartcompanion.screens

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface EventApi {
    @GET("events.json")
    suspend fun getEvents(): Response<List<EventModel>>

    companion object {
        private const val BASE_URL = "https://isen-smart-companion-default-rtdb.europe-west1.firebasedatabase.app/"

        fun create(): EventApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EventApi::class.java)
        }
    }
}
