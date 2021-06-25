package com.bybutter.kiradesu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bybutter.kiradesu.demo.DemoActivity

class PermissionActivity : AppCompatActivity() {
    companion object {
        private const val REQ_PMS_CODE = 0x123
        private const val PERMISSION = Manifest.permission.CAMERA
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, PERMISSION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION)) {
                // 需要说明请求权限的理由，这里直接请求
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(PERMISSION),
                    REQ_PMS_CODE
                )
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(PERMISSION), REQ_PMS_CODE)
            }
        } else {
            startMain()
            finish()
        }
    }

    private fun startMain() {
        startActivity(Intent(this, DemoActivity::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_PMS_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMain()
                    finish()
                } else {
                }
                return
            }
        }
    }
}