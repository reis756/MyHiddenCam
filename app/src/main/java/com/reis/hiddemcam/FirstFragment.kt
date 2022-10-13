package com.reis.hiddemcam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.reis.hiddemcam.databinding.FragmentFirstBinding
import com.reis.hiddemcam.lib.HiddenCam
import com.reis.hiddemcam.lib.OnImageCapturedListener
import java.io.File

class FirstFragment : Fragment(){

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!

    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkPermissions()) onPermissionsGranted()

        binding.buttonTakePicture.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun checkPermissions(): Boolean {
        return if (hasPermissions(permissions)) true
        else {
            requestPermissions(permissions, CAMERA_REQUEST_CODE)
            false
        }
    }

    private fun hasPermissions(permissionList: Array<String>): Boolean {
        for (permission in permissionList) {
            if (!hasPermission(permission)) return false
        }
        return true
    }

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

    private fun onPermissionsGranted() {
        binding.buttonTakePicture.isEnabled = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_REQUEST_CODE && confirmPermissionResults(grantResults))
            onPermissionsGranted()
    }

    private fun confirmPermissionResults(results: IntArray): Boolean {
        results.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CAMERA_REQUEST_CODE = 1000
    }
}