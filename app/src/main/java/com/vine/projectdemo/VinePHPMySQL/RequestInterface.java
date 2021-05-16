package com.vine.projectdemo.VinePHPMySQL;

import com.vine.projectdemo.VinePHPMySQL.models.ServerRequest;
import com.vine.projectdemo.VinePHPMySQL.models.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {
    @POST("login-register/")
    Call<ServerResponse> operation(@Body ServerRequest request);
}
