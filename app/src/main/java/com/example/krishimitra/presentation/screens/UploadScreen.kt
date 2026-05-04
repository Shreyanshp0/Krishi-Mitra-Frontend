package com.example.krishimitra.presentation.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.krishimitra.presentation.viewmodel.ExtractedSoilData
import com.example.krishimitra.presentation.viewmodel.SHCUploadUiState
import com.example.krishimitra.presentation.viewmodel.SHCUploadViewModel
import com.example.krishimitra.ui.theme.*
import com.example.krishimitra.ui.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    onBack: () -> Unit,
    onNavigateToForm: () -> Unit,
    viewModel: SHCUploadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileName(context, it) ?: "SoilHealthCard.pdf"
            viewModel.onFileSelected(it, fileName)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Soil Health Card", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepGreen)
            )
        },
        containerColor = LightBeige
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimensions.SCREEN_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.CARD_SPACING)
        ) {
            item {
                Text(
                    text = "Upload your Soil Health Card (SHC) to automatically fill in details.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkCharcoal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // SECTION 1: Upload Card / Idle State
            item {
                if (uiState is SHCUploadUiState.Idle || uiState is SHCUploadUiState.Error) {
                    UploadBox(onUploadClick = {
                        launcher.launch(arrayOf("image/*", "application/pdf"))
                    })
                }
            }

            // SECTION 2: File Preview
            item {
                AnimatedVisibility(
                    visible = uiState is SHCUploadUiState.FileSelected,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val state = uiState as? SHCUploadUiState.FileSelected
                    if (state != null) {
                        FilePreviewCard(
                            fileName = state.fileName,
                            onRemove = { viewModel.removeFile() }
                        )
                    }
                }
            }

            // SECTION 3: Process Button
            item {
                if (uiState is SHCUploadUiState.FileSelected) {
                    Button(
                        onClick = { viewModel.analyzeSoilData() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepGreen)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyze Soil Data", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // SECTION 4: Loading State
            item {
                if (uiState is SHCUploadUiState.Processing) {
                    ProcessingState()
                }
            }

            // SECTION 5: Extracted Data
            item {
                if (uiState is SHCUploadUiState.Success) {
                    ExtractedDataCard(
                        data = (uiState as SHCUploadUiState.Success).data,
                        onSave = { viewModel.saveSoilData() }
                    )
                }
            }

            // SECTION 6: Error State
            item {
                if (uiState is SHCUploadUiState.Error) {
                    ErrorStateView(
                        message = (uiState as SHCUploadUiState.Error).message,
                        onRetry = { viewModel.retry() }
                    )
                }
            }

            // Footer
            item {
                TextButton(onClick = onNavigateToForm) {
                    Text("Or Fill Details Manually", color = SoilBrown, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UploadBox(onUploadClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onUploadClick() },
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null,
                tint = DeepGreen,
                modifier = Modifier.size(Dimensions.ICON_SIZE_LARGE)
            )
            Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
            Text(
                "Upload Soil Health Card",
                style = MaterialTheme.typography.titleMedium,
                color = DeepGreen,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Supported: Image or PDF",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
            Button(
                onClick = onUploadClick,
                colors = ButtonDefaults.buttonColors(containerColor = DeepGreen.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM)
            ) {
                Text("Choose File", color = DeepGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FilePreviewCard(fileName: String, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Description, contentDescription = null, tint = SoilBrown, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(fileName, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("Ready to analyze", style = MaterialTheme.typography.bodySmall, color = LeafGreen)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}

@Composable
fun ProcessingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = DeepGreen)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Reading your soil data...", fontWeight = FontWeight.Medium, color = DeepGreen)
        Text("This may take a few seconds", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun ExtractedDataCard(data: ExtractedSoilData, onSave: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "✨ Extracted Soil Data",
                style = MaterialTheme.typography.titleMedium,
                color = DeepGreen,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            SoilDataItem(label = "Nitrogen (N)", value = data.nitrogen, icon = Icons.Default.Science)
            SoilDataItem(label = "Phosphorus (P)", value = data.phosphorus, icon = Icons.Default.Science)
            SoilDataItem(label = "Potassium (K)", value = data.potassium, icon = Icons.Default.Science)
            SoilDataItem(label = "Soil pH", value = data.ph, icon = Icons.Default.WaterDrop)

            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepGreen)
            ) {
                Text("Save Soil Data", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SoilDataItem(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(LightBeige),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = SoilBrown, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, modifier = Modifier.weight(1f), color = DarkCharcoal)
        
        Surface(
            color = if (value.contains("Low", true)) Amber.copy(alpha = 0.2f) else LeafGreen.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = if (value.contains("Low", true)) Color(0xFF916400) else DeepGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ErrorStateView(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Red.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Red)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                textAlign = TextAlign.Center,
                color = Red,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Retry")
            }
        }
    }
}

private fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}
