package com.mirea.veremeev.l.m.audiorecord

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val TAG : String = MainActivity::class.java.name
    private val REQUEST_CODE_PERMISSION = 100
    private val PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )
    private var isWork : Boolean = false

    private lateinit var startRecordingButton : Button
    private lateinit var stopRecordingButton : Button
    private lateinit var mediaRecorder: MediaRecorder
    private var audioFile : File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startRecordingButton = findViewById(R.id.btnStart)
        stopRecordingButton = findViewById(R.id.btnStop)
        mediaRecorder = MediaRecorder()

        isWork = hasPermissions(this, PERMISSIONS)
        if (!isWork){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSION)
        }
    }

    private fun hasPermissions(context: Context, permissions : Array<String>) : Boolean{
        if (context != null && permissions != null){
            for (permission in permissions){
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION){
            isWork = grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    public fun onStart(view : View){
        try {
            startRecordingButton.isEnabled = false
            stopRecordingButton.isEnabled = true
            stopRecordingButton.requestFocus()
            startRecording()
        } catch (e : Exception) {
            Log.e(TAG, "Caught io exception " + e.message)
        }
    }

    public fun onStop(view : View){
        startRecordingButton.isEnabled = true
        stopRecordingButton.isEnabled = false
        stopRecording()
        processAudioFile()
    }

    @Throws(IOException::class)
    private fun startRecording(){
        var state : String = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d(TAG, "Sd-card successfully found")
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            if (audioFile == null) {
                audioFile = File(this.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                    "mirea.3gp")
            }
            mediaRecorder.setOutputFile(audioFile!!.absolutePath)
            mediaRecorder.prepare()
            mediaRecorder.start()
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        if (mediaRecorder != null) {
            Log.d(TAG, "stopRecording")
            mediaRecorder.stop()
            mediaRecorder.reset()
            mediaRecorder.release()
            Toast.makeText(
                this, "You are not recording right now!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun processAudioFile() {
        Log.d(TAG, "processAudioFile")
        val values = ContentValues(4)
        val current = System.currentTimeMillis()

        values.put(MediaStore.Audio.Media.TITLE, "audio" + audioFile!!.name)
        values.put(MediaStore.Audio.Media.DATE_ADDED, (current / 1000).toInt())
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp")
        values.put(MediaStore.Audio.Media.DATA, audioFile!!.absolutePath)
        val contentResolver = contentResolver

        Log.d(TAG, "audioFile: " + audioFile!!.canRead())

        val baseUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val newUri: Uri? = contentResolver.insert(baseUri, values)
        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri))
    }

}