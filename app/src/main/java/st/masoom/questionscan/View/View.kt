package st.masoom.questionscan.View

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import st.masoom.questionscan.Model.HandwritingRecognitionUI
import st.masoom.questionscan.Model.ImageTextExtractor
import st.masoom.questionscan.ViewModel.TextRecognitionViewModel

@Composable
fun AssignmentGraderScreen(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    val selectedOption = remember { mutableStateOf("Handwritten Text to Text") }
    val viewModel: TextRecognitionViewModel = viewModel()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        TopBar(isDarkTheme, onThemeChange)
        Text(
            text = "Choose the format of your assignment. You can upload images or PDFs",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        FormatSelection(selectedOption)
        Spacer(modifier = Modifier.height(16.dp))
        if (selectedOption.value == "Handwritten Text to Text") {
            HandwritingRecognitionUI(viewModel)
        } else {
            ImageTextExtractor()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = "Assignment Grader",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* Handle back navigation */ }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Light Mode") },
                        onClick = {
                            onThemeChange(false)
                            expanded = false
                        },
                        leadingIcon = {
                            if (!isDarkTheme) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Dark Mode") },
                        onClick = {
                            onThemeChange(true)
                            expanded = false
                        },
                        leadingIcon = {
                            if (isDarkTheme) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun FormatSelection(selectedOption: MutableState<String>) {
    val options = listOf("Handwritten Text to Text", "Text Image to Text")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = selectedOption.value == option
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            val textColor = MaterialTheme.colorScheme.onBackground
            val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface

            Box(
                modifier = Modifier
                    .border(width = if (isSelected) 3.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
                    .background(backgroundColor, shape = RoundedCornerShape(12.dp))
                    .clickable { selectedOption.value = option }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}
