package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AttachmentEntity
import com.example.data.local.CorrectiveMaintenanceEntity
import com.example.data.local.EquipmentEntity
import com.example.data.local.MaintenanceScheduleEntity
import com.example.data.local.PhotoReportEntity
import com.example.ui.components.DateUtils
import com.example.ui.theme.MaintPrimary
import com.example.ui.theme.StatusFalla

@Composable
fun AddMaintenanceScheduleDialog(
    equipmentList: List<EquipmentEntity>,
    preselectedDateEpochDay: Long? = null,
    onDismiss: () -> Unit,
    onConfirm: (MaintenanceScheduleEntity) -> Unit
) {
    var selectedEquipment by remember {
        mutableStateOf<EquipmentEntity?>(null)
    }

    // All input boxes start empty initially (no pre-filled text)
    var equipmentName by remember { mutableStateOf("") }
    var equipmentNumber by remember { mutableStateOf("") }
    var equipmentModel by remember { mutableStateOf("") }
    var equipmentTag by remember { mutableStateOf("") }

    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    val today = remember { DateUtils.todayEpochDay() }
    var scheduledDaysOffset by remember {
        mutableLongStateOf(
            if (preselectedDateEpochDay != null) preselectedDateEpochDay - today else 2L
        )
    }

    var time by remember { mutableStateOf("") }
    var frequencyDaysInput by remember { mutableStateOf("") } // Frecuencia numérica en días
    var requiredTools by remember { mutableStateOf("") }
    var technician by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var expandedEquipmentDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Programar Mantenimiento", fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                // Selector de Equipos existentes
                item {
                    Text(
                        text = "Seleccionar equipo registrado (opcional):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedEquipmentDropdown = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedEquipment != null) {
                                        "${selectedEquipment?.name} [${selectedEquipment?.equipmentNumber}]"
                                    } else {
                                        "Tocar para elegir equipo de la lista..."
                                    },
                                    fontWeight = if (selectedEquipment != null) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedEquipment != null) Color(0xFF0F172A) else Color.Gray
                                )
                                if (selectedEquipment != null) {
                                    Text(
                                        text = "Modelo: ${selectedEquipment?.model} • Tag: ${selectedEquipment?.tag}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = expandedEquipmentDropdown,
                            onDismissRequest = { expandedEquipmentDropdown = false }
                        ) {
                            for (eq in equipmentList) {
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                "${eq.name} [${eq.equipmentNumber}]",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                "Modelo: ${eq.model} • Tag: ${eq.tag}",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedEquipment = eq
                                        equipmentName = eq.name
                                        equipmentNumber = eq.equipmentNumber
                                        equipmentModel = eq.model
                                        equipmentTag = eq.tag
                                        expandedEquipmentDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Identificación del Equipo a Intervenir (Nombre, Número y Modelo)
                item {
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Equipo a intervenir (personalizar unidad):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = equipmentName,
                                onValueChange = { equipmentName = it },
                                label = { Text("Nombre del Equipo *") },
                                placeholder = { Text("Ej. Bomba Centrífuga, Compresor...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = equipmentNumber,
                                    onValueChange = { equipmentNumber = it },
                                    label = { Text("Nº / Unidad de Equipo *") },
                                    placeholder = { Text("Ej. Bomba Nº 02 / Unidad 1") },
                                    modifier = Modifier.weight(1.3f),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                OutlinedTextField(
                                    value = equipmentTag,
                                    onValueChange = { equipmentTag = it },
                                    label = { Text("Tag") },
                                    placeholder = { Text("Ej. BOM-02") },
                                    modifier = Modifier.weight(0.9f),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = equipmentModel,
                                onValueChange = { equipmentModel = it },
                                label = { Text("Modelo del Equipo") },
                                placeholder = { Text("Ej. KSB Multitec 50/3, GA37...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Tarea de mantenimiento
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título / Tarea de Mantenimiento *") },
                        placeholder = { Text("Ej. Cambio de rodamientos, lubricación...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Selección de fecha programada
                item {
                    Text("Fecha de Mantenimiento:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = scheduledDaysOffset == 0L,
                            onClick = { scheduledDaysOffset = 0L },
                            label = { Text("Hoy") }
                        )
                        FilterChip(
                            selected = scheduledDaysOffset == 1L,
                            onClick = { scheduledDaysOffset = 1L },
                            label = { Text("Mañana") }
                        )
                        FilterChip(
                            selected = scheduledDaysOffset == 2L,
                            onClick = { scheduledDaysOffset = 2L },
                            label = { Text("En 2 días (Alerta)") }
                        )
                        FilterChip(
                            selected = scheduledDaysOffset == 7L,
                            onClick = { scheduledDaysOffset = 7L },
                            label = { Text("+1 sem") }
                        )
                    }

                    Text(
                        text = "📅 Fecha: ${DateUtils.formatEpochDay(today + scheduledDaysOffset)}",
                        fontSize = 12.sp,
                        color = Color(0xFF0369A1),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Tipo y Frecuencia en CANTIDAD DE DÍAS (NUMÉRICO)
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = type,
                            onValueChange = { type = it },
                            label = { Text("Tipo") },
                            placeholder = { Text("Ej. Preventivo") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = frequencyDaysInput,
                            onValueChange = { input ->
                                if (input.all { it.isDigit() }) {
                                    frequencyDaysInput = input
                                }
                            },
                            label = { Text("Frecuencia (días) *") },
                            placeholder = { Text("Ej. 30") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    // Chips rápidos para días comunes
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

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Equipos y herramientas necesarios
                item {
                    OutlinedTextField(
                        value = requiredTools,
                        onValueChange = { requiredTools = it },
                        label = { Text("Equipos y herramientas necesarios *") },
                        placeholder = { Text("Ej. Torquímetro 1/2 pulg, Extractor SKF, EPP...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Apartado de Notas
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Apartado para notas del mantenimiento") },
                        placeholder = { Text("Instrucciones específicas, tolerancias, precauciones...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Técnico, Hora y Duración
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = technician,
                            onValueChange = { technician = it },
                            label = { Text("Técnico Asignado") },
                            placeholder = { Text("Ej. Ing. Carlos Mendoza") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Hora") },
                            placeholder = { Text("08:30") },
                            modifier = Modifier.weight(0.9f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = durationText,
                            onValueChange = { durationText = it },
                            label = { Text("Horas") },
                            placeholder = { Text("2.5") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val eqId = selectedEquipment?.id ?: 1L
                    val duration = durationText.toFloatOrNull() ?: 2.0f
                    val daysInt = frequencyDaysInput.toIntOrNull() ?: 30
                    val freqLabel = if (daysInt > 0) "Cada $daysInt días" else "Puntual"

                    val finalSchedule = MaintenanceScheduleEntity(
                        equipmentId = eqId,
                        equipmentTag = equipmentTag.ifBlank { "EQ-${System.currentTimeMillis() % 1000}" },
                        equipmentName = equipmentName.ifBlank { "Equipo Industrial" },
                        equipmentNumber = equipmentNumber.ifBlank { "Unidad 1" },
                        model = equipmentModel,
                        title = title.ifBlank { "Mantenimiento de ${equipmentName.ifBlank { "Equipo" }}" },
                        type = type.ifBlank { "PREVENTIVO" },
                        scheduledEpochDay = today + scheduledDaysOffset,
                        scheduledTime = time.ifBlank { "08:00" },
                        frequencyDays = daysInt,
                        frequency = freqLabel,
                        requiredTools = requiredTools.ifBlank { "Herramientas de mano y EPP" },
                        assignedTechnician = technician.ifBlank { "Personal Técnico" },
                        estimatedDurationHours = duration,
                        notes = notes,
                        status = "PROGRAMADO"
                    )
                    onConfirm(finalSchedule)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaintPrimary)
            ) {
                Text("Programar Mantenimiento")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddCorrectiveDialog(
    equipmentList: List<EquipmentEntity>,
    onDismiss: () -> Unit,
    onConfirm: (CorrectiveMaintenanceEntity) -> Unit
) {
    var selectedEquipment by remember {
        mutableStateOf<EquipmentEntity?>(null)
    }

    // All fields clean and empty initially
    var equipmentName by remember { mutableStateOf("") }
    var equipmentNumber by remember { mutableStateOf("") }
    var equipmentModel by remember { mutableStateOf("") }
    var equipmentTag by remember { mutableStateOf("") }

    var failureDescription by remember { mutableStateOf("") }
    var requiredSpareParts by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("ALTA") }
    var scheduledDaysOffset by remember { mutableLongStateOf(1L) }
    var requiredTools by remember { mutableStateOf("") }
    var technician by remember { mutableStateOf("") }
    var downtimeText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var expandedEquipmentDropdown by remember { mutableStateOf(false) }
    val today = remember { DateUtils.todayEpochDay() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = StatusFalla)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reportar Falla Correctiva", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                // Selector de Equipos existentes
                item {
                    Text(
                        text = "Seleccionar equipo registrado (opcional):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedEquipmentDropdown = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedEquipment != null) {
                                        "${selectedEquipment?.name} [${selectedEquipment?.equipmentNumber}]"
                                    } else {
                                        "Tocar para elegir equipo de la lista..."
                                    },
                                    fontWeight = if (selectedEquipment != null) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedEquipment != null) Color(0xFF0F172A) else Color.Gray
                                )
                                if (selectedEquipment != null) {
                                    Text(
                                        text = "Modelo: ${selectedEquipment?.model} • Tag: ${selectedEquipment?.tag}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = expandedEquipmentDropdown,
                            onDismissRequest = { expandedEquipmentDropdown = false }
                        ) {
                            for (eq in equipmentList) {
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                "${eq.name} [${eq.equipmentNumber}]",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                "Modelo: ${eq.model} • Tag: ${eq.tag}",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedEquipment = eq
                                        equipmentName = eq.name
                                        equipmentNumber = eq.equipmentNumber
                                        equipmentModel = eq.model
                                        equipmentTag = eq.tag
                                        expandedEquipmentDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Identificación de la unidad con falla
                item {
                    Surface(
                        color = Color(0xFFFFF1F2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = StatusFalla,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Identificación de la unidad con falla:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9F1239)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = equipmentName,
                                onValueChange = { equipmentName = it },
                                label = { Text("Nombre del Equipo *") },
                                placeholder = { Text("Ej. Torno CNC, Bomba, Compresor...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = equipmentNumber,
                                    onValueChange = { equipmentNumber = it },
                                    label = { Text("Nº / Unidad de Equipo *") },
                                    placeholder = { Text("Ej. Torno Nº 2 / Unidad 01") },
                                    modifier = Modifier.weight(1.3f),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                OutlinedTextField(
                                    value = equipmentTag,
                                    onValueChange = { equipmentTag = it },
                                    label = { Text("Tag") },
                                    placeholder = { Text("TOR-02") },
                                    modifier = Modifier.weight(0.9f),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = equipmentModel,
                                onValueChange = { equipmentModel = it },
                                label = { Text("Modelo del Equipo") },
                                placeholder = { Text("Ej. Haas ST-30") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Severidad
                item {
                    Text("Severidad de la Falla:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = severity == "CRITICA",
                            onClick = { severity = "CRITICA" },
                            label = { Text("🚨 Crítica") }
                        )
                        FilterChip(
                            selected = severity == "ALTA",
                            onClick = { severity = "ALTA" },
                            label = { Text("Alta") }
                        )
                        FilterChip(
                            selected = severity == "MEDIA",
                            onClick = { severity = "MEDIA" },
                            label = { Text("Media") }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Descripción de la Falla
                item {
                    OutlinedTextField(
                        value = failureDescription,
                        onValueChange = { failureDescription = it },
                        label = { Text("Descripción de la Falla *") },
                        placeholder = { Text("Detalle síntomas, ruidos, sobrecalentamiento, código de alarma...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Repuesto Necesario
                item {
                    OutlinedTextField(
                        value = requiredSpareParts,
                        onValueChange = { requiredSpareParts = it },
                        label = { Text("Repuesto Necesario para la Reparación *") },
                        placeholder = { Text("Ej. Rodamiento SKF 7014 (x2), Correa HTD 8M-1200...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Programación en Cronograma
                item {
                    Text("Programar intervención en el cronograma:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = scheduledDaysOffset == 0L,
                            onClick = { scheduledDaysOffset = 0L },
                            label = { Text("Hoy") }
                        )
                        FilterChip(
                            selected = scheduledDaysOffset == 1L,
                            onClick = { scheduledDaysOffset = 1L },
                            label = { Text("Mañana") }
                        )
                        FilterChip(
                            selected = scheduledDaysOffset == 3L,
                            onClick = { scheduledDaysOffset = 3L },
                            label = { Text("En 3 días") }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Herramientas requeridas
                item {
                    OutlinedTextField(
                        value = requiredTools,
                        onValueChange = { requiredTools = it },
                        label = { Text("Herramientas necesarias") },
                        placeholder = { Text("Ej. Extractor hidráulico, Multímetro...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Técnico y Parada de planta
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = technician,
                            onValueChange = { technician = it },
                            label = { Text("Técnico Asignado") },
                            placeholder = { Text("Ej. Téc. Roberto Silva") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = downtimeText,
                            onValueChange = { downtimeText = it },
                            label = { Text("Parada (hrs)") },
                            placeholder = { Text("4.0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val eqId = selectedEquipment?.id ?: 1L
                    val downtime = downtimeText.toFloatOrNull() ?: 2.0f
                    val cor = CorrectiveMaintenanceEntity(
                        equipmentId = eqId,
                        equipmentTag = equipmentTag.ifBlank { "COR-${System.currentTimeMillis() % 1000}" },
                        equipmentName = equipmentName.ifBlank { "Equipo Industrial" },
                        equipmentNumber = equipmentNumber.ifBlank { "Unidad 1" },
                        model = equipmentModel,
                        failureDescription = failureDescription.ifBlank { "Falla operacional en $equipmentName" },
                        requiredSpareParts = requiredSpareParts.ifBlank { "Repuestos bajo inspección" },
                        severity = severity.ifBlank { "ALTA" },
                        status = "ESPERANDO_REPUESTOS",
                        reportedEpochDay = today,
                        scheduledInterventionEpochDay = today + scheduledDaysOffset,
                        assignedTechnician = technician.ifBlank { "Personal Técnico" },
                        requiredTools = requiredTools,
                        downtimeHours = downtime,
                        notes = notes
                    )
                    onConfirm(cor)
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusFalla)
            ) {
                Text("Registrar Falla")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddEquipmentDialog(
    onDismiss: () -> Unit,
    onConfirm: (EquipmentEntity) -> Unit
) {
    // All fields completely empty initially
    var tag by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var equipmentNumber by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var criticality by remember { mutableStateOf("") }
    var hoursText by remember { mutableStateOf("") }
    var tools by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Nuevo Equipo / Activo", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    OutlinedTextField(
                        value = tag,
                        onValueChange = { tag = it },
                        label = { Text("Tag / Código del Equipo *") },
                        placeholder = { Text("Ej. BOM-03, COMP-103...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del Equipo *") },
                        placeholder = { Text("Ej. Bomba Centrífuga, Compresor...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = equipmentNumber,
                            onValueChange = { equipmentNumber = it },
                            label = { Text("Número / Unidad *") },
                            placeholder = { Text("Ej. Bomba Nº 02 / Unidad 1") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Modelo") },
                            placeholder = { Text("Ej. KSB 50/3, GA37...") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Categoría") },
                            placeholder = { Text("Ej. Hidráulica, Mecánica...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = criticality,
                            onValueChange = { criticality = it },
                            label = { Text("Criticidad") },
                            placeholder = { Text("Alta, Media o Baja") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Ubicación en Planta") },
                        placeholder = { Text("Ej. Nave 1, Sala Bombas...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    OutlinedTextField(
                        value = hoursText,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) hoursText = input
                        },
                        label = { Text("Horas de operación") },
                        placeholder = { Text("Ej. 1200") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    OutlinedTextField(
                        value = tools,
                        onValueChange = { tools = it },
                        label = { Text("Herramientas necesarias para su mantenimiento") },
                        placeholder = { Text("Ej. Llaves mixtas, Torquímetro, EPP...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notas y especificaciones técnicas") },
                        placeholder = { Text("Detalles técnicos, tolerancias, fabricante...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hours = hoursText.toIntOrNull() ?: 0
                    val entity = EquipmentEntity(
                        tag = tag.ifBlank { "EQ-${System.currentTimeMillis() % 1000}" },
                        name = name.ifBlank { "Nuevo Equipo" },
                        equipmentNumber = equipmentNumber.ifBlank { "Unidad 1" },
                        model = model,
                        category = category.ifBlank { "General" },
                        location = location.ifBlank { "Planta Principal" },
                        status = "OPERATIVO",
                        criticality = criticality.ifBlank { "MEDIA" },
                        operatingHours = hours,
                        defaultToolsRequired = tools.ifBlank { "Herramientas estándar" },
                        notes = notes
                    )
                    onConfirm(entity)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaintPrimary)
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddPhotoReportDialog(
    equipmentList: List<EquipmentEntity>,
    onDismiss: () -> Unit,
    onConfirm: (PhotoReportEntity) -> Unit
) {
    var selectedEquipment by remember { mutableStateOf<EquipmentEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf("ANTES") }
    var description by remember { mutableStateOf("") }
    var technician by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Reporte Fotográfico", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    Text("Equipo:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedDropdown = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (selectedEquipment != null) {
                                    "${selectedEquipment?.name} [${selectedEquipment?.equipmentNumber}] (${selectedEquipment?.tag})"
                                } else {
                                    "Seleccionar equipo..."
                                },
                                fontSize = 13.sp,
                                fontWeight = if (selectedEquipment != null) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selectedEquipment != null) Color(0xFF0F172A) else Color.Gray
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            for (eq in equipmentList) {
                                DropdownMenuItem(
                                    text = { Text("${eq.name} [${eq.equipmentNumber}] • ${eq.tag}") },
                                    onClick = { selectedEquipment = eq; expandedDropdown = false }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    Text("Etapa de Inspección:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = stage == "ANTES",
                            onClick = { stage = "ANTES" },
                            label = { Text("Antes") }
                        )
                        FilterChip(
                            selected = stage == "DURANTE",
                            onClick = { stage = "DURANTE" },
                            label = { Text("Durante") }
                        )
                        FilterChip(
                            selected = stage == "DESPUES",
                            onClick = { stage = "DESPUES" },
                            label = { Text("Después") }
                        )
                        FilterChip(
                            selected = stage == "FALLA",
                            onClick = { stage = "FALLA" },
                            label = { Text("Falla") }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título del reporte *") },
                        placeholder = { Text("Ej. Inspección de desgaste de faja, rodamientos...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción técnica y hallazgos") },
                        placeholder = { Text("Mediciones, grietas observadas, juego axial...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    OutlinedTextField(
                        value = technician,
                        onValueChange = { technician = it },
                        label = { Text("Técnico / Inspector") },
                        placeholder = { Text("Ej. Carlos Mendoza") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val eq = selectedEquipment
                    val eqId = eq?.id ?: 1L
                    val eqTag = eq?.tag ?: "EQ-01"
                    val eqName = eq?.name ?: "Equipo"
                    val eqNum = eq?.equipmentNumber ?: "Unidad 1"

                    val rep = PhotoReportEntity(
                        equipmentId = eqId,
                        equipmentTag = eqTag,
                        equipmentName = eqName,
                        equipmentNumber = eqNum,
                        title = title.ifBlank { "Reporte Fotográfico $stage" },
                        stage = stage,
                        description = description.ifBlank { "Inspección fotográfica de $eqName [$eqNum]" },
                        imageUri = "photo_report_${System.currentTimeMillis()}",
                        technician = technician.ifBlank { "Inspector Técnico" }
                    )
                    onConfirm(rep)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaintPrimary)
            ) {
                Text("Guardar Reporte")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddAttachmentDialog(
    equipmentList: List<EquipmentEntity>,
    onDismiss: () -> Unit,
    onConfirm: (AttachmentEntity) -> Unit
) {
    var selectedEquipment by remember { mutableStateOf<EquipmentEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("MANUAL_PDF") }
    var sizeDesc by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adjuntar Archivo Técnico", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nombre del archivo *") },
                    placeholder = { Text("Ej. Manual_Bomba_KSB.pdf") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = sizeDesc,
                    onValueChange = { sizeDesc = it },
                    label = { Text("Tamaño del archivo") },
                    placeholder = { Text("Ej. 3.5 MB") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas o descripción") },
                    placeholder = { Text("Especificaciones de alineación, plano eléctrico...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val eq = selectedEquipment
                    val eqId = eq?.id ?: 1L
                    val eqTag = eq?.tag ?: "EQ-01"
                    val eqName = eq?.name ?: "Equipo"
                    val eqNum = eq?.equipmentNumber ?: "Unidad 1"

                    val att = AttachmentEntity(
                        equipmentId = eqId,
                        equipmentTag = eqTag,
                        equipmentName = eqName,
                        equipmentNumber = eqNum,
                        title = title.ifBlank { "Documento_Tecnico.pdf" },
                        fileType = fileType,
                        fileSizeDescription = sizeDesc.ifBlank { "1.5 MB" },
                        notes = notes
                    )
                    onConfirm(att)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaintPrimary)
            ) {
                Text("Adjuntar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
