package com.example

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notifications.NotificationHelper
import com.example.ui.screens.AddAttachmentDialog
import com.example.ui.screens.AddCorrectiveDialog
import com.example.ui.screens.AddEquipmentDialog
import com.example.ui.screens.AddMaintenanceScheduleDialog
import com.example.ui.screens.AddPhotoReportDialog
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.CorrectiveScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EquipmentListScreen
import com.example.ui.screens.NotificationCenterSheet
import com.example.ui.screens.PhotoReportsScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.theme.MaintPrimary
import com.example.ui.theme.MaintSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StatusFalla
import com.example.viewmodel.MaintenanceViewModel

enum class AppScreen {
    DASHBOARD,
    SCHEDULE,
    CORRECTIVE,
    CALENDAR,
    EQUIPMENT,
    MEDIA_REPORTS
}

class MainActivity : ComponentActivity() {

    private val viewModel: MaintenanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createNotificationChannel(this)

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MaintenanceViewModel) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

    // Dialog states
    var showAddScheduleDialog by remember { mutableStateOf(false) }
    var preselectedScheduleDate by remember { mutableStateOf<Long?>(null) }
    var showAddCorrectiveDialog by remember { mutableStateOf(false) }
    var showAddEquipmentDialog by remember { mutableStateOf(false) }
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var showAddAttachmentDialog by remember { mutableStateOf(false) }
    var showNotificationCenter by remember { mutableStateOf(false) }

    val equipmentList by viewModel.equipmentList.collectAsStateWithLifecycle()
    val unreadNotifsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()

    // Request notification permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* result handled */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // BackHandler for sub-screens: returns to DASHBOARD
    if (currentScreen != AppScreen.DASHBOARD) {
        BackHandler {
            currentScreen = AppScreen.DASHBOARD
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentScreen == AppScreen.DASHBOARD) {
                            Image(
                                painter = painterResource(id = R.drawable.akaupkeep_gear_logo_1791834837544),
                                contentDescription = "Logo AkaUpkeep",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Text(
                            text = when (currentScreen) {
                                AppScreen.DASHBOARD -> "AkaUpkeep"
                                AppScreen.SCHEDULE -> "Cronograma de Mantenimientos"
                                AppScreen.CORRECTIVE -> "Mantenimientos Correctivos"
                                AppScreen.CALENDAR -> "Calendario de Intervenciones"
                                AppScreen.EQUIPMENT -> "Catálogo de Equipos"
                                AppScreen.MEDIA_REPORTS -> "Reportes Fotográficos & Archivos"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaintPrimary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    // Quick button to Photos & Attachments
                    IconButton(
                        onClick = { currentScreen = AppScreen.MEDIA_REPORTS },
                        modifier = Modifier.testTag("action_media_reports")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Reportes Fotográficos y Archivos"
                        )
                    }

                    // Notification Bell with badge
                    IconButton(
                        onClick = { showNotificationCenter = true },
                        modifier = Modifier.testTag("action_notification_center")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifsCount > 0) {
                                    Badge(containerColor = MaintSecondary) {
                                        Text("$unreadNotifsCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.DASHBOARD,
                    onClick = { currentScreen = AppScreen.DASHBOARD },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Panel", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaintPrimary,
                        selectedTextColor = MaintPrimary,
                        indicatorColor = MaintPrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_item_dashboard")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.SCHEDULE,
                    onClick = { currentScreen = AppScreen.SCHEDULE },
                    icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    label = { Text("Cronograma", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaintPrimary,
                        selectedTextColor = MaintPrimary,
                        indicatorColor = MaintPrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_item_schedule")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.CORRECTIVE,
                    onClick = { currentScreen = AppScreen.CORRECTIVE },
                    icon = { Icon(Icons.Default.Construction, contentDescription = null) },
                    label = { Text("Correctivos", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StatusFalla,
                        selectedTextColor = StatusFalla,
                        indicatorColor = StatusFalla.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_item_correctives")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.CALENDAR,
                    onClick = { currentScreen = AppScreen.CALENDAR },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    label = { Text("Calendario", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaintPrimary,
                        selectedTextColor = MaintPrimary,
                        indicatorColor = MaintPrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_item_calendar")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.EQUIPMENT,
                    onClick = { currentScreen = AppScreen.EQUIPMENT },
                    icon = { Icon(Icons.Default.PrecisionManufacturing, contentDescription = null) },
                    label = { Text("Equipos", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaintPrimary,
                        selectedTextColor = MaintPrimary,
                        indicatorColor = MaintPrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_item_equipment")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = { currentScreen = AppScreen.SCHEDULE },
                    onNavigateToCorrectives = { currentScreen = AppScreen.CORRECTIVE },
                    onNavigateToCalendar = { currentScreen = AppScreen.CALENDAR },
                    onNavigateToEquipment = { currentScreen = AppScreen.EQUIPMENT },
                    onOpenAddScheduleDialog = {
                        preselectedScheduleDate = null
                        showAddScheduleDialog = true
                    },
                    onOpenAddCorrectiveDialog = { showAddCorrectiveDialog = true },
                    onOpenNotificationCenter = { showNotificationCenter = true }
                )

                AppScreen.SCHEDULE -> ScheduleScreen(
                    viewModel = viewModel,
                    onOpenAddSchedule = {
                        preselectedScheduleDate = null
                        showAddScheduleDialog = true
                    }
                )

                AppScreen.CORRECTIVE -> CorrectiveScreen(
                    viewModel = viewModel,
                    onOpenAddCorrective = { showAddCorrectiveDialog = true }
                )

                AppScreen.CALENDAR -> CalendarScreen(
                    viewModel = viewModel,
                    onOpenAddScheduleForDate = { dateEpoch ->
                        preselectedScheduleDate = dateEpoch
                        showAddScheduleDialog = true
                    }
                )

                AppScreen.EQUIPMENT -> EquipmentListScreen(
                    viewModel = viewModel,
                    onOpenAddEquipment = { showAddEquipmentDialog = true }
                )

                AppScreen.MEDIA_REPORTS -> PhotoReportsScreen(
                    viewModel = viewModel,
                    onOpenAddPhotoReport = { showAddPhotoDialog = true },
                    onOpenAddAttachment = { showAddAttachmentDialog = true }
                )
            }
        }
    }

    // Modal Sheet: Notification Center
    if (showNotificationCenter) {
        NotificationCenterSheet(
            viewModel = viewModel,
            onDismiss = { showNotificationCenter = false }
        )
    }

    // Dialog: Add Maintenance Schedule
    if (showAddScheduleDialog) {
        AddMaintenanceScheduleDialog(
            equipmentList = equipmentList,
            preselectedDateEpochDay = preselectedScheduleDate,
            onDismiss = { showAddScheduleDialog = false },
            onConfirm = { newSchedule ->
                viewModel.addSchedule(newSchedule)
                showAddScheduleDialog = false
            }
        )
    }

    // Dialog: Add Corrective Maintenance
    if (showAddCorrectiveDialog) {
        AddCorrectiveDialog(
            equipmentList = equipmentList,
            onDismiss = { showAddCorrectiveDialog = false },
            onConfirm = { newCorrective ->
                viewModel.addCorrective(newCorrective)
                showAddCorrectiveDialog = false
            }
        )
    }

    // Dialog: Add Equipment
    if (showAddEquipmentDialog) {
        AddEquipmentDialog(
            onDismiss = { showAddEquipmentDialog = false },
            onConfirm = { newEq ->
                viewModel.addEquipment(newEq)
                showAddEquipmentDialog = false
            }
        )
    }

    // Dialog: Add Photo Report
    if (showAddPhotoDialog) {
        AddPhotoReportDialog(
            equipmentList = equipmentList,
            onDismiss = { showAddPhotoDialog = false },
            onConfirm = { newPhoto ->
                viewModel.addPhotoReport(newPhoto)
                showAddPhotoDialog = false
            }
        )
    }

    // Dialog: Add Attachment
    if (showAddAttachmentDialog) {
        AddAttachmentDialog(
            equipmentList = equipmentList,
            onDismiss = { showAddAttachmentDialog = false },
            onConfirm = { newAtt ->
                viewModel.addAttachment(newAtt)
                showAddAttachmentDialog = false
            }
        )
    }
}
