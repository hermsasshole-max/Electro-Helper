package com.electroassistant.sa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * PinoutViewerScreen.kt
 * Jetpack Compose UI rendering interactive pinout tables and hardware schematics
 * for HVAC-R refrigeration controllers, 5-2-1 hard start kits, and inverter BMS cables.
 */

data class TerminalItem(
    val pin: String,
    val label: String,
    val function: String,
    val voltageRating: String = "230V AC",
    val wireColorHint: String = "Brown"
)

data class HardwareDevice(
    val id: String,
    val category: String, // "HVAC", "Relays & Starters", "Comms & Power"
    val title: String,
    val subtitle: String,
    val manufacturer: String,
    val terminals: List<TerminalItem>,
    val fieldNotes: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinoutViewerScreen(
    onNavigateBack: () -> Unit = {}
) {
    val sampleDevices = remember {
        listOf(
            HardwareDevice(
                id = "eliwell_974",
                category = "HVAC",
                title = "Eliwell IDPlus 974",
                subtitle = "Dual Probe (Pb1/Pb2), 3-Relay (Comp, Defrost, Fan)",
                manufacturer = "Eliwell / Invensys",
                terminals = listOf(
                    TerminalItem("1", "Relay Common", "Live 230V feed for relays", "230V AC", "Brown"),
                    TerminalItem("2", "OUT 1 (Compressor)", "Compressor contactor coil (8A / 2HP)", "230V AC", "Black"),
                    TerminalItem("3", "OUT 2 (Defrost)", "Defrost electric heater contactor (8A)", "230V AC", "Orange"),
                    TerminalItem("4", "OUT 3 (Evap Fan)", "Evaporator fan motor relay (5A)", "230V AC", "Yellow"),
                    TerminalItem("6", "Power Supply (L)", "Instrument power Live input", "230V AC", "Brown"),
                    TerminalItem("7", "Power Supply (N)", "Instrument power Neutral return", "0V Neutral", "Blue"),
                    TerminalItem("8", "Probe Common", "Analogue ground for NTC probes", "SELV", "White"),
                    TerminalItem("9", "Pb1 (Room Air)", "Cold room air temperature NTC 10k", "SELV", "Grey"),
                    TerminalItem("10", "Pb2 (Evaporator)", "Defrost termination NTC sensor", "SELV", "Grey")
                ),
                fieldNotes = listOf(
                    "Bridge terminal 1 to 6 if all relays are powered from the same control phase breaker.",
                    "SANS 10142-1 Clause 6.1.4: Never run probe signal cables in the same conduit as 230V motor lines.",
                    "Probe resistance test: 10 kΩ at 25°C, ~32.6 kΩ at 0°C."
                )
            ),
            HardwareDevice(
                id = "hard_start_521",
                category = "Relays & Starters",
                title: "5-2-1 Compressor Hard Start Kit",
                subtitle: "Potential Relay & Start Capacitor for Single-Phase Hermetic Compressors",
                manufacturer: "Supco / Mars / Universal",
                terminals = listOf(
                    TerminalItem("Pin 1", "Start Contact Out", "Connects to Start Capacitor series lead", "370V AC", "Black"),
                    TerminalItem("Pin 2", "Coil & Contact High", "Connects to Compressor START (S) terminal & Run Cap HERM", "Back-EMF (300V+)", "Yellow"),
                    TerminalItem("Pin 5", "Coil Return Low", "Connects to Compressor COMMON (C) or Main Line Live L1", "Line Voltage", "Red"),
                    TerminalItem("Start Cap", "Start Capacitor Lead", "Connects to Compressor RUN (R) / Neutral L2", "330V AC Rated", "Black")
                ),
                fieldNotes = listOf(
                    "Potential relay coil between Pin 2 and Pin 5 measures generated Back-EMF.",
                    "At 80% motor speed, Back-EMF climbs above 240V-300V, exciting coil 2-5 and opening normally closed contact 1-2.",
                    "Never use 2-wire PTC start kits on high-compression TXV systems; always use genuine 3-wire 5-2-1 kits."
                )
            ),
            HardwareDevice(
                id = "inverter_bms",
                category = "Comms & Power",
                title: "Solar Inverter BMS CAN & RS485",
                subtitle: "Sunsynk, Deye, Victron & Lithium Battery (Hubble, Pylontech) Pinouts",
                manufacturer: "Sunsynk / Victron / Deye",
                terminals = listOf(
                    TerminalItem("Pin 4", "CAN-High (CAN-H)", "Sunsynk/Deye CAN Bus High signal", "SELV Data", "Blue"),
                    TerminalItem("Pin 5", "CAN-Low (CAN-L)", "Sunsynk/Deye CAN Bus Low signal", "SELV Data", "White/Blue"),
                    TerminalItem("Pin 7", "RS485-A / Victron CAN-H", "Modbus RS485-A or Victron VE.Can High", "SELV Data", "White/Brown"),
                    TerminalItem("Pin 8", "RS485-B / Victron CAN-L", "Modbus RS485-B or Victron VE.Can Low", "SELV Data", "Brown")
                ),
                fieldNotes = listOf(
                    "Pylontech to Sunsynk: Pin 4 on Sunsynk links to Pin 2 on Pylontech; Pin 5 links to Pin 1.",
                    "Victron VE.Can RJ45 ports require 120 Ω terminator resistors on end devices.",
                    "SANS 10142-2 Section 5: Battery comms loss must trigger safe inverter shutdown."
                )
            )
        )
    }

    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDevice by remember { mutableStateOf(sampleDevices.first()) }

    val categories = listOf("All", "HVAC", "Relays & Starters", "Comms & Power")
    val filteredDevices = sampleDevices.filter { device ->
        (selectedCategory == "All" || device.category == selectedCategory) &&
                (device.title.contains(searchQuery, ignoreCase = true) ||
                        device.subtitle.contains(searchQuery, ignoreCase = true) ||
                        device.manufacturer.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hardware Pinouts & Schematics", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0B0F17)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search Eliwell, 5-2-1, Dixell, Sunsynk...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF38BDF8),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1E293B),
                            selectedLabelColor = Color(0xFF38BDF8),
                            containerColor = Color(0xFF0F172A),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Split Layout / Selected Device Viewer
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Device Header Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = selectedDevice.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = selectedDevice.subtitle,
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Manufacturer: ${selectedDevice.manufacturer}",
                                fontSize = 12.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }

                // Interactive Terminal Block Table
                item {
                    Text(
                        text = "Terminal Connections & Pinouts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                items(selectedDevice.terminals) { term ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertizing
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, Color(0xFF475569), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = term.pin,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = term.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = term.function,
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = term.voltageRating,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFFBBF24),
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = term.wireColorHint,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                // SANS Field Notes
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF172554)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Installation Electrician Field Notes", fontWeight = FontWeight.Bold, color = Color(0xFF93C5FD), fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            selectedDevice.fieldNotes.forEach { note ->
                                Text("• $note", fontSize = 12.sp, color = Color(0xFFDBEAFE), modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
