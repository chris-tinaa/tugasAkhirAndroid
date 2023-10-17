package com.bcaf.inovative.repository

import com.bcaf.inovative.data.api.methods.UserApi
import com.bcaf.inovative.data.api.request.LoginRequest
import com.bcaf.inovative.data.api.response.LoginResponse
import retrofit2.Response

class UserRepository {

   suspend fun loginUser(loginRequest:LoginRequest): Response<LoginResponse>? {
      return  UserApi.getApi()?.loginUser(loginRequest = loginRequest)
    }
}