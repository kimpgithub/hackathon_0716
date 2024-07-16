package com.example.hackathon_0716

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.hackathon_0716.ui.theme.Hackathon_0716Theme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var poseAnalyzer: PoseAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        previewView = PreviewView(this)
        poseAnalyzer = PoseAnalyzer(previewView)

        requestCameraPermission()

        setContent {
            Hackathon_0716Theme {
                val isRecording = remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreview(previewView, poseAnalyzer.keyPoints)

                    FloatingActionButton(
                        onClick = {
                            if (isRecording.value) {
                                // 녹화 중지 코드 추가
                                isRecording.value = false
                            } else {
                                // 녹화 시작 코드 추가
                                isRecording.value = true
                            }
                        },
                        backgroundColor = if (isRecording.value) Color.Gray else Color.Red,
                        contentColor = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = if (isRecording.value) Icons.Filled.Stop else Icons.Filled.FiberManualRecord,
                            contentDescription = if (isRecording.value) "Stop Recording" else "Start Recording"
                        )
                    }
                }
            }
        }
    }

    private fun requestCameraPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    startCamera()
                } else {
                    Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        CameraUtils.startCamera(
            this,
            cameraExecutor,
            previewView,
            poseAnalyzer
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
