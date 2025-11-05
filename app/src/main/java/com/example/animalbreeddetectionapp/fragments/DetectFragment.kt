package com.example.animalbreeddetectionapp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.animalbreeddetectionapp.dashboard.BreedResultActivity
import com.example.animalbreeddetectionapp.databinding.FragmentDetectBinding
import java.io.ByteArrayOutputStream

class DetectFragment : Fragment() {

    private lateinit var binding: FragmentDetectBinding
    private val CAMERA_REQUEST = 101
    private val GALLERY_REQUEST = 102
    private var selectedImageBytes: ByteArray? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetectBinding.inflate(inflater, container, false)

        binding.btnCamera.setOnClickListener { openCamera() }
        binding.btnGallery.setOnClickListener { openGallery() }

        binding.btnDetect.setOnClickListener {
            if (selectedImageBytes != null) {
                // Send the image to BreedResultActivity
                val intent = Intent(requireContext(), BreedResultActivity::class.java)
                intent.putExtra("imageBytes", selectedImageBytes)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Please select or capture an image first.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.imagePreview.setImageBitmap(bitmap)
                    selectedImageBytes = convertBitmapToByteArray(bitmap)
                }
                GALLERY_REQUEST -> {
                    val imageUri: Uri? = data?.data
                    binding.imagePreview.setImageURI(imageUri)

                    imageUri?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
                        selectedImageBytes = convertBitmapToByteArray(bitmap)
                    }
                }
            }
        }
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return stream.toByteArray()
    }
}
