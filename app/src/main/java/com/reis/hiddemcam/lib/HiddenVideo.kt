package com.reis.hiddemcam.lib

import android.content.Context
import android.util.Size
import androidx.camera.core.*
import java.io.File

class HiddenVideo @JvmOverloads constructor(
    context: Context,
    private val baseFileDirectory: File,
    private val videoCaptureListener: OnVideoCapturedListener,
    private val targetAspectRatio: TargetAspectRatio? = null,
    private val targetResolution: Size? = null,
    private val targetRotation: Int? = null,
    private val cameraType: CameraType = CameraType.BACK_CAMERA,
    private val flashMode: FlashMode = FlashMode.OFF
) {
    private lateinit var captureTimer: CaptureTimerHandler
    private val lifeCycleOwner = HiddenCamLifeCycleOwner()

    private var isRecording = false
    private var preview: Preview

    private var previewConfig = PreviewConfig.Builder().apply {
        setLensFacing(cameraType.lensFacing)
        if (targetRotation != null) setTargetRotation(targetRotation)
        if (targetAspectRatio != null) setTargetAspectRatio(targetAspectRatio.aspectRatio)
        if (targetResolution != null) setTargetResolution(targetResolution)
    }.build()

    private var videoCapture: VideoCapture

    private var videoCaptureConfig = VideoCaptureConfig.Builder().apply {
        if (targetAspectRatio != null) setTargetAspectRatio(targetAspectRatio.aspectRatio)
        setLensFacing(cameraType.lensFacing)
        setVideoFrameRate(24)
        if (targetRotation != null) setTargetRotation(targetRotation)
    }.build()

    init {
        if (context.hasPermissions()) {
            preview = Preview(previewConfig)
            videoCapture = VideoCapture(videoCaptureConfig)
            CameraX.bindToLifecycle(lifeCycleOwner, preview, videoCapture)
        } else throw SecurityException("You nedd to have access CAMERA, WRITE_EXTERNAL_STORAGE, and AUDIO permissions")
    }

    fun start() {
        lifeCycleOwner.start()
    }

    fun stop() {
        lifeCycleOwner.stop()
    }

    fun destroy() {
        lifeCycleOwner.tearDown()
    }

    fun captureVideo() {
        if (!isRecording) {
            preview.enableTorch(flashMode == FlashMode.ON)
            videoCapture.startRecording(
                createVideoFile(baseFileDirectory),
                MainThreadExecutor,
                object : VideoCapture.OnVideoSavedListener {
                    override fun onVideoSaved(file: File) {
                        videoCaptureListener.onVideoCaptured(file)
                    }

                    override fun onError(
                        videoCaptureError: VideoCapture.VideoCaptureError,
                        message: String,
                        cause: Throwable?
                    ) {
                        videoCaptureListener.onVideoCaptureError(cause)
                    }
                }
            )
        } else {
            preview.enableTorch(false)
            videoCapture.stopRecording()
        }
        isRecording = !isRecording
    }
}