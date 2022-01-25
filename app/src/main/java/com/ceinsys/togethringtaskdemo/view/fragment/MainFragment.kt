package com.ceinsys.togethringtaskdemo.view.fragment

import android.Manifest
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ceinsys.togethringtaskdemo.R
import com.ceinsys.togethringtaskdemo.databinding.FragmentMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import android.content.ContentValues
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.launch
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        // Set Menu
        setHasOptionsMenu(true)

        permissionLauncher =
            registerForActivityResult(RequestMultiplePermissions()) { permission ->
                isReadPermissionGranted =
                    permission[android.Manifest.permission.READ_EXTERNAL_STORAGE]
                        ?: isReadPermissionGranted
                isWritePermissionGranted =
                    permission[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                        ?: isWritePermissionGranted
            }
        requestPermission()

        val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            lifecycleScope.launch {
                if (isWritePermissionGranted) {
                    saveImageToInternalStorage(it!!)
                    binding.photoCapture.setImageBitmap(it)
                    if (savePhotoToGallery(UUID.randomUUID().toString(), it)) {
                        Toast.makeText(
                            requireContext(),
                            "Photo save successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed Photo save", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Permission not Granted", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.capturePhotoBtn.setOnClickListener {
            takePhoto.launch()
        }

        return binding.root
    }

    private fun sdkCheck(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        val isReadPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val isWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSDKLevel = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        isReadPermissionGranted = isReadPermission
        isWritePermissionGranted = isWritePermission || minSDKLevel

        val permissionRequest = mutableListOf<String>()
        if (!isWritePermissionGranted) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!isReadPermissionGranted) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    private fun savePhotoToGallery(name: String, bmp: Bitmap): Boolean {
        val imageCollection: Uri = if (sdkCheck()) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bmp.width)
            put(MediaStore.Images.Media.HEIGHT, bmp.height)
        }

        return try {
            requireContext().contentResolver.insert(imageCollection, contentValues)?.also {
                requireContext().contentResolver.openOutputStream(it).use { outputStream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                        throw IOException("Failed to save Bitmap")
                    }
                }
            } ?: throw IOException("Failed to create Media Store")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getExternalFilesDir(IMAGE_DIRECTORY)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.showRecyclerview -> {
                findNavController().navigate(R.id.action_mainFragment_to_listFragment2)
                true
            }
            R.id.userProfile -> {
                findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "MyTask"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}