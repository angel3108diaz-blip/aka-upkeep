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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.local.MaintenanceScheduleEntity
import com.example.ui.components.DateUtils
import com.example.ui.components.ScheduleStatusBadge
import com.example.ui.theme.MaintPrimary
import com.example.ui.theme.MaintSecondary
import com.example.ui.theme.StatusOperativo
import com.example.viewmodel.MaintenanceViewModel

@Composable
fun ScheduleScreen(
    viewModel: MaintenanceViewModel,
    onOpenAddSchedule: () -> Unit
) {
    val schedules by viewModel.scheduleList.collectAsStateWithLifecycle()
    val equipmentList by viewModel.equipmentList.collectAsStateWithLifecycle()

    var filterStatus by remember { mutableStateOf("TODOS") }
    var searchQuery by remember { mutableStateOf("") }

    var scheduleToComplete by remember { mutableStateOf<MaintenanceScheduleEntity?>(null) }
    var scheduleToEdit by remember { mutableStateOf<MaintenanceScheduleEntity?>(null) }
    var scheduleToDelete by remember { mutableStateOf<MaintenanceScheduleEntity?>(null) }

    val filteredList = schedules.filter { item ->
        val matchesFilter = when (filterStatus) {
            "TODOS" -> true
            "48_HORAS" -> item.status != "COMPLETADO" && DateUtils.isWithinNext48Hours(item.scheduledEpochDay)
            "PROGRAMADO" -> item.status == "PROGRAMADO"
            "EN_PROGRESO" -> item.status == "EN_PROGRESO"
            "COMPLETADO" -> item.status == "COMPLETADO"
            "VENCIDO" -> item.status != "COMPLETADO" && DateUtils.isOverdue(item.scheduledEpochDay)
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                item.equipmentTag.contains(searchQuery, ignoreCase = true) ||
                item.equipmentName.contains(searchQuery, ignoreCase = true) ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.requiredTools.contains(searchQuery, ignoreCase = true)

        matchesFilter && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddSchedule,
                containerColor = MaintPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_schedule")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Programar Mantenimiento")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("schedule_screen")
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("schedule_search_input"),
                placeholder = { Text("Buscar por equipo, tag, tarea o herramientas...") },
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
                        label = { Text("Todos (${schedules.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterStatus == "48_HORAS",
                        onClick = { filterStatus = "48_HORAS" },
                        label = { Text("⚠️ En 48h") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterStatus == "PROGRAMADO",
                        onClick = { filterStatus = "PROGRAMADO" },
                        label = { Text("Programados") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterStatus == "EN_PROGRESO",
                        onClick = { filterStatus = "EN_PROGRESO" },
                        label = { Text("En Progreso") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterStatus == "VENCIDO",
                        onClick = { filterStatus = "VENCIDO" },
                        label = { Text("Vencidos") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterStatus == "COMPLETADO",
                        onClick = { filterStatus = "COMPLETADO" },
                        label = { Text("Completados") }
                    )
                }
            }

            // Schedule Items List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No se encontraron intervenciones",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Pruebe cambiando los filtros o programe una nueva.",
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
                    items(filteredList, key = { it.id }) { schedule ->
                        ScheduleItemCard(
                            schedule = schedule,
                            onComplete = { scheduleToComplete = schedule },
                            onEdit = { scheduleToEdit = schedule },
                            onDelete = { scheduleToDelete = schedule }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            }
        }
    }

    // Complete Maintenance Dialog
    scheduleToComplete?.let { sch ->
        var completionNotes by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { scheduleToComplete = null },
            title = { Text("Completar Mantenimiento") },
            text = {
                Column {
                    Text(
                        text = "¿Desea registrar como completado el mantenimiento de ${sch.equipmentTag}?",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = completionNotes,
                        onValueChange = { completionNotes = it },
                        label = { Text("Notas de finalización y observaciones") },
                        placeholder = { Text("Ej. Rodamientos instalados sin novedades, prueba de vibración superada...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markScheduleCompleted(sch.id, completionNotes)
                        scheduleToComplete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusOperativo)
                ) {
                    Text("Marcar Completado")
                }
            },
            dismissButton = {
                TextButton(onClick = { scheduleToComplete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    scheduleToDelete?.let { sch ->
        AlertDialog(
            onDismissRequest = { scheduleToDelete = null },
            title = { Text("Eliminar Intervención") },
            text = { Text("¿Está seguro de eliminar la intervención '${sch.title}' para ${sch.equipmentTag}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSchedule(sch)
                        scheduleToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { scheduleToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Edit Schedule Dialog
    scheduleToEdit?.let { sch ->
        EditScheduleDialog(
            schedule = sch,
            equipmentList = equipmentList,
            onDismiss = { scheduleToEdit = null },
            onSave = { updated ->
                viewModel.updateSchedule(updated)
                scheduleToEdit = null
            }
        )
    }
}

@Composable
fun ScheduleItemCard(
    schedule: MaintenanceScheduleEntity,
    onComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val is48h = schedule.status != "COMPLETADO" && DateUtils.isWithinNext48Hours(schedule.scheduledEpochDay)
    val isOverdue = schedule.status != "COMPLETADO" && DateUtils.isOverdue(schedule.scheduledEpochDay)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("schedule_card_${schedule.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Equipment Tag, Type pill and Status
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
                            text = schedule.equipmentTag,
                            color = MaintPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = schedule.type,
                            color = Color(0xFF475569),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
                ScheduleStatusBadge(status = schedule.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Equipment Name and Unit Number
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = schedule.equipmentName,
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
                        text = schedule.equipmentNumber,
                        color = MaintPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (schedule.model.isNotBlank()) {
                Text(
                    text = "Modelo: ${schedule.model}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = schedule.title,
                fontSize = 13.sp,
                color = Color(0xFF334155),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Date & Relative Time Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when {
                        isOverdue -> Color(0xFFFFEBEE)
                        is48h -> Color(0xFFFFF3E0)
                        schedule.status == "COMPLETADO" -> Color(0xFFE8F5E9)
                        else -> Color(0xFFE0F2FE)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = when {
                                isOverdue -> Color(0xFFC62828)
                                is48h -> Color(0xFFE65100)
                                schedule.status == "COMPLETADO" -> Color(0xFF2E7D32)
                                else -> Color(0xFF0284C7)
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${DateUtils.formatEpochDay(schedule.scheduledEpochDay)} • ${schedule.scheduledTime}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = when {
                                isOverdue -> Color(0xFFC62828)
                                is48h -> Color(0xFFBF360C)
                                schedule.status == "COMPLETADO" -> Color(0xFF1B5E20)
                                else -> Color(0xFF0369A1)
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${DateUtils.formatRelativeDays(schedule.scheduledEpochDay)})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when {
                                isOverdue -> Color(0xFFC62828)
                                is48h -> Color(0xFFD84315)
                                else -> Color(0xFF0284C7)
                            }
                        )
                    }
                }

                Text(
                    text = "Frecuencia: ${schedule.frequency}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Box for Required Tools & Equipment
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            tint = MaintSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Equipos y herramientas necesarios:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = schedule.requiredTools.ifBlank { "No especificado" },
                        fontSize = 12.sp,
                        color = Color(0xFF334155)
                    )
                }
            }

            // Box for Maintenance Notes (Apartado para notas de cada mantenimiento)
            if (schedule.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Notes,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Notas del mantenimiento:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = schedule.notes,
                            fontSize = 12.sp,
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }

            // Completion Notes if completed
            if (!schedule.completionNotes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "✓ Observaciones de finalización:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = schedule.completionNotes,
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Technician & Action Buttons
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
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${schedule.assignedTechnician} (${schedule.estimatedDurationHours}h)",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row {
                    if (schedule.status != "COMPLETADO") {
                        IconButton(onClick = onComplete) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Completar",
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

@Composable
fun EditScheduleDialog(
    schedule: MaintenanceScheduleEntity,
    equipmentList: List<EquipmentEntity>,
    onDismiss: () -> Unit,
    onSave: (MaintenanceScheduleEntity) -> Unit
) {
    var equipmentName by remember { mutableStateOf(schedule.equipmentName) }
    var equipmentNumber by remember { mutableStateOf(schedule.equipmentNumber) }
    var model by remember { mutableStateOf(schedule.model) }
    var title by remember { mutableStateOf(schedule.title) }
    var requiredTools by remember { mutableStateOf(schedule.requiredTools) }
    var notes by remember { mutableStateOf(schedule.notes) }
    var technician by remember { mutableStateOf(schedule.assignedTechnician) }
    var frequencyDaysInput by remember { mutableStateOf(schedule.frequencyDays.toString()) }
    var time by remember { mutableStateOf(schedule.scheduledTime) }
    var status by remember { mutableStateOf(schedule.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Mantenimiento: ${schedule.equipmentTag}") },
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
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la intervención") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = frequencyDaysInput,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            frequencyDaysInput = input
                        }
                    },
                    label = { Text("Frecuencia (cantidad de días) *") },
                    placeholder = { Text("Ej. 30") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("7", "15", "30", "60", "90", "180", "365").forEach { days ->
                        FilterChip(
                            selected = frequencyDaysInput == days,
                            onClick = { frequencyDaysInput = days },
                            label = { Text("${days}d", fontSize = 11.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = requiredTools,
                    onValueChange = { requiredTools = it },
                    label = { Text("Equipos y herramientas necesarios") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Apartado para notas / instrucciones") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = technician,
                        onValueChange = { technician = it },
                        label = { Text("Técnico") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Hora") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val daysInt = frequencyDaysInput.toIntOrNull() ?: schedule.frequencyDays
                    val freqText = if (daysInt > 0) "Cada $daysInt días" else "Puntual"
                    onSave(
                        schedule.copy(
                            equipmentName = equipmentName,
                            equipmentNumber = equipmentNumber,
                            model = model,
                            title = title,
                            frequencyDays = daysInt,
                            frequency = freqText,
                            requiredTools = requiredTools,
                            notes = notes,
                            assignedTechnician = technician,
                            scheduledTime = time,
                            status = status
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaintPrimary)
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
