package com.example.hackathon_0716

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

class PoseAnalyzer(private val previewView: PreviewView) : ImageAnalysis.Analyzer {
    private val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()
    private val poseDetector = PoseDetection.getClient(options)

    private val _keyPoints = mutableStateListOf<KeyPoint>()
    val keyPoints: SnapshotStateList<KeyPoint> get() = _keyPoints

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val frameWidth = mediaImage.width
            val frameHeight = mediaImage.height
            val viewWidth = previewView.width
            val viewHeight = previewView.height

            Log.d("PoseAnalyzer", "Frame width: $frameWidth, Frame height: $frameHeight")
            Log.d("PoseAnalyzer", "View width: $viewWidth, View height: $viewHeight")

            val scaleFactorX = viewWidth.toFloat() / frameWidth
            val scaleFactorY = viewHeight.toFloat() / frameHeight
            val scaleFactor = minOf(scaleFactorX, scaleFactorY)

            val offsetX = (viewWidth - frameWidth * scaleFactor) / 2
            val offsetY = (viewHeight - frameHeight * scaleFactor) / 2

            poseDetector.process(image)
                .addOnSuccessListener { pose ->
                    _keyPoints.clear()
                    pose.allPoseLandmarks.forEach { landmark ->
                        if (landmark.inFrameLikelihood >= 0.95) {
                            val name = getLandmarkName(landmark.landmarkType)
                            val x = landmark.position.x * scaleFactor + offsetX
                            val y = landmark.position.y * scaleFactor + offsetY

                            Log.d("PoseAnalyzer", "KeyPoint $name: x=$x, y=$y")

                            _keyPoints.add(KeyPoint(x, y, name))
                        }
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun getLandmarkName(landmarkType: Int): String {
        return when (landmarkType) {
            PoseLandmark.NOSE -> "NOSE"
            PoseLandmark.LEFT_EYE_INNER -> "LEFT_EYE_INNER"
            PoseLandmark.RIGHT_EYE_INNER -> "RIGHT_EYE_INNER"
            PoseLandmark.LEFT_EYE -> "LEFT_EYE"
            PoseLandmark.RIGHT_EYE -> "RIGHT_EYE"
            PoseLandmark.LEFT_EYE_OUTER -> "LEFT_EYE_OUTER"
            PoseLandmark.RIGHT_EYE_OUTER -> "RIGHT_EYE_OUTER"
            PoseLandmark.LEFT_EAR -> "LEFT_EAR"
            PoseLandmark.RIGHT_EAR -> "RIGHT_EAR"
            PoseLandmark.LEFT_SHOULDER -> "LEFT_SHOULDER"
            PoseLandmark.RIGHT_SHOULDER -> "RIGHT_SHOULDER"
            PoseLandmark.LEFT_ELBOW -> "LEFT_ELBOW"
            PoseLandmark.RIGHT_ELBOW -> "RIGHT_ELBOW"
            PoseLandmark.LEFT_WRIST -> "LEFT_WRIST"
            PoseLandmark.RIGHT_WRIST -> "RIGHT_WRIST"
            PoseLandmark.LEFT_HIP -> "LEFT_HIP"
            PoseLandmark.RIGHT_HIP -> "RIGHT_HIP"
            PoseLandmark.LEFT_KNEE -> "LEFT_KNEE"
            PoseLandmark.RIGHT_KNEE -> "RIGHT_KNEE"
            PoseLandmark.LEFT_ANKLE -> "LEFT_ANKLE"
            PoseLandmark.RIGHT_ANKLE -> "RIGHT_ANKLE"
            PoseLandmark.LEFT_PINKY -> "LEFT_PINKY"
            PoseLandmark.RIGHT_PINKY -> "RIGHT_PINKY"
            PoseLandmark.LEFT_INDEX -> "LEFT_INDEX"
            PoseLandmark.RIGHT_INDEX -> "RIGHT_INDEX"
            PoseLandmark.LEFT_THUMB -> "LEFT_THUMB"
            PoseLandmark.RIGHT_THUMB -> "RIGHT_THUMB"
            PoseLandmark.LEFT_HEEL -> "LEFT_HEEL"
            PoseLandmark.RIGHT_HEEL -> "RIGHT_HEEL"
            PoseLandmark.LEFT_FOOT_INDEX -> "LEFT_FOOT_INDEX"
            PoseLandmark.RIGHT_FOOT_INDEX -> "RIGHT_FOOT_INDEX"
            else -> "UNKNOWN"
        }
    }
}
