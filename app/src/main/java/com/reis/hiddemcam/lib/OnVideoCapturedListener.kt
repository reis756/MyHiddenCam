package com.reis.hiddemcam.lib

import java.io.File

interface OnVideoCapturedListener {
    fun onVideoCaptured(video: File)
    fun onVideoCaptureError(e: Throwable?)
}