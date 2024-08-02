package com.example.notificationdownloaduseprogressbar

import android.app.DownloadManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private var videoId: Int = 0
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            startDownload()
        } else {
            Snackbar.make(
                findViewById(R.id.main),
                R.string.permission_denied,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }


    private fun startDownload() {
        val uri = Uri.parse(DOWNLOAD_URL)
        val request = DownloadManager.Request(uri)
        val subPath = "${TARGET_FILE}${FILE_TYPE}"
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setTitle("Downloading....")
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath)
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnDownload = findViewById<Button>(R.id.btn_download)
        btnDownload.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            requestPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        }
        startDownload()
    }
    companion object{
        private const val HOST_NAME = "https://braniumacademy.net"
        const val FILE_TYPE = ".mp4"
        const val TARGET_FILE ="install_netbeans"
        const val DOWNLOAD_URL = "${HOST_NAME}/resources/videos/java/$TARGET_FILE$FILE_TYPE"
    }
}
