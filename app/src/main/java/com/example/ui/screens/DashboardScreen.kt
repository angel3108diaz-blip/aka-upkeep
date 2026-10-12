package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.MaintenanceScheduleEntity
import com.example.ui.components.DateUtils
import com.example.ui.components.EquipmentStatusBadge
import com.example.ui.components.ScheduleStatusBadge
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.MaintPrimary
import com.example.ui.theme.MaintSecondary
import com.example.ui.theme.StatusAlerta
import com.example.ui.theme.StatusFalla
import com.example.ui.theme.StatusMantenimiento
import com.example.ui.theme.StatusOperativo
import com.example.viewmodel.MaintenanceViewModel

@Composable
fun DashboardScreen(
    viewModel: MaintenanceViewModel,
    onNavigateToSchedule: () -> Unit,
    onNavigateToCorrectives: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToEquipment: () -> Unit,
    onOpenAddScheduleDialog: () -> Unit,
    onOpenAddCorrectiveDialog: () -> Unit,
    onOpenNotificationCenter: () -> Unit
) {
    val stats by viewModel.operationalStats.collectAsStateWithLifecycle()
    val schedules by viewModel.scheduleList.collectAsStateWithLifecycle()
    val correctives by viewModel.correctiveList.collectAsStateWithLifecycle()

    val next48HoursList = schedules.filter {
        it.status != "COMPLETADO" && DateUtils.isWithinNext48Hours(it.scheduledEpochDay)
    }

    val activeCorrectives = correctives.filter { it.status != "SOLUCIONADO" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AkaUpkeep Gear Logo Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("akaupkeep_brand_header")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.akaupkeep_gear_logo_1791834837544),
                        contentDescription = "Logo AkaUpkeep Engranaje",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AkaUpkeep",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaintPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaintSecondary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "PRO",
                                    color = MaintSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Gestión de Mantenimiento Industrial",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Hero Card - Operational Availability & Real-Time Status
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaintPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("availability_kpi_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E676))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PANEL EN TIEMPO REAL",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Estado Operativo de Planta",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Availability Percentage Pill
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${stats.availabilityPercentage}%",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Disponibilidad",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Equipment status count summary pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatMiniBadge(
                            label = "Operativos",
                            count = stats.operativosCount,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        StatMiniBadge(
                            label = "En Manten.",
                            count = stats.enMantenimientoCount,
                            color = Color(0xFFFF9800),
                            modifier = Modifier.weight(1f)
                        )
                        StatMiniBadge(
                            label = "En Falla",
                            count = stats.fueraDeServicioCount,
                            color = Color(0xFFFF5252),
                            modifier = Modifier.weight(1f)
                        )
                        StatMiniBadge(
                            label = "En Alerta",
                            count = stats.enAlertaCount,
                            color = Color(0xFFFFD600),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 48-Hour Advance Notification Banner (if any)
        if (stats.upcoming48HoursCount > 0) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenNotificationCenter() }
                        .testTag("advance_alert_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaintSecondary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = MaintSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "¡Alerta Automática (48h de antelación)!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBF360C),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Hay ${stats.upcoming48HoursCount} intervenciones en las próximas 48 horas. Verifique repuestos y equipos.",
                                color = Color(0xFF5D4037),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenAddScheduleDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = MaintPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_quick_schedule")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Programar", fontSize = 13.sp)
                }

                Button(
                    onClick = onOpenAddCorrectiveDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusFalla),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_quick_corrective")
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reportar Falla", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onNavigateToCalendar,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_quick_calendar")
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Calendario", fontSize = 13.sp)
                }
            }
        }

        // Section: Intervenciones Próximas (48h / Urgentes)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = MaintPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Próximos Mantenimientos (48h)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Ver todo (${schedules.size})",
                    color = MaintPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToSchedule() }
                )
            }
        }

        if (next48HoursList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "No hay mantenimientos programados para las próximas 48 horas.",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(next48HoursList, key = { "dash_sch_${it.id}" }) { schedule ->
                DashboardScheduleCard(
                    schedule = schedule,
                    onClick = onNavigateToSchedule
                )
            }
        }

        // Section: Fallas Correctivas Activas
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = StatusFalla,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Mantenimientos Correctivos Activos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Ver pestaña (${activeCorrectives.size})",
                    color = StatusFalla,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToCorrectives() }
                )
            }
        }

        if (activeCorrectives.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusOperativo)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Excelente: No hay fallas correctivas pendientes.",
                            fontSize = 13.sp,
                            color = StatusOperativo,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            items(activeCorrectives, key = { "dash_cor_${it.id}" }) { cor ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCorrectives() }
                        .testTag("dash_corrective_card_${cor.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${cor.equipmentTag} • ${cor.equipmentName} [${cor.equipmentNumber}]",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            SeverityBadge(severity = cor.severity)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Falla: ${cor.failureDescription}",
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Construction,
                                    contentDescription = null,
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Repuestos: ${cor.requiredSpareParts.take(65)}...",
                                    fontSize = 11.sp,
                                    color = Color(0xFFBF360C),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
private fun StatMiniBadge(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DashboardScheduleCard(
    schedule: MaintenanceScheduleEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("dash_schedule_card_${schedule.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${schedule.equipmentTag} • ${schedule.equipmentName} [${schedule.equipmentNumber}]",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = schedule.title,
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                }
                ScheduleStatusBadge(status = schedule.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFE0F2FE),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "📅 ${DateUtils.formatRelativeDays(schedule.scheduledEpochDay)} (${schedule.scheduledTime})",
                        color = Color(0xFF0369A1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "🔧 ${schedule.assignedTechnician}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            if (schedule.requiredTools.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        tint = MaintSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Equipos requeridos: ${schedule.requiredTools}",
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
