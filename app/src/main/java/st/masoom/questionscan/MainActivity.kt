package st.masoom.questionscan

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import st.masoom.questionscan.ui.theme.QuestionScanTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
//import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestionScanTheme {
                ImageTextExtractor()
            }
        }
    }
}
@Composable
fun ImageTextExtractor() {
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var extractedText by remember { mutableStateOf("Extracted text will appear here.") }
    var pdfFilePath by remember { mutableStateOf<String?>(null) }
    var fileName by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImages = uris
        if (uris.isNotEmpty()) {
            extractedText = "Extracting text from ${uris.size} images..."
            processMultipleImages(uris, context) { text ->
                extractedText = text.ifEmpty { "No text found in selected images." }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Upload Image Button
            Button(
                onClick = { launcher.launch("image/*") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB))
            ) {
                Text("Upload Image", fontSize = 18.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(selectedImages.size) { index ->
            Image(
                painter = rememberAsyncImagePainter(selectedImages[index]),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(250.dp)
                    .padding(8.dp)
            )
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Show Extracted Text
            if (selectedImages.isNotEmpty()) {
                Text(
                    text = extractedText,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Enter File Name
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Enter File Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Save as PDF Button
                Button(
                    onClick = {
                        val safeFileName = if (fileName.text.isBlank()) "ExtractedText.pdf"
                        else "${fileName.text}.pdf"
                        pdfFilePath = saveAsPDF(context, extractedText, safeFileName)
                    }
                ) {
                    Text("Save as PDF")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            // Download PDF Button after PDF is Saved
            pdfFilePath?.let { path ->
                Button(onClick = { openPDF(context, path) }) {
                    Text("Download PDF")
                }
            }
        }
    }
}

// Process Multiple Images for Text Recognition
fun processMultipleImages(uris: List<Uri>, context: Context, onTextExtracted: (String) -> Unit) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val extractedTexts = mutableListOf<String>()

    uris.forEachIndexed { index, uri ->
        try {
            val image = InputImage.fromFilePath(context, uri)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    extractedTexts.add("Image ${index + 1}:\n${visionText.text}")
                    if (extractedTexts.size == uris.size) {
                        onTextExtracted(extractedTexts.joinToString("\n\n"))
                    }
                }
                .addOnFailureListener { e ->
                    extractedTexts.add("Image ${index + 1}: Error extracting text.")
                    if (extractedTexts.size == uris.size) {
                        onTextExtracted(extractedTexts.joinToString("\n\n"))
                    }
                }
        } catch (e: Exception) {
            extractedTexts.add("Image ${index + 1}: Error processing image.")
            if (extractedTexts.size == uris.size) {
                onTextExtracted(extractedTexts.joinToString("\n\n"))
            }
        }
    }
}

// Save Extracted Text as a PDF with a custom name
fun saveAsPDF(context: Context, text: String, fileName: String): String? {
    return try {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        val pdfWriter = PdfWriter(FileOutputStream(file))
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
        val document = Document(pdfDocument)
        document.add(Paragraph(text))
        document.close()

        Toast.makeText(context, "Saved as $fileName in Downloads", Toast.LENGTH_LONG).show()
        file.absolutePath
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        null
    }
}

// Open the Saved PDF File
fun openPDF(context: Context, filePath: String) {
    val file = File(filePath)
    val uri = Uri.fromFile(file)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(Intent.createChooser(intent, "Open PDF"))
}