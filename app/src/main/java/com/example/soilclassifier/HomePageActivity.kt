package com.example.soilclassifier

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class HomePageActivity : AppCompatActivity() {
    private lateinit var tflite: Interpreter
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var pickImageButton: Button
    private lateinit var soilInfoButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.textView)
        pickImageButton = findViewById(R.id.btnPickImage)
        soilInfoButton = findViewById(R.id.btnSoilInfo)
        tflite = Interpreter(loadModelFile())
        pickImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                val bitmap = uriToBitmap(it)
                imageView.setImageBitmap(bitmap)

                // Run inference on selected image
                val inputBuffer = preprocessImage(bitmap)
                val outputArray = Array(1) { ByteArray(11) } // Output as uint8
                tflite.run(inputBuffer, outputArray)

                // Post-process and display result
                val predictedClass = postProcessOutput(outputArray[0])
                val soilType = getSoilNameFromLabel(predictedClass)  // Get soil name

                textView.text = "Predicted Soil Type: $soilType"
                soilInfoButton.setOnClickListener {
                    val intent = Intent(this, SoilInfoActivity::class.java)
                    intent.putExtra("PREDICTED_SOIL", soilType)
                    startActivity(intent)
                }


            }
        }
    }
    private fun loadModelFile(): ByteBuffer {
        val assetFileDescriptor = assets.openFd("soilclassifiermodel.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    private fun uriToBitmap(uri: Uri): Bitmap {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)!!
    }
    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val buffer = ByteBuffer.allocateDirect(224 * 224 * 3) // uint8
        buffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(224 * 224)
        resizedBitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)

        for (pixel in intValues) {
            buffer.put(((pixel shr 16) and 0xFF).toByte()) // Red
            buffer.put(((pixel shr 8) and 0xFF).toByte())  // Green
            buffer.put((pixel and 0xFF).toByte())         // Blue
        }
        return buffer
    }
    private fun postProcessOutput(output: ByteArray): Int {
        val scores = output.map { (it.toInt() and 0xFF) * 0.00390625f }
        return scores.indices.maxByOrNull { scores[it] } ?: -1
    }
    private fun getSoilNameFromLabel(predictedClass: Int): String {
        return try {
            val soilLabels = assets.open("label.txt").bufferedReader().readLines()
            if (predictedClass in soilLabels.indices) {
                soilLabels[predictedClass]
            } else {
                "Unknown Soil Type"
            }
        } catch (e: Exception) {
            "Error Loading Labels"
        }
    }
}
