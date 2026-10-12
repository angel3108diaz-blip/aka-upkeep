package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusAlerta
import com.example.ui.theme.StatusAlertaBg
import com.example.ui.theme.StatusFalla
import com.example.ui.theme.StatusFallaBg
import com.example.ui.theme.StatusMantenimiento
import com.example.ui.theme.StatusMantenimientoBg
import com.example.ui.theme.StatusOperativo
import com.example.ui.theme.StatusOperativoBg

@Composable
fun EquipmentStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (label, textColor, bgColor, icon) = when (status) {
        "OPERATIVO" -> Quad("Operativo", StatusOperativo, StatusOperativoBg, Icons.Default.CheckCircle)
        "EN_MANTENIMIENTO" -> Quad("En Mantenimiento", StatusMantenimiento, StatusMantenimientoBg, Icons.Default.Engineering)
        "FUERA_DE_SERVICIO" -> Quad("Fuera de Servicio", StatusFalla, StatusFallaBg, Icons.Default.Error)
        "EN_ALERTA" -> Quad("En Alerta", StatusAlerta, StatusAlertaBg, Icons.Default.Warning)
        else -> Quad(status, Color.DarkGray, Color(0xFFEEEEEE), Icons.Default.Warning)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScheduleStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (label, textColor, bgColor) = when (status) {
        "PROGRAMADO" -> Triple("Programado", Color(0xFF0D47A1), Color(0xFFE3F2FD))
        "EN_PROGRESO" -> Triple("En Progreso", Color(0xFFE65100), Color(0xFFFFF3E0))
        "COMPLETADO" -> Triple("Completado", Color(0xFF1B5E20), Color(0xFFE8F5E9))
        "VENCIDO" -> Triple("Vencido", Color(0xFFB71C1C), Color(0xFFFFEBEE))
        else -> Triple(status, Color.DarkGray, Color(0xFFEEEEEE))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun SeverityBadge(
    severity: String,
    modifier: Modifier = Modifier
) {
    val (label, textColor, bgColor) = when (severity) {
        "CRITICA" -> Triple("Crítica", Color(0xFFB71C1C), Color(0xFFFFEBEE))
        "ALTA" -> Triple("Alta", Color(0xFFE65100), Color(0xFFFFF3E0))
        "MEDIA" -> Triple("Media", Color(0xFFF57F17), Color(0xFFFFFDE7))
        "BAJA" -> Triple("Baja", Color(0xFF2E7D32), Color(0xFFE8F5E9))
        else -> Triple(severity, Color.DarkGray, Color(0xFFEEEEEE))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(textColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CorrectiveStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (label, textColor, bgColor) = when (status) {
        "REPORTADO" -> Triple("Reportado", Color(0xFFB71C1C), Color(0xFFFFEBEE))
        "EN_DIAGNOSTICO" -> Triple("En Diagnóstico", Color(0xFFF57F17), Color(0xFFFFFDE7))
        "ESPERANDO_REPUESTOS" -> Triple("Esperando Repuestos", Color(0xFFE65100), Color(0xFFFFF3E0))
        "EN_REPARACION" -> Triple("En Reparación", Color(0xFF1565C0), Color(0xFFE3F2FD))
        "SOLUCIONADO" -> Triple("Solucionado", Color(0xFF2E7D32), Color(0xFFE8F5E9))
        else -> Triple(status, Color.DarkGray, Color(0xFFEEEEEE))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
