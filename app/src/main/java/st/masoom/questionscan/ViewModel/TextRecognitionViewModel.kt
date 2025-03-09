package st.masoom.questionscan.ViewModel


import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

class TextRecognitionViewModel : ViewModel() {
    var recognizedText by mutableStateOf("")

    fun processImage(imageUri: Uri, context: android.content.Context) {
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, imageUri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    recognizedText = visionText.text
                }
                .addOnFailureListener { e ->
                    recognizedText = "Error: ${e.localizedMessage}"
                }
        } catch (e: IOException) {
            recognizedText = "Error loading image"
        }
    }
}