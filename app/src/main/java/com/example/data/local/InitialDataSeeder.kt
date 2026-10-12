package com.example.data.local

import com.example.ui.components.DateUtils

object InitialDataSeeder {

    suspend fun seedIfNeeded(dao: MaintenanceDao) {
        // Only seed if empty
        val existing = dao.getEquipmentById(1L)
        if (existing != null) return

        val today = DateUtils.todayEpochDay()

        val equipmentList = listOf(
            EquipmentEntity(
                id = 1L,
                tag = "BOM-01",
                name = "Bomba Centrífuga Multietapa",
                equipmentNumber = "Bomba Nº 01",
                model = "KSB Multitec 50/3",
                category = "Hidráulica",
                location = "Planta 1 - Sala Bombas",
                status = "OPERATIVO",
                criticality = "ALTA",
                operatingHours = 4320,
                defaultToolsRequired = "Extractor de rodamientos, Torquímetro 1/2 pulg, Alineador láser Easy-Laser, EPP dieléctrico",
                notes = "Unidad 1 para suministro de agua al circuito de refrigeración.",
                photoUri = null
            ),
            EquipmentEntity(
                id = 2L,
                tag = "BOM-02",
                name = "Bomba Centrífuga Multietapa",
                equipmentNumber = "Bomba Nº 02 (Standby)",
                model = "KSB Multitec 50/3",
                category = "Hidráulica",
                location = "Planta 1 - Sala Bombas",
                status = "OPERATIVO",
                criticality = "MEDIA",
                operatingHours = 1890,
                defaultToolsRequired = "Extractor de rodamientos, Torquímetro 1/2 pulg, Alineador láser",
                notes = "Mismo modelo que BOM-01, opera como unidad de respaldo redundante.",
                photoUri = null
            ),
            EquipmentEntity(
                id = 3L,
                tag = "COMP-101",
                name = "Compresor Rotativo de Tornillo",
                equipmentNumber = "Compresor Nº 1",
                model = "Atlas Copco GA37",
                category = "Neumática",
                location = "Nave Principal - Cuarto de Aire",
                status = "OPERATIVO",
                criticality = "ALTA",
                operatingHours = 6200,
                defaultToolsRequired = "Juego de llaves Allen, Manómetro diferencial, Bomba de vacío manual",
                notes = "Alimenta la línea neumática 1 de 7.5 bar.",
                photoUri = null
            ),
            EquipmentEntity(
                id = 4L,
                tag = "COMP-102",
                name = "Compresor Rotativo de Tornillo",
                equipmentNumber = "Compresor Nº 2",
                model = "Atlas Copco GA37",
                category = "Neumática",
                location = "Nave Principal - Cuarto de Aire",
                status = "EN_MANTENIMIENTO",
                criticality = "ALTA",
                operatingHours = 8750,
                defaultToolsRequired = "Juego de llaves Allen métricas, Manómetro diferencial, Bomba de vacío manual",
                notes = "Mismo modelo que COMP-101. En servicio por cambio de filtros y separador.",
                photoUri = null
            ),
            EquipmentEntity(
                id = 5L,
                tag = "TOR-01",
                name = "Torno CNC de Bancada Inclinada",
                equipmentNumber = "Torno Nº 1",
                model = "Haas ST-30",
                category = "Mecanizado",
                location = "Taller Central - Nave 2",
                status = "OPERATIVO",
                criticality = "ALTA",
                operatingHours = 9540,
                defaultToolsRequired = "Indicador de carátula mitutoyo 0.001mm, Calibrador pie de rey",
                notes = "Maquinado de ejes de alta precisión.",
                photoUri = null
            ),
            EquipmentEntity(
                id = 6L,
                tag = "TOR-02",
                name = "Torno CNC de Bancada Inclinada",
                equipmentNumber = "Torno Nº 2",
                model = "Haas ST-30",
                category = "Mecanizado",
                location = "Taller Central - Nave 2",
                status = "FUERA_DE_SERVICIO",
                criticality = "ALTA",
                operatingHours = 12400,
                defaultToolsRequired = "Indicador de carátula mitutoyo 0.001mm, Calibrador pie de rey, Llaves de gancho para husillo",
                notes = "Mismo modelo Haas ST-30. En parada correctiva por daño en rodamiento de husillo.",
                photoUri = null
            ),
            EquipmentEntity(
                id = 7L,
                tag = "GEN-04",
                name = "Grupo Electrógeno Diésel",
                equipmentNumber = "Generador Nº 4",
                model = "Caterpillar C15 500kVA",
                category = "Generación Eléctrica",
                location = "Patio Eléctrico Subestación",
                status = "OPERATIVO",
                criticality = "ALTA",
                operatingHours = 640,
                defaultToolsRequired = "Multímetro Fluke 87V, Pinza amperimétrica, Densímetro de electrolito",
                notes = "Respaldo de emergencia para la planta.",
                photoUri = null
            ),
            EquipmentEntity(
                id = 8L,
                tag = "CAL-01",
                name = "Caldera Pirotubular de Vapor",
                equipmentNumber = "Caldera Nº 1",
                model = "Cleaver-Brooks CB-200",
                category = "Térmica",
                location = "Sala de Calderas Externa",
                status = "EN_ALERTA",
                criticality = "ALTA",
                operatingHours = 9100,
                defaultToolsRequired = "Analizador de gases Testo 350, Termómetro infrarrojo Flir, Llaves Stillson 24 pulg",
                notes = "Presenta ligera desviación térmica en chimenea secundaria.",
                photoUri = null
            )
        )
        dao.insertEquipmentList(equipmentList)

        // Seed Schedules (Include one in exactly 2 days for the 48h advance alert!)
        val schedules = listOf(
            MaintenanceScheduleEntity(
                id = 1L,
                equipmentId = 1L,
                equipmentTag = "BOM-01",
                equipmentName = "Bomba Centrífuga Multietapa",
                equipmentNumber = "Bomba Nº 01",
                model = "KSB Multitec 50/3",
                title = "Mantenimiento Preventivo Bimestral: Rodamientos y Alineación",
                type = "PREVENTIVO",
                scheduledEpochDay = today + 2, // EXACTAMENTE EN 2 DÍAS
                scheduledTime = "08:30",
                frequencyDays = 60,
                frequency = "Cada 60 días",
                requiredTools = "Extractor de rodamientos de 3 garras, Alineador láser Easy-Laser, Torquímetro, EPP nivel 2",
                assignedTechnician = "Ing. Carlos Mendoza",
                estimatedDurationHours = 4.0f,
                notes = "Reemplazar rodamientos SKF 6309 en la Bomba Nº 01, verificar juego axial < 0.05 mm, realizar alineación láser motor-bomba y engrasar con grasa Mobil Polyrex EM.",
                status = "PROGRAMADO"
            ),
            MaintenanceScheduleEntity(
                id = 2L,
                equipmentId = 4L,
                equipmentTag = "COMP-102",
                equipmentName = "Compresor Rotativo de Tornillo",
                equipmentNumber = "Compresor Nº 2",
                model = "Atlas Copco GA37",
                title = "Servicio Preventivo: Cambio de filtros y separador aire/aceite",
                type = "PREVENTIVO",
                scheduledEpochDay = today, // HOY
                scheduledTime = "14:00",
                frequencyDays = 30,
                frequency = "Cada 30 días",
                requiredTools = "Llave para filtros de cartucho, Recipiente recolector 40L, Manómetro patrón, Guantes de nitrilo",
                assignedTechnician = "Téc. Roberto Silva",
                estimatedDurationHours = 3.0f,
                notes = "Drenar condensados de tanques pulmón en Compresor Nº 2, sustituir filtro de admisión, cartucho coalescente separador y purgar línea piloto.",
                status = "EN_PROGRESO"
            ),
            MaintenanceScheduleEntity(
                id = 3L,
                equipmentId = 7L,
                equipmentTag = "GEN-04",
                equipmentName = "Grupo Electrógeno Diésel",
                equipmentNumber = "Generador Nº 4",
                model = "Caterpillar C15 500kVA",
                title = "Inspección y prueba en vacío de sistema de transferencia",
                type = "INSPECCION",
                scheduledEpochDay = today + 5,
                scheduledTime = "09:00",
                frequencyDays = 30,
                frequency = "Cada 30 días",
                requiredTools = "Multímetro Fluke 87V, Pinza amperimétrica, Termógrafo infrarrojo, Guantes dieléctricos 1000V",
                assignedTechnician = "Ing. Andrés Paredes",
                estimatedDurationHours = 2.5f,
                notes = "Arrancar motor en modo prueba por 15 minutos, monitorear presión de aceite 45 PSI, voltaje 440V 60Hz y temperatura de refrigerante.",
                status = "PROGRAMADO"
            ),
            MaintenanceScheduleEntity(
                id = 4L,
                equipmentId = 2L,
                equipmentTag = "BOM-02",
                equipmentName = "Bomba Centrífuga Multietapa",
                equipmentNumber = "Bomba Nº 02 (Standby)",
                model = "KSB Multitec 50/3",
                title = "Lubricación general de descansos y prueba de giro libre",
                type = "LUBRICACION",
                scheduledEpochDay = today - 1, // VENCIDO AYER
                scheduledTime = "11:00",
                frequencyDays = 7,
                frequency = "Cada 7 días",
                requiredTools = "Bomba de engrase manual, Grasa Mobilith SHC 220, Llaves combinadas 19mm y 24mm",
                assignedTechnician = "Téc. Marcos Díaz",
                estimatedDurationHours = 1.5f,
                notes = "Giro manual del eje en Bomba Nº 02 para evitar marcación de pistas por vibración en reposo.",
                status = "VENCIDO"
            ),
            MaintenanceScheduleEntity(
                id = 5L,
                equipmentId = 8L,
                equipmentTag = "CAL-01",
                equipmentName = "Caldera Pirotubular de Vapor",
                equipmentNumber = "Caldera Nº 1",
                model = "Cleaver-Brooks CB-200",
                title = "Calibración de instrumentos y análisis de combustión",
                type = "CALIBRACION",
                scheduledEpochDay = today + 9,
                scheduledTime = "07:30",
                frequencyDays = 90,
                frequency = "Cada 90 días",
                requiredTools = "Analizador de gases Testo 350, Manómetro diferencial digital, Bomba de calibración de presión",
                assignedTechnician = "Especialista Externo - CertiTech",
                estimatedDurationHours = 5.0f,
                notes = "Verificar exceso de aire entre 15% y 20%, nivel de CO < 50 ppm, y contrastar presostato de seguridad de corte por alta presión.",
                status = "PROGRAMADO"
            )
        )
        dao.insertScheduleList(schedules)

        // Seed Corrective Maintenances
        val correctives = listOf(
            CorrectiveMaintenanceEntity(
                id = 1L,
                equipmentId = 6L,
                equipmentTag = "TOR-02",
                equipmentName = "Torno CNC de Bancada Inclinada",
                equipmentNumber = "Torno Nº 2",
                model = "Haas ST-30",
                failureDescription = "Vibración severa y chirrido agudo en el cabezal del Torno Nº 2 durante el maquinado a más de 2000 RPM. Disparo por sobrecorriente en servomotor del husillo. Nota: El Torno Nº 1 opera normal.",
                requiredSpareParts = "1. Rodamientos de contacto angular SKF 7014 CD/P4A (Set de 2 unidades)\n2. Correa sincrónica de transmisión Gates Poly Chain GT Carbon 8M-1200\n3. Retén de aceite Viton de doble labio 75x100x10 mm",
                severity = "CRITICA",
                status = "ESPERANDO_REPUESTOS",
                reportedEpochDay = today - 1,
                scheduledInterventionEpochDay = today + 1,
                assignedTechnician = "Ing. Carlos Mendoza / Especialista Mecatrónico",
                requiredTools = "Extractor hidráulico de 10 Ton, Calentador por inducción para rodamientos, Indicador micrométrico Mitutoyo, Torquímetro de precisión",
                downtimeHours = 18.5f,
                notes = "Se desmontó la tapa del cabezal en Torno Nº 2. Se confirmó daño por fatiga en la pista interna del rodamiento frontal. Repuestos solicitados con orden PO-8924."
            ),
            CorrectiveMaintenanceEntity(
                id = 2L,
                equipmentId = 8L,
                equipmentTag = "CAL-01",
                equipmentName = "Caldera Pirotubular de Vapor",
                equipmentNumber = "Caldera Nº 1",
                model = "Cleaver-Brooks CB-200",
                failureDescription = "Fuga constante de vapor por el prensaestopas de la válvula check de alimentación de agua de la Caldera Nº 1. Descenso de presión en línea de vapor saturado.",
                requiredSpareParts = "1. Empaquetadura trenzada de grafito flexible con incrustación de hilo Inconel 1/2 pulg (2 metros)\n2. Junta espiralada metálica ASME B16.20 Clase 300 2 pulg",
                severity = "ALTA",
                status = "EN_DIAGNOSTICO",
                reportedEpochDay = today,
                scheduledInterventionEpochDay = today + 3,
                assignedTechnician = "Téc. Roberto Silva",
                requiredTools = "Extractor de empaquetaduras flexible, Llaves Stillson 18 y 24 pulg, Cepillo de cerdas de bronce, Grasa anti-aferrante de cobre Loctite",
                downtimeHours = 2.0f,
                notes = "Se colocó aviso de precaución en zona de válvula. Intervención programada durante la parada nocturna de vapor para despresurizar con seguridad."
            )
        )
        dao.insertCorrectiveList(correctives)

        // Seed Photo Reports
        val photoReports = listOf(
            PhotoReportEntity(
                id = 1L,
                equipmentId = 6L,
                equipmentTag = "TOR-02",
                equipmentName = "Torno CNC de Bancada Inclinada",
                equipmentNumber = "Torno Nº 2",
                correctiveId = 1L,
                title = "Evidencia de daño en rodamiento de husillo - Torno Nº 2",
                stage = "FALLA",
                description = "Inspección visual macroscópica en Torno Nº 2: Descascarillado severo en pista de rodadura por sobrecarga cíclica y sobrecalentamiento térmico.",
                imageUri = "sample_photo_bearing_damage",
                technician = "Ing. Carlos Mendoza"
            ),
            PhotoReportEntity(
                id = 2L,
                equipmentId = 1L,
                equipmentTag = "BOM-01",
                equipmentName = "Bomba Centrífuga Multietapa",
                equipmentNumber = "Bomba Nº 01",
                maintenanceScheduleId = 1L,
                title = "Inspección termográfica del acoplamiento elástico - Bomba Nº 01",
                stage = "ANTES",
                description = "Termografía infrarroja reporta temperatura normal de 42°C en descanso anterior y 45°C en descanso posterior en Bomba Nº 01.",
                imageUri = "sample_photo_pump_thermal",
                technician = "Téc. Roberto Silva"
            ),
            PhotoReportEntity(
                id = 3L,
                equipmentId = 4L,
                equipmentTag = "COMP-102",
                equipmentName = "Compresor Rotativo de Tornillo",
                equipmentNumber = "Compresor Nº 2",
                maintenanceScheduleId = 2L,
                title = "Sustitución de cartucho separador aire/aceite - Compresor Nº 2",
                stage = "DURANTE",
                description = "Desmontaje de brida superior del depósito separador en Compresor Nº 2. Limpieza de asiento de junta O-Ring y colocación de elemento nuevo.",
                imageUri = "sample_photo_compressor_filter",
                technician = "Téc. Roberto Silva"
            )
        )
        dao.insertPhotoReportList(photoReports)

        // Seed Attachments
        val attachments = listOf(
            AttachmentEntity(
                id = 1L,
                equipmentId = 1L,
                equipmentTag = "BOM-01",
                equipmentName = "Bomba Centrífuga Multietapa",
                equipmentNumber = "Bomba Nº 01",
                title = "Manual de Operación KSB Multitec 50-3.pdf",
                fileType = "MANUAL_PDF",
                fileSizeDescription = "4.8 MB",
                notes = "Aplica a Bomba Nº 01 y Bomba Nº 02 (mismo modelo). Incluye tolerancias de alineación radial."
            ),
            AttachmentEntity(
                id = 2L,
                equipmentId = 6L,
                equipmentTag = "TOR-02",
                equipmentName = "Torno CNC de Bancada Inclinada",
                equipmentNumber = "Torno Nº 2",
                title = "Esquema Mecánico y Precarga Husillo Haas ST-30.pdf",
                fileType = "FICHA_TECNICA",
                fileSizeDescription = "6.1 MB",
                notes = "Procedimiento de montaje y apriete para rodamientos SKF 7014 de ultra precisión."
            ),
            AttachmentEntity(
                id = 3L,
                equipmentId = 4L,
                equipmentTag = "COMP-102",
                equipmentName = "Compresor Rotativo de Tornillo",
                equipmentNumber = "Compresor Nº 2",
                title = "Ficha Técnica y Guía de Servicio Atlas Copco GA37.pdf",
                fileType = "CHECKLIST",
                fileSizeDescription = "1.2 MB",
                notes = "Aplica a Compresor Nº 1 y Compresor Nº 2. Checklist firmado de recepción de filtros."
            )
        )
        dao.insertAttachmentList(attachments)

        // Seed Initial App Notifications
        val notifications = listOf(
            AppNotificationEntity(
                id = 1L,
                maintenanceScheduleId = 1L,
                equipmentName = "Bomba Centrífuga (BOM-01)",
                equipmentNumber = "Bomba Nº 01",
                title = "⚠️ Alerta previa: Mantenimiento en 2 días",
                message = "El mantenimiento de la Bomba Nº 01 (BOM-01, Modelo KSB Multitec) está programado para el día ${DateUtils.formatEpochDay(today + 2)}. Verifique repuestos y alineador láser.",
                alertType = "PREVIA_2_DIAS",
                scheduledEpochDay = today + 2,
                isRead = false
            ),
            AppNotificationEntity(
                id = 2L,
                maintenanceScheduleId = 2L,
                equipmentName = "Compresor de Tornillo (COMP-102)",
                equipmentNumber = "Compresor Nº 2",
                title = "🔔 Mantenimiento programado para HOY",
                message = "Hoy a las 14:00 corresponde el servicio en el Compresor Nº 2 (Modelo Atlas Copco GA37) en Nave Principal.",
                alertType = "HOY",
                scheduledEpochDay = today,
                isRead = false
            ),
            AppNotificationEntity(
                id = 3L,
                correctiveId = 1L,
                equipmentName = "Torno CNC (TOR-02)",
                equipmentNumber = "Torno Nº 2",
                title = "🚨 Falla Correctiva Crítica: Torno Nº 2",
                message = "Torno Nº 2 (Modelo Haas ST-30) fuera de servicio por vibración extrema en husillo. Intervención en espera de rodamientos SKF.",
                alertType = "CORRECTIVO_URGENTE",
                scheduledEpochDay = today + 1,
                isRead = false
            )
        )
        dao.insertNotificationList(notifications)
    }
}
