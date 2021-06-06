package com.mirea.veremeev.l.m.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_PERMISSION_CAMERA = 100
    val TAG : String = MainActivity::class.java.name
    private lateinit var imageView : ImageView
    private val CAMERA_REQUEST = 0
    private var isWork = true
    private lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)

        val cameraPermissionStatus = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)
        val storagePermissionStatus = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (cameraPermissionStatus == PackageManager.PERMISSION_GRANTED &&
                storagePermissionStatus == PackageManager.PERMISSION_GRANTED){
            isWork = true
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION_CAMERA)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            imageView.setImageURI(imageUri)
        }
    }

    fun imageViewOnClick(view : View) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        Log.d("imageViewOnClick", "IM OUTSIDE")
        if(cameraIntent.resolveActivity(getPackageManager()) != null && isWork){
            Log.d("imageViewOnClick", "IM INSIDE")
            var photoFile : File? = null
            try {
                photoFile = createImageFile()
                Log.d("imageViewOnClick", "FILE CREATED")
            } catch (e : IOException) {
                e.printStackTrace()
            }
            var authorities = applicationContext.packageName + ".fileprovider"
            imageUri = FileProvider.getUriForFile(this, authorities, photoFile!!)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile() : File{
        var timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName : String = "IMAGE_" + timeStamp + "_"
        var storageDirectory : File = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDirectory)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION_CAMERA){
            if (grantResults.size > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isWork = true
            } else {
                isWork = false
            }
        }
    }
}