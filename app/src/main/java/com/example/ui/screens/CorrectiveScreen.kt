package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.CorrectiveMaintenanceEntity
import com.example.ui.components.CorrectiveStatusBadge
import com.example.ui.components.DateUtils
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.MaintPrimary
import com.example.ui.theme.StatusFalla
import com.example.ui.theme.StatusOperativo
import com.example.viewmodel.MaintenanceViewModel

@Composable
fun CorrectiveScreen(
    viewModel: MaintenanceViewModel,
    onOpenAddCorrective: () -> Unit
) {
    val correctives by viewModel.correctiveList.collectAsStateWithLifecycle()
    val equipmentList by viewModel.equipmentList.collectAsStateWithLifecycle()

    var filterStatus by remember { mutableStateOf("TODOS") }
    var searchQuery by remember { mutableStateOf("") }

    var correctiveToResolve by remember { mutableStateOf<CorrectiveMaintenanceEntity?>(null) }
    var correctiveToDelete by remember { mutableStateOf<CorrectiveMaintenanceEntity?>(null) }
    var correctiveToEdit by remember { mutableStateOf<CorrectiveMaintenanceEntity?>(null) }

    val filteredList = correctives.filter { item ->
        val matchesFilter = when (filterStatus) {
            "TODOS" -> true
            "CRITICA" -> item.severity == "CRITICA" && item.status != "SOLUCIONADO"
            "ESPERANDO_REPUESTOS" -> item.status == "ESPERANDO_REPUESTOS"
            "EN_REPARACION" -> item.status == "EN_REPARACION"
            "EN_DIAGNOSTICO" -> item.status == "EN_DIAGNOSTICO"
            "SOLUCIONADO" -> item.status == "SOLUCIONADO"
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                item.equipmentTag.contains(searchQuery, ignoreCase = true) ||
                item.equipmentName.contains(searchQuery, ignoreCase = true) ||
                item.failureDescription.contains(searchQuery, ignoreCase = true) ||
                item.requiredSpareParts.contains(searchQuery, ignoreCase = true)

        matchesFilter && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddCorrective,
                containerColor = StatusFalla,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_corrective")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Reportar Falla Correctiva")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("corrective_screen")
        ) {
            // Header Description
            Surface(
                color = Color(0xFFFFEBEE),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = StatusFalla,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Gestión de Mantenimientos Correctivos y Repuestos",
                        color = Color(0xFFB71C1C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("corrective_search_input"),
                placeholder = { Text("Buscar falla, equipo, o código de repuesto...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = filterStatus == "TODOS",
                        onClick = { filterStatus = "TODOS" },
                        label = { Text("Todos (${correctives.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterStatus == "CRITICA",
                        onClick = { filterStatus = "CRITICA" },
                        label = { Text("🚨 Críticos") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterStatus == "ESPERANDO_REPUESTOS",
                        onClick = { filterStatus = "ESPERANDO_REPUESTOS" },
                        label = { Text("Esperando Repuestos") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterStatus == "EN_REPARACION",
                        onClick = { filterStatus = "EN_REPARACION" },
                        label = { Text("En Reparación") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterStatus == "EN_DIAGNOSTICO",
                        onClick = { filterStatus = "EN_DIAGNOSTICO" },
                        label = { Text("En Diagnóstico") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterStatus == "SOLUCIONADO",
                        onClick = { filterStatus = "SOLUCIONADO" },
                        label = { Text("Solucionados") }
                    )
                }
            }

            // Corrective Items List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Construction,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No hay fallas correctivas registradas",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Use el botón '+' para reportar una falla inesperada.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        CorrectiveItemCard(
                            item = item,
                            onResolve = { correctiveToResolve = item },
                            onEdit = { correctiveToEdit = item },
                            onDelete = { correctiveToDelete = item }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            }
        }
    }

    // Resolve Corrective Dialog
    correctiveToResolve?.let { cor ->
        var solutionSummary by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { correctiveToResolve = null },
            title = { Text("Solucionar Mantenimiento Correctivo") },
            text = {
                Column {
                    Text(
                        text = "Registrar solución para ${cor.equipmentTag} (${cor.equipmentName}):",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = solutionSummary,
                        onValueChange = { solutionSummary = it },
                        label = { Text("Acciones tomadas y pruebas realizadas") },
                        placeholder = { Text("Ej. Se reemplazó el kit de rodamientos SKF, ajuste de correa y pruebas a 2500 RPM satisfactorias...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resolveCorrective(cor.id, solutionSummary)
                        // Also restore equipment status to OPERATIVO
                        viewModel.updateEquipmentStatus(cor.equipmentId, "OPERATIVO")
                        correctiveToResolve = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusOperativo)
                ) {
                    Text("Marcar Solucionado")
                }
            },
            dismissButton = {
                TextButton(onClick = { correctiveToResolve = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Delete Corrective Dialog
    correctiveToDelete?.let { cor ->
        AlertDialog(
            onDismissRequest = { correctiveToDelete = null },
            title = { Text("Eliminar Registro Correctivo") },
            text = { Text("¿Desea eliminar el correctivo de ${cor.equipmentTag}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCorrective(cor)
                        correctiveToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { correctiveToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Edit Corrective Dialog
    correctiveToEdit?.let { cor ->
        var equipmentName by remember { mutableStateOf(cor.equipmentName) }
        var equipmentNumber by remember { mutableStateOf(cor.equipmentNumber) }
        var model by remember { mutableStateOf(cor.model) }
        var failureDesc by remember { mutableStateOf(cor.failureDescription) }
        var spareParts by remember { mutableStateOf(cor.requiredSpareParts) }
        var status by remember { mutableStateOf(cor.status) }
        var technician by remember { mutableStateOf(cor.assignedTechnician) }

        AlertDialog(
            onDismissRequest = { correctiveToEdit = null },
            title = { Text("Editar Correctivo: ${cor.equipmentTag}") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = equipmentName,
                            onValueChange = { equipmentName = it },
                            label = { Text("Nombre del Equipo") },
                            modifier = Modifier.weight(1.3f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = equipmentNumber,
                            onValueChange = { equipmentNumber = it },
                            label = { Text("Nº Equipo") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Modelo del Equipo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = failureDesc,
                        onValueChange = { failureDesc = it },
                        label = { Text("Descripción de la falla") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = spareParts,
                        onValueChange = { spareParts = it },
                        label = { Text("Repuestos necesarios") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = technician,
                        onValueChange = { technician = it },
                        label = { Text("Técnico asignado") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCorrective(
                            cor.copy(
                                equipmentName = equipmentName,
                                equipmentNumber = equipmentNumber,
                                model = model,
                                failureDescription = failureDesc,
                                requiredSpareParts = spareParts,
                                assignedTechnician = technician,
                                status = status
                            )
                        )
                        correctiveToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaintPrimary)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { correctiveToEdit = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CorrectiveItemCard(
    item: CorrectiveMaintenanceEntity,
    onResolve: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("corrective_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Equipment tag + Severity + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.equipmentTag,
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    SeverityBadge(severity = item.severity)
                }
                CorrectiveStatusBadge(status = item.status)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.equipmentName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.equipmentNumber,
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (item.model.isNotBlank()) {
                Text(
                    text = "Modelo: ${item.model}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Box: DESCRIPCIÓN DE LA FALLA
            Surface(
                color = Color(0xFFFFF1F2),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Descripción de la Falla:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF9F1239)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.failureDescription,
                        fontSize = 12.sp,
                        color = Color(0xFF881337),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Box: REPUESTOS NECESARIOS PARA LA REPARACIÓN
            Surface(
                color = Color(0xFFFFF7ED),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Inventory,
                            contentDescription = null,
                            tint = Color(0xFFEA580C),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Repuesto necesario para la reparación:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF9A3412)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.requiredSpareParts,
                        fontSize = 12.sp,
                        color = Color(0xFF7C2D12),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cronograma Scheduled Intervention & Parada (Downtime)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFEEF2F6),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "En Cronograma: ${DateUtils.formatEpochDay(item.scheduledInterventionEpochDay)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155)
                        )
                    }
                }

                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Parada: ${item.downtimeHours}h",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
            }

            // Solution if solved
            if (!item.solutionSummary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "✓ Reparación Completada:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = item.solutionSummary,
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Technician & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Engineering,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.assignedTechnician,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row {
                    if (item.status != "SOLUCIONADO") {
                        IconButton(onClick = onResolve) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Resolver",
                                tint = StatusOperativo
                            )
                        }
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color(0xFF0284C7)
                        )
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
    }
}
