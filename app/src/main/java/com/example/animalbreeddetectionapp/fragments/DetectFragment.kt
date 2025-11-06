package com.example.animalbreeddetectionapp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.animalbreeddetectionapp.R
import com.example.animalbreeddetectionapp.dashboard.BreedResultActivity
import java.io.ByteArrayOutputStream

class DetectFragment : Fragment() {

    private val CAMERA_REQUEST = 101
    private val GALLERY_REQUEST = 102
    private var selectedImageBytes: ByteArray? = null

    private lateinit var imagePreview: ImageView
    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var btnDetect: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_detect, container, false)

        imagePreview = view.findViewById(R.id.imagePreview)
        btnCamera = view.findViewById(R.id.btnCamera)
        btnGallery = view.findViewById(R.id.btnGallery)
        btnDetect = view.findViewById(R.id.btnDetect)

        btnCamera.setOnClickListener { openCamera() }
        btnGallery.setOnClickListener { openGallery() }
        btnDetect.setOnClickListener {
            if (selectedImageBytes != null) {
                val intent = Intent(requireContext(), BreedResultActivity::class.java)
                intent.putExtra("imageBytes", selectedImageBytes)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Please select or capture an image first.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
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
                    imagePreview.setImageBitmap(bitmap)
                    selectedImageBytes = convertBitmapToByteArray(bitmap)
                }
                GALLERY_REQUEST -> {
                    val imageUri: Uri? = data?.data
                    imagePreview.setImageURI(imageUri)

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
