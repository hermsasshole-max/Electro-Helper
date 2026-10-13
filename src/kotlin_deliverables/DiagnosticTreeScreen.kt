package com.electroassistant.sa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * DiagnosticTreeScreen.kt
 * Jetpack Compose UI for on-site interactive diagnostic troubleshooting trees
 * (Nuisance Earth Leakage Tripping, Compressor Motor Diagnostics, Inverter Neutral Float).
 */

data class DecisionOption(
    val label: String,
    val summary: String,
    val nextStepId: String? = null,
    val isResolution: Boolean = false,
    val isSuccess: Boolean = false,
    val remedyList: List<String> = emptyList(),
    val sansStandardReference: String = ""
)

data class StepNode(
    val id: String,
    val stepIndex: Int,
    val title: String,
    val procedure: String,
    val meterMode: String,
    val safetyNote: String? = null,
    val expectedReading: String,
    val options: List<DecisionOption>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticTreeScreen() {
    // Current step in the active troubleshooting tree
    var currentStepId by remember { mutableStateOf("step_shedding") }
    var stepHistory by remember { mutableStateOf(listOf("step_shedding")) }
    var selectedTreeTitle by remember { mutableStateOf("Nuisance Earth Leakage Tripping (30mA ELCB)") }

    // Diagnostic Tree nodes definition
    val diagnosticNodes = remember {
        mapOf(
            "step_shedding" to StepNode(
                id = "step_shedding",
                stepIndex = 1,
                title = "Total Branch Load Shedding",
                procedure = "Turn OFF all downstream branch breakers fed by the 30mA Earth Leakage unit. Leave the Main Isolator ON. Attempt to lift the ELCB toggle lever.",
                meterMode = "Visual Inspection",
                safetyNote = "Keep hands behind IP2X deadfront panel. Live 230V busbars remain energized.",
                expectedReading = "ELCB toggle stays engaged in the UP position without immediately tripping.",
                options = listOf(
                    DecisionOption(
                        label = "ELCB trips instantly even with all sub-breakers OFF",
                        summary = "Direct Neutral-to-Earth fault inside the DB or on the common neutral bar.",
                        nextStepId = "step_ne_megger"
                    ),
                    DecisionOption(
                        label = "ELCB stays UP successfully with all sub-breakers OFF",
                        summary = "The fault is localized to an individual branch circuit.",
                        nextStepId = "step_sequential_breakers"
                    )
                )
            ),
            "step_ne_megger" to StepNode(
                id = "step_ne_megger",
                stepIndex = 2,
                title = "Neutral-to-Earth Insulation Resistance Test",
                procedure = "Switch OFF the Main Isolator. Disconnect incoming utility neutral from the neutral bar to isolate from grid ground. Set Insulation Tester to 500V DC. Connect probes between Neutral bar and Earth bar.",
                meterMode = "Insulation Megger @ 500V DC (MΩ scale)",
                safetyNote = "Verify 0.0V AC between Phase and Neutral before injecting 500V DC test voltages.",
                expectedReading = "Mandatory minimum 1.0 MΩ (SANS 10142-1 Clause 8.6.3). Normal healthy reading is >50 MΩ.",
                options = listOf(
                    DecisionOption(
                        label = "Reading is < 1.0 MΩ (e.g. 0.02 MΩ or 0.00 Ω dead short)",
                        summary = "Severe Neutral-Earth breakdown or moisture ingress inside trunking/wiring.",
                        isResolution = true,
                        isSuccess = false,
                        remedyList = listOf(
                            "Unscrew branch neutral conductors from the neutral bar one-by-one while watching the Megger display.",
                            "When the reading jumps back to >20 MΩ, the lifted conductor is the faulty neutral circuit.",
                            "Trace conductor to junction boxes. Inspect for pinched wires, damp light fittings, or neutral screws driven through conduit.",
                            "Repair damaged cable and re-verify insulation resistance Rins >= 1.0 MΩ."
                        ),
                        sansStandardReference = "SANS 10142-1 Clause 8.6.3 (Insulation Resistance)"
                    ),
                    DecisionOption(
                        label = "Reading is > 1.0 MΩ (Insulation is healthy)",
                        summary = "ELCB internal mechanical latch or core balance sensor failure.",
                        isResolution = true,
                        isSuccess = false,
                        remedyList = listOf(
                            "Perform calibrated RCD Ramp Test (15mA - 30mA) using calibrated multifunction tester.",
                            "If trip current is below 15mA or trip time exceeds 30ms, replace the 30mA Earth Leakage unit.",
                            "Verify SANS 767 / IEC 61008-1 compliant replacement."
                        ),
                        sansStandardReference = "SANS 10142-1 Clause 8.6.4 (Earth leakage testing)"
                    )
                )
            ),
            "step_sequential_breakers" to StepNode(
                id = "step_sequential_breakers",
                stepIndex = 2,
                title = "Sequential Branch Circuit Re-Engagement",
                procedure = "Keep ELCB engaged. Turn ON sub-breakers one-by-one (Lights, Plugs, Geyser, Stove, Aircon) with a 3-second delay between circuits.",
                meterMode = "Visual & Auditory Feedback",
                expectedReading = "All breakers close normally until the faulty circuit is reached.",
                options = listOf(
                    DecisionOption(
                        label = "ELCB trips instantly on Geyser or Stove breaker",
                        summary = "Immersion heating element dielectric failure caused by limescale and thermal stress.",
                        isResolution = true,
                        isSuccess = false,
                        remedyList = listOf(
                            "Isolate geyser rotary switch and drain tank.",
                            "Replace water heating element (typically 3kW / 4kW spiral Incoloy element).",
                            "Measure element insulation resistance at 500V DC (must be > 1.0 MΩ when cold).",
                            "Verify equipotential earth bonding on water inlet/outlet copper pipes (Rpe <= 0.2 Ω)."
                        ),
                        sansStandardReference = "SANS 10142-1 Clause 6.15 (Water heaters) & Clause 8.6.2"
                    ),
                    DecisionOption(
                        label = "ELCB holds all breakers, but trips randomly during operation",
                        summary = "Cumulative standing leakage from multiple switch-mode power supplies (EMI filters).",
                        isResolution = true,
                        isSuccess = true,
                        remedyList = listOf(
                            "Clamp both Phase and Neutral together with high-resolution mA clamp meter to measure standing leakage.",
                            "If standing leakage exceeds 10mA, split the distribution board into two separate 30mA RCD sections.",
                            "Install dedicated RCBOs for sensitive computing or refrigeration equipment."
                        ),
                        sansStandardReference = "SANS 10142-1 Clause 6.7.5 & Annexure E"
                    )
                )
            )
        )
    }

    val activeNode = diagnosticNodes[currentStepId] ?: diagnosticNodes.values.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Field Diagnostic Decision Trees", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0B0F17)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Diagnostic Topic Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Active Field Issue Resolver", fontSize = 12.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold)
                        Text(selectedTreeTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Step ${activeNode.stepIndex} of 3 in systematic isolation sequence",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // Step Instruction & Multimeter Settings
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = activeNode.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = activeNode.procedure,
                            fontSize = 14.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Meter Mode Box
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Meter Mode:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(activeNode.meterMode, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF38BDF8))
                            }
                        }

                        // Safety Alert
                        activeNode.safetyNote?.let { note ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF451A03),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(note, fontSize = 11.sp, color = Color(0xFFFED7AA))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Expected Standard Reading:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
                        Text(activeNode.expectedReading, fontSize = 13.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Interactive Decision Options
            item {
                Text("Select Observed Field Result:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            items(activeNode.options.size) { index ->
                val option = activeNode.options[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(option.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(option.summary, fontSize = 12.sp, color = Color(0xFF94A3B8))

                        if (option.isResolution) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (option.isSuccess) Color(0xFF064E3B) else Color(0xFF7F1D1D),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            if (option.isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (option.isSuccess) Color(0xFF34D399) else Color(0xFFF87171),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            if (option.isSuccess) "Recommended SANS Action" else "Defect Rectification Steps",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    option.remedyList.forEach { step ->
                                        Text("• $step", fontSize = 12.sp, color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Ref: ${option.sansStandardReference}", fontSize = 11.sp, color = Color(0xFF93C5FD), fontFamily = FontFamily.Monospace)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    option.nextStepId?.let { nextId ->
                                        currentStepId = nextId
                                        stepHistory = stepHistory + nextId
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Proceed to Next Diagnostic Step", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Reset Flow Button
            if (stepHistory.size > 1) {
                item {
                    OutlinedButton(
                        onClick = {
                            currentStepId = "step_shedding"
                            stepHistory = listOf("step_shedding")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
                    ) {
                        Text("Restart Decision Tree from Step 1", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
