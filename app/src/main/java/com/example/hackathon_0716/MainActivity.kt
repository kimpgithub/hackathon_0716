package com.example.hackathon_0716

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.hackathon_0716.ui.theme.Hackathon_0716Theme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        previewView = PreviewView(this) // PreviewView 초기화

        requestCameraPermission()

        setContent {
            Hackathon_0716Theme {
                CameraPreview(previewView)
            }
        }
    }

    private fun requestCameraPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    // 권한이 허용된 경우의 처리
                    // 카메라를 초기화하거나 필요한 기능을 실행합니다.
                    startCamera()
                } else {
                    // 권한이 거부된 경우의 처리
                    // 사용자에게 권한이 필요하다는 메시지를 표시합니다.
                    Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 이미 허용된 경우의 처리
                startCamera()
            }

            else -> {
                // 권한을 요청하는 처리
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

@Composable
fun CameraPreview(previewView: PreviewView) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { previewView }
    )
}