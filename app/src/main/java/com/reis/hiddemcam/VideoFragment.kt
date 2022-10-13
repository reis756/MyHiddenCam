package com.reis.hiddemcam

import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.reis.hiddemcam.databinding.FragmentVideoBinding
import com.reis.hiddemcam.lib.HiddenVideo
import com.reis.hiddemcam.lib.OnVideoCapturedListener
import java.io.File

class VideoFragment : Fragment(), OnVideoCapturedListener {

    private var _binding: FragmentVideoBinding? = null

    private val binding get() = _binding!!

    private lateinit var hiddenVideo: HiddenVideo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val baseStorageFolder = File(requireContext().getExternalFilesDir(null), "HiddenCam").apply {
            if (exists()) deleteRecursively()
            mkdir()
        }

        hiddenVideo = HiddenVideo(
            requireContext(), baseStorageFolder, this,
            targetResolution = Size(1080, 1920)
        )

        binding.buttonSecond.setOnClickListener {
            hiddenVideo.captureVideo()
        }
    }

    override fun onStart() {
        super.onStart()
        hiddenVideo.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hiddenVideo.destroy()
        _binding = null
    }

    override fun onVideoCaptured(video: File) {
        showToast("Video Captured, saved to: ${video.absoluteFile}")
    }

    override fun onVideoCaptureError(e: Throwable?) {
        e?.message?.let { showToast(it) }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}