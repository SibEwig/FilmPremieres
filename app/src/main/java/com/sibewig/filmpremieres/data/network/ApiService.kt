package com.sibewig.filmpremieres.data.network

import com.sibewig.filmpremieres.data.network.model.MovieDto
import com.sibewig.filmpremieres.data.network.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie")
    suspend fun loadMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int = LIMIT,
        @Query("sortField") sortField: String = SORT_FIELD,
        @Query("sortType") sortType: Int = SORT_TYPE,
        @Query("notNullFields") notNullFields: List<String> = listOf(
            NOT_NULL_PREMIERE_RUSSIA,
            NOT_NULL_NAME
        ),
        @Query("type") type: String = TYPE,
        @Query("premiere.russia") premiereRange: String = PREMIERE_RUSSIA,
        @Query("selectFields") selectFields: List<String> = listOf(
            SELECT_FIELD_ID,
            SELECT_FIELD_NAME,
            SELECT_FIELD_DESCRIPTION,
            SELECT_FIELD_POSTER,
            SELECT_FIELD_VIDEOS,
            SELECT_FIELD_PREMIERE
        ),
        @Query("token") token: String = TOKEN,
    ): MovieResponse

    @GET("movie/{id}")
    suspend fun loadMovie(
        @Path("id") id: Int,
        @Query("token") token: String = TOKEN
    ): MovieDto

    @GET("movie/search")
    suspend fun searchMovie(
        @Query("query") query: String,
        @Query("token") token: String = TOKEN,
        @Query("limit") limit: Int = LIMIT
    ): MovieResponse

    companion object {

        private const val LIMIT = 26
        private const val SORT_FIELD = "premiere.russia"
        private const val SORT_TYPE = 1
        private const val NOT_NULL_PREMIERE_RUSSIA = "premiere.russia"
        private const val NOT_NULL_NAME = "name"
        private const val TYPE = "!tv-series"
        private const val PREMIERE_RUSSIA = "01.01.2025-31.12.2025"
        private const val TOKEN = "VNHCQ2C-H684JSE-J3N4HT7-MRS78CB"
        private const val SELECT_FIELD_PREMIERE = "premiere"
        private const val SELECT_FIELD_VIDEOS = "videos"
        private const val SELECT_FIELD_NAME = "name"
        private const val SELECT_FIELD_POSTER = "poster"
        private const val SELECT_FIELD_ID = "id"
        private const val SELECT_FIELD_DESCRIPTION = "description"

    }
}