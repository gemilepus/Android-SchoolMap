package com.vine.projectdemo.API;

import com.vine.projectdemo.DataView.JSONResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {
    @GET("login-register/api/")
    Call<JSONResponse> getJSON();
}