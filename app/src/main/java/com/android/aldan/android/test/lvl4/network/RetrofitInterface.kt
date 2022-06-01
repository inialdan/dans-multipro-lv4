package com.android.aldan.android.test.lvl4.network

import com.android.aldan.android.test.lvl4.model.PositionDetailResponse
import com.android.aldan.android.test.lvl4.model.PositionResponseItem
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface RetrofitInterface {

    @GET("positions.json")
    fun getPositionsScroll(
        @Query("page") page: Int?,
        @Query("description") description: String?,
        @Query("location") location: String?,
        @Query("full_time") full_time: Boolean?,
    ): Observable<ArrayList<PositionResponseItem>>

    @GET("positions/{ID}")
    fun getPositionsDetail(
        @Path("ID") ID: String?,
    ): Call<PositionDetailResponse>

}
