package com.bcaf.inovative.ui

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bcaf.inovative.R
import com.bcaf.inovative.data.api.fragment.HomeActivity
import com.bcaf.inovative.data.api.fragment.MainActivity
import com.bcaf.inovative.data.api.response.BaseResponse
import com.bcaf.inovative.data.api.response.LoginResponse
import com.bcaf.inovative.databinding.ActivityMainBinding

import com.bcaf.inovative.utils.SessionManager
import com.bcaf.inovative.viewmodel.LoginViewModel

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermission()


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frmFragmentRoot, MainActivity.newInstance("", ""))
                .commit()
        }

    }

    fun checkAndRequestPermission(){
        val permissionNotGranted = mutableListOf<String>()
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionNotGranted.add(android.Manifest.permission.CAMERA)
        }

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionNotGranted.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionNotGranted.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if(permissionNotGranted.isNotEmpty()){
            val permissionArray = permissionNotGranted.toTypedArray()
            requestPermissions(permissionArray,0)
            }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0){
            for(i in permissions.indices){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Log.e("INFO","Permission Granted ${permissions[i]}")
                }
                else{
                    Log.e("INFO","Permission Not Granted ${permissions[i]}")
                }
            }
        }
    }
}
