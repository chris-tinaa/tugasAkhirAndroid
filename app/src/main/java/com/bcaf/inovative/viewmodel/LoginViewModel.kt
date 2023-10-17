package com.bcaf.inovative.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.bcaf.inovative.data.api.request.LoginRequest
import com.bcaf.inovative.data.api.response.BaseResponse
import com.bcaf.inovative.data.api.response.LoginResponse
import com.bcaf.inovative.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val userRepo = UserRepository()
    val loginResult: MutableLiveData<BaseResponse<LoginResponse>> = MutableLiveData()

    fun loginUser(email: String, pwd: String) {

        loginResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(
                    password = pwd,
                    username = email
                )
                val response = userRepo.loginUser(loginRequest = loginRequest)
                if (response?.code() == 200) {
                    loginResult.value = BaseResponse.Success(response.body())
                    Log.i("Hasil login 200", loginResult.toString())

                } else {
                    loginResult.value = BaseResponse.Error(response?.message())
                    Log.i("Hasil login non 200", loginResult.value.toString())
                }

            } catch (ex: Exception) {
                loginResult.value = BaseResponse.Error(ex.message)
                Log.i("Hasil login excp", loginResult.value.toString())

            }
        }
    }
}