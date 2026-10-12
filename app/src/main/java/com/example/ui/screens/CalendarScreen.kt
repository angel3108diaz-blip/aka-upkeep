package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DateUtils
import com.example.ui.components.ScheduleStatusBadge
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.MaintPrimary
import com.example.ui.theme.StatusFalla
import com.example.viewmodel.MaintenanceViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    viewModel: MaintenanceViewModel,
    onOpenAddScheduleForDate: (Long) -> Unit
) {
    val schedules by viewModel.scheduleList.collectAsStateWithLifecycle()
    val correctives by viewModel.correctiveList.collectAsStateWithLifecycle()

    val today = remember { LocalDate.now() }
    var currentMonthYear by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val selectedEpochDay = selectedDate.toEpochDay()

    val daySchedules = schedules.filter { it.scheduledEpochDay == selectedEpochDay }
    val dayCorrectives = correctives.filter { it.scheduledInterventionEpochDay == selectedEpochDay }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("calendar_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Calendar Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header: Month Navigation & Today button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { currentMonthYear = currentMonthYear.minusMonths(1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mes anterior")
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = DateUtils.formatMonthYear(currentMonthYear.year, currentMonthYear.monthValue),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        IconButton(onClick = { currentMonthYear = currentMonthYear.plusMonths(1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mes siguiente")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            currentMonthYear = YearMonth.now()
                            selectedDate = LocalDate.now()
                        }) {
                            Icon(Icons.Default.Today, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ir a Hoy", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Days of week header (Lun, Mar, Mié, Jue, Vie, Sáb, Dom)
                    val daysOfWeek = listOf("L", "M", "X", "J", "V", "S", "D")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (d in daysOfWeek) {
                            Text(
                                text = d,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar Grid Days
                    val firstDayOfMonth = currentMonthYear.atDay(1)
                    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1 = Mon, 7 = Sun
                    val daysInMonth = currentMonthYear.lengthOfMonth()

                    // Previous month pad
                    val totalSlots = ((firstDayOfWeek - 1 + daysInMonth + 6) / 7) * 7

                    for (week in 0 until (totalSlots / 7)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (dayCol in 1..7) {
                                val slotIndex = week * 7 + dayCol
                                val dayNumber = slotIndex - (firstDayOfWeek - 1)

                                if (dayNumber in 1..daysInMonth) {
                                    val cellDate = currentMonthYear.atDay(dayNumber)
                                    val cellEpoch = cellDate.toEpochDay()
                                    val isSelected = cellDate == selectedDate
                                    val isToday = cellDate == today

                                    val hasPreventive = schedules.any { it.scheduledEpochDay == cellEpoch }
                                    val hasCorrective = correctives.any { it.scheduledInterventionEpochDay == cellEpoch }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    isSelected -> MaintPrimary
                                                    isToday -> Color(0xFFE2E8F0)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .border(
                                                width = if (isToday && !isSelected) 1.5.dp else 0.dp,
                                                color = if (isToday && !isSelected) MaintPrimary else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedDate = cellDate },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "$dayNumber",
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = when {
                                                    isSelected -> Color.White
                                                    isToday -> MaintPrimary
                                                    else -> Color(0xFF1E293B)
                                                }
                                            )

                                            // Dots for maintenance events
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                if (hasPreventive) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(5.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isSelected) Color(0xFF67E8F9) else Color(0xFF0284C7))
                                                    )
                                                }
                                                if (hasCorrective) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(5.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isSelected) Color(0xFFFFA4A4) else StatusFalla)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Empty slot
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF0284C7)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Preventivo", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusFalla))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Correctivo", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }
        }

        // Section Header for Selected Day
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Intervenciones para el ${DateUtils.formatShort(selectedEpochDay)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "${daySchedules.size + dayCorrectives.size} programadas (${DateUtils.formatRelativeDays(selectedEpochDay)})",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Button(
                    onClick = { onOpenAddScheduleForDate(selectedEpochDay) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaintPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Programar", fontSize = 12.sp)
                }
            }
        }

        // Items for selected day
        if (daySchedules.isEmpty() && dayCorrectives.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sin intervenciones programadas para este día",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            // Preventive schedules on this day
            items(daySchedules, key = { "cal_sch_${it.id}" }) { sch ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔵 ${sch.equipmentTag} • ${sch.equipmentName} [${sch.equipmentNumber}]",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            ScheduleStatusBadge(status = sch.status)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = sch.title, fontSize = 13.sp, color = Color(0xFF334155))

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Equipos requeridos: ${sch.requiredTools}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569)
                                )
                                if (sch.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Notas: ${sch.notes}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Hora: ${sch.scheduledTime} • Técnico: ${sch.assignedTechnician} (${sch.estimatedDurationHours}h)",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Corrective items on this day
            items(dayCorrectives, key = { "cal_cor_${it.id}" }) { cor ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔴 ${cor.equipmentTag} • ${cor.equipmentName} [${cor.equipmentNumber}]",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = StatusFalla
                            )
                            SeverityBadge(severity = cor.severity)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Falla: ${cor.failureDescription}",
                            fontSize = 12.sp,
                            color = Color(0xFF334155)
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Repuesto: ${cor.requiredSpareParts}",
                                fontSize = 11.sp,
                                color = Color(0xFF9A3412),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}
