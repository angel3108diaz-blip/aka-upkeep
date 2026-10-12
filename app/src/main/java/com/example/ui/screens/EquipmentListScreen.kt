package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.data.local.EquipmentEntity
import com.example.ui.components.EquipmentStatusBadge
import com.example.ui.theme.MaintPrimary
import com.example.ui.theme.StatusAlerta
import com.example.ui.theme.StatusFalla
import com.example.ui.theme.StatusMantenimiento
import com.example.ui.theme.StatusOperativo
import com.example.viewmodel.MaintenanceViewModel

@Composable
fun EquipmentListScreen(
    viewModel: MaintenanceViewModel,
    onOpenAddEquipment: () -> Unit
) {
    val equipmentList by viewModel.equipmentList.collectAsStateWithLifecycle()
    val schedules by viewModel.scheduleList.collectAsStateWithLifecycle()
    val correctives by viewModel.correctiveList.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("TODAS") }

    var selectedEquipmentForDetail by remember { mutableStateOf<EquipmentEntity?>(null) }
    var equipmentToEdit by remember { mutableStateOf<EquipmentEntity?>(null) }
    var equipmentToDelete by remember { mutableStateOf<EquipmentEntity?>(null) }

    val categories = remember(equipmentList) {
        listOf("TODAS") + equipmentList.map { it.category }.distinct()
    }

    val filteredList = equipmentList.filter { eq ->
        val matchesCategory = selectedCategory == "TODAS" || eq.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                eq.tag.contains(searchQuery, ignoreCase = true) ||
                eq.name.contains(searchQuery, ignoreCase = true) ||
                eq.location.contains(searchQuery, ignoreCase = true) ||
                eq.category.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddEquipment,
                containerColor = MaintPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_equipment")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar Equipo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("equipment_screen")
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("equipment_search_input"),
                placeholder = { Text("Buscar por tag, nombre, categoría o planta...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Category Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PrecisionManufacturing,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No se encontraron equipos",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Agregue nuevos activos utilizando el botón '+'.",
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
                    items(filteredList, key = { it.id }) { equipment ->
                        val scheduledCount = schedules.count { it.equipmentId == equipment.id && it.status != "COMPLETADO" }
                        val activeFailures = correctives.count { it.equipmentId == equipment.id && it.status != "SOLUCIONADO" }

                        EquipmentItemCard(
                            equipment = equipment,
                            scheduledCount = scheduledCount,
                            activeFailures = activeFailures,
                            onClick = { selectedEquipmentForDetail = equipment },
                            onStatusChange = { newStatus ->
                                viewModel.updateEquipmentStatus(equipment.id, newStatus)
                            },
                            onEdit = { equipmentToEdit = equipment },
                            onDelete = { equipmentToDelete = equipment }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            }
        }
    }

    // Detail Dialog
    selectedEquipmentForDetail?.let { eq ->
        val eqSchedules = schedules.filter { it.equipmentId == eq.id }
        val eqCorrectives = correctives.filter { it.equipmentId == eq.id }

        AlertDialog(
            onDismissRequest = { selectedEquipmentForDetail = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${eq.tag} - Ficha Técnica", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    EquipmentStatusBadge(status = eq.status)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = eq.name,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaintPrimary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = eq.equipmentNumber,
                                    color = MaintPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (eq.model.isNotBlank()) {
                            Text(text = "🏷️ Modelo: ${eq.model}", fontSize = 13.sp, color = Color(0xFF475569))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "📍 Ubicación: ${eq.location}", fontSize = 13.sp, color = Color(0xFF475569))
                        Text(text = "⚙️ Categoría: ${eq.category}", fontSize = 13.sp, color = Color(0xFF475569))
                        Text(text = "⏱️ Horas de operación: ${eq.operatingHours} hrs", fontSize = 13.sp, color = Color(0xFF475569))
                        Text(text = "🎯 Criticidad: ${eq.criticality}", fontSize = 13.sp, color = Color(0xFF475569))

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Equipos/herramientas requeridos para su intervención:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = eq.defaultToolsRequired, fontSize = 12.sp, color = Color(0xFF334155))
                            }
                        }

                        if (eq.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Notas: ${eq.notes}", fontSize = 12.sp, color = Color(0xFF64748B))
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Historial en Cronograma (${eqSchedules.size}):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (eqSchedules.isEmpty()) {
                        item {
                            Text(text = "Sin mantenimientos agendados.", fontSize = 12.sp, color = Color.Gray)
                        }
                    } else {
                        items(eqSchedules) { s ->
                            Text(
                                text = "• [${s.status}] ${s.title} (${s.frequency})",
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }

                    if (eqCorrectives.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Fallas Correctivas Registradas (${eqCorrectives.size}):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = StatusFalla
                            )
                        }
                        items(eqCorrectives) { c ->
                            Text(
                                text = "• [${c.severity}] ${c.failureDescription.take(50)}... Repuesto: ${c.requiredSpareParts.take(40)}",
                                fontSize = 11.sp,
                                color = Color(0xFF7F1D1D),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedEquipmentForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaintPrimary)
                ) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Edit Equipment Dialog
    equipmentToEdit?.let { eq ->
        var name by remember { mutableStateOf(eq.name) }
        var equipmentNumber by remember { mutableStateOf(eq.equipmentNumber) }
        var model by remember { mutableStateOf(eq.model) }
        var location by remember { mutableStateOf(eq.location) }
        var category by remember { mutableStateOf(eq.category) }
        var tools by remember { mutableStateOf(eq.defaultToolsRequired) }
        var notes by remember { mutableStateOf(eq.notes) }

        AlertDialog(
            onDismissRequest = { equipmentToEdit = null },
            title = { Text("Editar Equipo: ${eq.tag}") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del equipo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = equipmentNumber,
                            onValueChange = { equipmentNumber = it },
                            label = { Text("Nº / Unidad *") },
                            modifier = Modifier.weight(1.2f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Modelo") },
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Ubicación / Planta") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tools,
                        onValueChange = { tools = it },
                        label = { Text("Herramientas necesarias") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateEquipment(
                            eq.copy(
                                name = name,
                                equipmentNumber = equipmentNumber,
                                model = model,
                                location = location,
                                category = category,
                                defaultToolsRequired = tools,
                                notes = notes
                            )
                        )
                        equipmentToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaintPrimary)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { equipmentToEdit = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    equipmentToDelete?.let { eq ->
        AlertDialog(
            onDismissRequest = { equipmentToDelete = null },
            title = { Text("Eliminar Equipo") },
            text = { Text("¿Desea eliminar definitivamente el equipo '${eq.tag} - ${eq.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEquipment(eq)
                        equipmentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { equipmentToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EquipmentItemCard(
    equipment: EquipmentEntity,
    scheduledCount: Int,
    activeFailures: Int,
    onClick: () -> Unit,
    onStatusChange: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("equipment_card_${equipment.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Tag, Status & Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaintPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = equipment.tag,
                            color = MaintPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = equipment.category,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    EquipmentStatusBadge(status = equipment.status)

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🟢 Marcar Operativo") },
                                onClick = { onStatusChange("OPERATIVO"); showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("🟠 En Mantenimiento") },
                                onClick = { onStatusChange("EN_MANTENIMIENTO"); showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("🔴 Fuera de Servicio") },
                                onClick = { onStatusChange("FUERA_DE_SERVICIO"); showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("🟡 En Alerta") },
                                onClick = { onStatusChange("EN_ALERTA"); showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("✏️ Editar Datos") },
                                onClick = { onEdit(); showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("🗑️ Eliminar") },
                                onClick = { onDelete(); showMenu = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = equipment.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = MaintPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = equipment.equipmentNumber,
                        color = MaintPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (equipment.model.isNotBlank()) {
                Text(
                    text = "Modelo: ${equipment.model}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = equipment.location,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tool summary
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Herramientas: ${equipment.defaultToolsRequired}",
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats footer: Scheduled interventions and Active failures
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color(0xFFE0F2FE),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "$scheduledCount programados",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0369A1),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    if (activeFailures > 0) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "⚠️ $activeFailures fallas",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${equipment.operatingHours}h operando",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
