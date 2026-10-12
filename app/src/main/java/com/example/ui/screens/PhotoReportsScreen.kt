package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.AttachmentEntity
import com.example.data.local.EquipmentEntity
import com.example.data.local.PhotoReportEntity
import com.example.ui.components.DateUtils
import com.example.ui.theme.MaintPrimary
import com.example.ui.theme.MaintSecondary
import com.example.ui.theme.StatusFalla
import com.example.viewmodel.MaintenanceViewModel

@Composable
fun PhotoReportsScreen(
    viewModel: MaintenanceViewModel,
    onOpenAddPhotoReport: () -> Unit,
    onOpenAddAttachment: () -> Unit
) {
    val photoReports by viewModel.photoReports.collectAsStateWithLifecycle()
    val attachments by viewModel.attachments.collectAsStateWithLifecycle()
    val equipmentList by viewModel.equipmentList.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Fotos, 1 = Archivos
    var photoToViewFullscreen by remember { mutableStateOf<PhotoReportEntity?>(null) }
    var photoToDelete by remember { mutableStateOf<PhotoReportEntity?>(null) }
    var attachmentToDelete by remember { mutableStateOf<AttachmentEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) onOpenAddPhotoReport() else onOpenAddAttachment()
                },
                containerColor = MaintPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_media")
            ) {
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Default.AddPhotoAlternate else Icons.Default.AttachFile,
                    contentDescription = if (selectedTab == 0) "Agregar Foto" else "Agregar Archivo"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("photo_reports_screen")
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Reportes Fotográficos (${photoReports.size})") },
                    icon = { Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Archivos Adjuntos (${attachments.size})") },
                    icon = { Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            if (selectedTab == 0) {
                // Photo Reports List
                if (photoReports.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Sin reportes fotográficos",
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Documente con fotos el estado Antes, Durante o Después de la intervención.",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(photoReports, key = { it.id }) { report ->
                            PhotoReportCard(
                                report = report,
                                onClick = { photoToViewFullscreen = report },
                                onDelete = { photoToDelete = report }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(60.dp)) }
                    }
                }
            } else {
                // Attachments List
                if (attachments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Sin archivos adjuntos",
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Adjunte fichas técnicas, manuales PDF o certificados.",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(attachments, key = { it.id }) { att ->
                            AttachmentItemCard(
                                attachment = att,
                                onDelete = { attachmentToDelete = att }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(60.dp)) }
                    }
                }
            }
        }
    }

    // Fullscreen Photo Inspection Dialog
    photoToViewFullscreen?.let { report ->
        AlertDialog(
            onDismissRequest = { photoToViewFullscreen = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reporte: ${report.equipmentTag}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    StageBadge(stage = report.stage)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Photo Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Display generated icon or sample asset
                        AsyncImage(
                            model = R.drawable.ic_launcher_foreground,
                            contentDescription = report.title,
                            modifier = Modifier.size(140.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = report.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Hallazgos y Observaciones Técnicas:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = report.description,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Técnico: ${report.technician}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = DateUtils.formatTimestamp(report.timestamp),
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { photoToViewFullscreen = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaintPrimary)
                ) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Delete Photo Dialog
    photoToDelete?.let { rep ->
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            title = { Text("Eliminar Reporte Fotográfico") },
            text = { Text("¿Desea eliminar este reporte fotográfico de ${rep.equipmentTag}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePhotoReport(rep)
                        photoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Delete Attachment Dialog
    attachmentToDelete?.let { att ->
        AlertDialog(
            onDismissRequest = { attachmentToDelete = null },
            title = { Text("Eliminar Archivo Adjunto") },
            text = { Text("¿Desea eliminar el archivo '${att.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAttachment(att)
                        attachmentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { attachmentToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun PhotoReportCard(
    report: PhotoReportEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("photo_card_${report.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaintPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = report.equipmentTag,
                            color = MaintPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    StageBadge(stage = report.stage)
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFEF4444)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Photo preview header with placeholder or image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = R.drawable.ic_launcher_foreground,
                    contentDescription = null,
                    modifier = Modifier.size(90.dp)
                )

                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ZoomIn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver Detalle", color = Color.White, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = report.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = report.description,
                fontSize = 12.sp,
                color = Color(0xFF475569),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "👷 ${report.technician}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = DateUtils.formatTimestamp(report.timestamp),
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
fun AttachmentItemCard(
    attachment: AttachmentEntity,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when (attachment.fileType) {
                            "MANUAL_PDF" -> Color(0xFFFFEBEE)
                            "FICHA_TECNICA" -> Color(0xFFE0F2FE)
                            else -> Color(0xFFFFF3E0)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (attachment.fileType) {
                        "MANUAL_PDF" -> Icons.Default.PictureAsPdf
                        "FICHA_TECNICA" -> Icons.Default.Description
                        else -> Icons.Default.FilePresent
                    },
                    contentDescription = null,
                    tint = when (attachment.fileType) {
                        "MANUAL_PDF" -> Color(0xFFC62828)
                        "FICHA_TECNICA" -> Color(0xFF0284C7)
                        else -> Color(0xFFE65100)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attachment.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${attachment.equipmentTag} • ${attachment.fileSizeDescription}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                if (attachment.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = attachment.notes,
                        fontSize = 11.sp,
                        color = Color(0xFF475569)
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
fun StageBadge(stage: String) {
    val (color, bg) = when (stage) {
        "ANTES" -> Pair(Color(0xFF0284C7), Color(0xFFE0F2FE))
        "DURANTE" -> Pair(Color(0xFFE65100), Color(0xFFFFF3E0))
        "DESPUES" -> Pair(Color(0xFF1B5E20), Color(0xFFE8F5E9))
        "FALLA" -> Pair(StatusFalla, Color(0xFFFFEBEE))
        else -> Pair(Color.DarkGray, Color(0xFFEEEEEE))
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = stage,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
