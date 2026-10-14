package com.electroassistant.sa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * CoCVerificationViewModel.kt
 * State management and SANS 10142-1 Clause 8.6 Verification & Audit Engine
 */

data class TestReadingItem(
    val id: String = UUID.randomUUID().toString(),
    val circuitName: String = "",
    val breakerRatingA: Int = 20,
    val earthContinuityOhms: Double = 0.08,
    val insulationPhaseNeutralMOhm: Double = 50.0,
    val insulationPhaseEarthMOhm: Double = 50.0,
    val insulationNeutralEarthMOhm: Double = 50.0,
    val earthLeakageTripCurrentMa: Double = 22.0,
    val earthLeakageTripTimeMs: Double = 18.0,
    val loopImpedanceZsOhms: Double = 0.35,
    val psccKa: Double = 4.5,
    val touchVoltageVolts: Double = 12.0,
    val breakerBreakingCapacityKa: Double = 6.0
) {
    val isContinuityPass: Boolean get() = earthContinuityOhms <= 0.20
    val isInsulationPnPass: Boolean get() = insulationPhaseNeutralMOhm >= 1.00
    val isInsulationPePass: Boolean get() = insulationPhaseEarthMOhm >= 1.00
    val isInsulationNePass: Boolean get() = insulationNeutralEarthMOhm >= 1.00
    val isEarthLeakagePass: Boolean get() = earthLeakageTripTimeMs <= 30.0 && earthLeakageTripCurrentMa in 15.0..30.0
    val isPsccSafe: Boolean get() = psccKa <= breakerBreakingCapacityKa
    val isCircuitOverallPass: Boolean get() = isContinuityPass && isInsulationPnPass &&
            isInsulationPePass && isInsulationNePass && isEarthLeakagePass && isPsccSafe
}

data class VisualChecklist(
    val mainIsolatorAccessible: Boolean = true,
    val neutralEarthSeparatedCorrectly: Boolean = true,
    val dbLegendedAndIdentified: Boolean = true,
    val ip2xFingerSafeBarriers: Boolean = true,
    val surgeProtectionSpdFunctional: Boolean = true,
    val earthElectrodeBonded: Boolean = true,
    val geyserAndPipesBonded: Boolean = true,
    val cablesSecuredAndGlanded: Boolean = true
) {
    val isAllVisualPassed: Boolean get() = mainIsolatorAccessible && neutralEarthSeparatedCorrectly &&
            dbLegendedAndIdentified && ip2xFingerSafeBarriers && surgeProtectionSpdFunctional &&
            earthElectrodeBonded && geyserAndPipesBonded && cablesSecuredAndGlanded
}

data class CoCAuditUiState(
    val certificateNumber: String = "EC-2026-SA001",
    val contractorName: String = "ElectroServices SA (Pty) Ltd",
    val accreditationNumber: String = "ECA-GP-98421",
    val clientName: String = "Johannesburg Cold Storage Ltd",
    val installationAddress: String = "44 Heidelberg Rd, City Deep, JHB",
    val supplyType: String = "Three-Phase 400V 50Hz",
    val earthingSystem: String = "TN-S",
    val mainBreakerRatingA: Int = 100,
    val testInstrumentModel: String = "Megger MFT1741+ Multifunction Tester",
    val testInstrumentSerial: String = "SN-20491823",
    val calibrationDate: String = "2026-02-15",
    val visualChecks: VisualChecklist = VisualChecklist(),
    val readings: List<TestReadingItem> = listOf(
        TestReadingItem(
            circuitName = "DB-Main Feeder",
            breakerRatingA = 100,
            earthContinuityOhms = 0.04,
            insulationPhaseNeutralMOhm = 120.0,
            insulationPhaseEarthMOhm = 95.0,
            insulationNeutralEarthMOhm = 90.0,
            earthLeakageTripCurrentMa = 22.0,
            earthLeakageTripTimeMs = 18.0,
            loopImpedanceZsOhms = 0.22,
            psccKa = 4.8,
            touchVoltageVolts = 8.4,
            breakerBreakingCapacityKa = 10.0
        ),
        TestReadingItem(
            circuitName = "Cold Room Compressor 1 (5.5kW)",
            breakerRatingA = 32,
            earthContinuityOhms = 0.11,
            insulationPhaseNeutralMOhm = 45.0,
            insulationPhaseEarthMOhm = 38.0,
            insulationNeutralEarthMOhm = 42.0,
            earthLeakageTripCurrentMa = 24.0,
            earthLeakageTripTimeMs = 21.0,
            loopImpedanceZsOhms = 0.38,
            psccKa = 3.2,
            touchVoltageVolts = 14.2,
            breakerBreakingCapacityKa = 6.0
        )
    ),
    val isReportGenerated: Boolean = false,
    val reportSummaryText: String = ""
) {
    val totalCircuits: Int get() = readings.size
    val passedCircuits: Int get() = readings.count { it.isCircuitOverallPass }
    val failedCircuits: Int get() = readings.count { !it.isCircuitOverallPass }
    val isCoCCompliant: Boolean get() = visualChecks.isAllVisualPassed && failedCircuits == 0
}

class CoCVerificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CoCAuditUiState())
    val uiState: StateFlow<CoCAuditUiState> = _uiState.asStateFlow()

    fun updateClientDetails(
        clientName: String,
        address: String,
        certNumber: String,
        contractor: String
    ) {
        _uiState.update { current ->
            current.copy(
                clientName = clientName,
                installationAddress = address,
                certificateNumber = certNumber,
                contractorName = contractor
            )
        }
    }

    fun updateVisualCheck(field: (VisualChecklist) -> VisualChecklist) {
        _uiState.update { current ->
            current.copy(visualChecks = field(current.visualChecks))
        }
    }

    fun addCircuitReading(reading: TestReadingItem) {
        _uiState.update { current ->
            current.copy(readings = current.readings + reading)
        }
    }

    fun updateCircuitReading(id: String, updated: TestReadingItem) {
        _uiState.update { current ->
            current.copy(
                readings = current.readings.map { if (it.id == id) updated else it }
            )
        }
    }

    fun removeCircuitReading(id: String) {
        _uiState.update { current ->
            current.copy(readings = current.readings.filterNot { it.id == id })
        }
    }

    fun calculatePsccAndTouchVoltage(zsOhms: Double, zeOhms: Double = 0.15, vNominal: Double = 230.0): Pair<Double, Double> {
        val zs = zsOhms.coerceAtLeast(0.01)
        val faultCurrentAmps = vNominal / zs
        val psccKa = faultCurrentAmps / 1000.0
        val touchVoltage = faultCurrentAmps * zeOhms
        return Pair(psccKa, touchVoltage)
    }

    fun generateJobCardReport(): String {
        val state = _uiState.value
        val sb = StringBuilder()
        sb.appendLine("======================================================================")
        sb.appendLine("   SOUTH AFRICAN SANS 10142-1 CERTIFICATE OF COMPLIANCE (CoC) REPORT")
        sb.appendLine("======================================================================")
        sb.appendLine("CoC Number: ${state.certificateNumber}")
        sb.appendLine("Contractor: ${state.contractorName} (Accreditation: ${state.accreditationNumber})")
        sb.appendLine("Client:     ${state.clientName}")
        sb.appendLine("Premises:   ${state.installationAddress}")
        sb.appendLine("Supply:     ${state.supplyType} | Earthing: ${state.earthingSystem}")
        sb.appendLine("----------------------------------------------------------------------")
        sb.appendLine("FINAL SANS 10142 COMPLIANCE VERDICT: " +
                if (state.isCoCCompliant) ">>> SANS 10142-1 COMPLIANT - APPROVED FOR CERTIFICATION <<<"
                else ">>> NON-COMPLIANT - SANS DEVIATIONS DETECTED (RECTIFICATION REQUIRED) <<<"
        )
        sb.appendLine("======================================================================")

        val reportStr = sb.toString()
        _uiState.update { it.copy(isReportGenerated = true, reportSummaryText = reportStr) }
        return reportStr
    }
}
