package com.vine.projectdemo.API;

import com.vine.projectdemo.Model.JSONResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestInterfaceByID {
    @POST("login-register/api-id/")
    Call<JSONResponse> getJSON(@Query("id") String id);
}