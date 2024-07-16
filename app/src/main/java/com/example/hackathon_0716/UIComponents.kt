package com.example.hackathon_0716

import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

data class KeyPoint(val x: Float, val y: Float, val name: String)

@Composable
fun CameraPreview(previewView: PreviewView, keyPoints: SnapshotStateList<KeyPoint>) {
    val lines = listOf(
        Pair("NOSE", "LEFT_EYE_INNER"),
        Pair("NOSE", "RIGHT_EYE_INNER"),
        Pair("LEFT_EYE_INNER", "LEFT_EYE"),
        Pair("RIGHT_EYE_INNER", "RIGHT_EYE"),
        Pair("LEFT_EYE", "LEFT_EYE_OUTER"),
        Pair("RIGHT_EYE", "RIGHT_EYE_OUTER"),
        Pair("LEFT_EYE_OUTER", "LEFT_EAR"),
        Pair("RIGHT_EYE_OUTER", "RIGHT_EAR"),
        Pair("LEFT_SHOULDER", "RIGHT_SHOULDER"),
        Pair("LEFT_SHOULDER", "LEFT_ELBOW"),
        Pair("RIGHT_SHOULDER", "RIGHT_ELBOW"),
        Pair("LEFT_ELBOW", "LEFT_WRIST"),
        Pair("RIGHT_ELBOW", "RIGHT_WRIST"),
        Pair("LEFT_SHOULDER", "LEFT_HIP"),
        Pair("RIGHT_SHOULDER", "RIGHT_HIP"),
        Pair("LEFT_HIP", "RIGHT_HIP"),
        Pair("LEFT_HIP", "LEFT_KNEE"),
        Pair("RIGHT_HIP", "RIGHT_KNEE"),
        Pair("LEFT_KNEE", "LEFT_ANKLE"),
        Pair("RIGHT_KNEE", "RIGHT_ANKLE"),
        Pair("LEFT_WRIST", "LEFT_THUMB"),
        Pair("LEFT_WRIST", "LEFT_PINKY"),
        Pair("LEFT_WRIST", "LEFT_INDEX"),
        Pair("RIGHT_WRIST", "RIGHT_THUMB"),
        Pair("RIGHT_WRIST", "RIGHT_PINKY"),
        Pair("RIGHT_WRIST", "RIGHT_INDEX"),
        Pair("LEFT_ANKLE", "LEFT_HEEL"),
        Pair("RIGHT_ANKLE", "RIGHT_HEEL"),
        Pair("LEFT_HEEL", "LEFT_FOOT_INDEX"),
        Pair("RIGHT_HEEL", "RIGHT_FOOT_INDEX"),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView }
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            lines.forEach { (startName, endName) ->
                val start = keyPoints.find { it.name == startName }
                val end = keyPoints.find { it.name == endName }
                if (start != null && end != null) {
                    drawLine(
                        color = Color.Blue,
                        start = Offset(start.x, start.y),
                        end = Offset(end.x, end.y),
                        strokeWidth = 4f
                    )
                }
            }
            keyPoints.forEach { point ->
                drawCircle(
                    color = Color.Red,
                    radius = 8f,
                    center = Offset(point.x, point.y)
                )
            }
        }
    }
}
