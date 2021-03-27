package com.bupt.sse.group7.covid19.interfaces;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface DAO {
    @GET
    Call<ResponseBody> executeGet(@Url String url, @QueryMap Map<String, String> queryMap);

    @GET
    Call<ResponseBody> executeGet(@Url String url);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST
    Call<String> executePost(@Url String url, @Body RequestBody data);

}
