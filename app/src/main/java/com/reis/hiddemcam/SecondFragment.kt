package com.reis.hiddemcam

import android.os.Bundle
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.reis.hiddemcam.databinding.FragmentSecondBinding
import com.reis.hiddemcam.lib.HiddenCam
import com.reis.hiddemcam.lib.OnImageCapturedListener
import java.io.File

class SecondFragment : Fragment(), OnImageCapturedListener {

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    private lateinit var hiddenCam: HiddenCam

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val baseStorageFolder = File(requireContext().getExternalFilesDir(null), "HiddenCam").apply {
            if (exists()) deleteRecursively()
            mkdir()
        }

        hiddenCam = HiddenCam(
            requireContext(), baseStorageFolder, this,
            targetResolution = Size(1080, 1920)
        )

        binding.buttonSecond.setOnClickListener {
            hiddenCam.captureImage()
        }
    }

    override fun onStart() {
        super.onStart()
        hiddenCam.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hiddenCam.destroy()
        _binding = null
    }

    override fun onImageCaptured(image: File) {
        showToast("Image Captured, saved to: ${image.absoluteFile}")
    }

    override fun onImageCaptureError(e: Throwable?) {
        e?.message?.let { showToast(it) }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}