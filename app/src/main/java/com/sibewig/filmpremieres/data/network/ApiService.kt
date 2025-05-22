package com.sibewig.filmpremieres.data.network

import com.sibewig.filmpremieres.data.network.model.MovieDto
import com.sibewig.filmpremieres.data.network.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie")
//    &selectFields=type
    suspend fun loadMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int = LIMIT,
        @Query("sortField") sortField: String = SORT_FIELD,
        @Query("sortType") sortType: Int = SORT_TYPE,
        @Query("notNullFields") notNull1: String = NOT_NULL_PREMIERE_RUSSIA,
        @Query("notNullFields") notNull2: String = NOT_NULL_NAME,
        @Query("type") type: String = TYPE,
        @Query("year") year: Int = YEAR,
        @Query("token") token: String = TOKEN,
        @Query("selectFields") selectFields1: String = SELECT_FIELD_ID,
        @Query("selectFields") selectFields2: String = SELECT_FIELD_NAME,
        @Query("selectFields") selectFields3: String = SELECT_FIELD_DESCRIPTION,
        @Query("selectFields") selectFields4: String = SELECT_FIELD_POSTER,
        @Query("selectFields") selectFields5: String = SELECT_FIELD_VIDEOS,
        @Query("selectFields") selectFields6: String = SELECT_FIELD_PREMIERE
    ): MovieResponse

    @GET("movie/{id}?token=VNHCQ2C-H684JSE-J3N4HT7-MRS78CB")
    suspend fun loadMovie(@Path("id") id: Int): MovieDto

    companion object {

        private const val LIMIT = 26
        private const val SORT_FIELD = "premiere.russia"
        private const val SORT_TYPE = 1
        private const val NOT_NULL_PREMIERE_RUSSIA = "premiere.russia"
        private const val NOT_NULL_NAME = "name"
        private const val TYPE = "!tv-series"
        private const val YEAR = 2025
        private const val TOKEN = "VNHCQ2C-H684JSE-J3N4HT7-MRS78CB"
        private const val SELECT_FIELD_PREMIERE = "premiere"
        private const val SELECT_FIELD_VIDEOS = "videos"
        private const val SELECT_FIELD_NAME = "name"
        private const val SELECT_FIELD_POSTER = "poster"
        private const val SELECT_FIELD_ID = "id"
        private const val SELECT_FIELD_DESCRIPTION = "description"

    }
}