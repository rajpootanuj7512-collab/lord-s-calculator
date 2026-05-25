package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import com.example.calculator.CalculationResult
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.network.GeminiClient
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()
    val activeOperation by viewModel.activeOperation.collectAsState()
    val calculationResult by viewModel.calculationResult.collectAsState()
    val aiOutput by viewModel.aiOutput.collectAsState()
    val isLoadingAi by viewModel.isLoadingAi.collectAsState()
    val isMistakeCheckerEnabled by viewModel.isMistakeCheckerEnabled.collectAsState()
    val activeFieldKey by viewModel.activeFieldKey.collectAsState()

    var subjectDropdownExpanded by remember { mutableStateOf(false) }
    var chapterDropdownExpanded by remember { mutableStateOf(false) }

    // Space-background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepBlack, CyberSlate, DeepBlack)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. STELLAR SYSTEM HEADER (Sticky navigation)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xE6060709))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(NeonCyan, HyperPink)),
                        shape = RoundedCornerShape(0.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OMNICALC AI",
                            style = TextStyle(
                                color = TextWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif,
                                shadow = Shadow(color = NeonCyan, offset = Offset(0f, 0f), blurRadius = 8f)
                            )
                        )
                    }
                    Text(
                        text = "QUANTUM STEM SOLVER ENGINE • v2026",
                        style = TextStyle(
                            color = GhostGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                // AI Neural Link indication indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x22161920))
                        .border(1.dp, Color(0x338A99AD), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val isConnected = GeminiClient.isApiKeyConfigured()
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isConnected) NeonGreen else CosmicGold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isConnected) "NEURAL LINK ACTIVE" else "LOCAL MODULES ONLY",
                        color = if (isConnected) NeonGreen else CosmicGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Main body scrollable column for safe fitting on smaller screen class limits
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 2. STICKY DROPDOWN ARCHITECTURE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Dropdown 1: Subject Selection
                    ExposedDropdownMenuBox(
                        expanded = subjectDropdownExpanded,
                        onExpandedChange = { subjectDropdownExpanded = !subjectDropdownExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedSubject,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Subject", color = NeonCyan) },
                            textStyle = TextStyle(color = TextWhite, fontWeight = FontWeight.Bold),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0x6600E6FF),
                                focusedLabelColor = NeonCyan
                            ),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                                .testTag("subject_trigger")
                        )
                        ExposedDropdownMenu(
                            expanded = subjectDropdownExpanded,
                            onDismissRequest = { subjectDropdownExpanded = false },
                            modifier = Modifier.background(CyberSlate)
                        ) {
                            viewModel.subjectMap.keys.forEach { subject ->
                                DropdownMenuItem(
                                    text = { Text(subject, color = TextWhite, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        viewModel.selectSubject(subject)
                                        subjectDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Dropdown 2: Chapter Selection (Changes dynamically)
                    ExposedDropdownMenuBox(
                        expanded = chapterDropdownExpanded,
                        onExpandedChange = { chapterDropdownExpanded = !chapterDropdownExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedChapter,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Chapter", color = HyperPink) },
                            textStyle = TextStyle(color = TextWhite, fontWeight = FontWeight.Bold),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = chapterDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                focusedBorderColor = HyperPink,
                                unfocusedBorderColor = Color(0x66FF007F),
                                focusedLabelColor = HyperPink
                            ),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                                .testTag("chapter_trigger")
                        )
                        ExposedDropdownMenu(
                            expanded = chapterDropdownExpanded,
                            onDismissRequest = { chapterDropdownExpanded = false },
                            modifier = Modifier.background(CyberSlate)
                        ) {
                            val listChapters = viewModel.subjectMap[selectedSubject] ?: emptyList()
                            listChapters.forEach { chapter ->
                                DropdownMenuItem(
                                    text = { Text(chapter, color = TextWhite, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        viewModel.selectChapter(chapter)
                                        chapterDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 3. SCI-FI OPERATIONS ROW
                Text(
                    text = "SELECT OPERATIONAL PROTOCOL:",
                    color = GhostGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val listOps = viewModel.operationMap[selectedChapter] ?: emptyList()
                    listOps.forEach { op ->
                        val isActive = op.uppercase() == activeOperation.uppercase()
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(32.dp))
                                .background(if (isActive) HyperPink else Color(0x1A00E6FF))
                                .border(
                                    width = 1.3.dp,
                                    color = if (isActive) Color.White else NeonCyan.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(32.dp)
                                )
                                .clickable { viewModel.selectOperation(op) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = op.replace("_", " ").uppercase(),
                                color = if (isActive) TextWhite else NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("operation_${op.lowercase()}")
                            )
                        }
                    }
                }

                // 4. RESPONSIBLE ADAPTIVE CONTENT LAYOUT (Double Pane for wide screen widths, Single column for compact mobile screens)
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val isTablet = maxWidth >= 600.dp

                    if (isTablet) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Left Column (Visual canvas + Inputs + Keyboard)
                            Column(
                                modifier = Modifier.weight(1.1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                VisualCanvasCard(selectedChapter, activeOperation, calculationResult)
                                FormulaInputCard(selectedChapter, viewModel, activeFieldKey)
                                ScientificHelperKeyboard(viewModel)
                            }

                            // Right Column (Step list answers + AI tutoring)
                            Column(
                                modifier = Modifier.weight(0.9f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                SolutionAnswerCard(calculationResult, context, clipboardManager)
                                QuantumTutorConsultCard(viewModel, isMistakeCheckerEnabled, isLoadingAi, aiOutput)
                            }
                        }
                    } else {
                        // Standard Vertical Stack for Mobile
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            VisualCanvasCard(selectedChapter, activeOperation, calculationResult)
                            FormulaInputCard(selectedChapter, viewModel, activeFieldKey)
                            ScientificHelperKeyboard(viewModel)
                            SolutionAnswerCard(calculationResult, context, clipboardManager)
                            QuantumTutorConsultCard(viewModel, isMistakeCheckerEnabled, isLoadingAi, aiOutput)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// CARD COMPONENT: VISUAL PLOTTER CANVAS
// ----------------------------------------------------
@Composable
fun VisualCanvasCard(
    chapter: String,
    operation: String,
    calcResult: CalculationResult?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
            .background(DarkGlass)
    ) {
        val params = calcResult?.graphParams ?: emptyMap()
        InteractiveCanvas(
            chapter = chapter,
            operation = operation,
            params = params,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay layout description label
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0x88060709))
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timeline,
                contentDescription = "Visual Graph",
                tint = NeonCyan,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "REAL-TIME MATRIX GRAPHICS",
                color = TextWhite,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// ----------------------------------------------------
// CARD COMPONENT: DYNAMIC FORMULA INPUT CONTROL
// ----------------------------------------------------
@Composable
fun FormulaInputCard(
    chapter: String,
    viewModel: CalculatorViewModel,
    activeFieldKey: String?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkGlass),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Inputs Icon",
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "QUANTUM INPUT CONSTANTS",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Render different input layouts depending on active selected chapter
            when (chapter.uppercase()) {
                "MATRIX" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Matrix A (2x2 Matrix Editor Coeffs)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MatrixCellField("a11", viewModel, activeFieldKey, Modifier.weight(1f))
                            MatrixCellField("a12", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MatrixCellField("a21", viewModel, activeFieldKey, Modifier.weight(1f))
                            MatrixCellField("a22", viewModel, activeFieldKey, Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Matrix B (For Multiplications)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MatrixCellField("b11", viewModel, activeFieldKey, Modifier.weight(1f))
                            MatrixCellField("b12", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MatrixCellField("b21", viewModel, activeFieldKey, Modifier.weight(1f))
                            MatrixCellField("b22", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                    }
                }
                "ALGEBRA" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Solve quadratic standard: a x² + b x + c = 0", color = TextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputConstantField("a", "coeff a", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("b", "coeff b", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("c", "coeff c", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                    }
                }
                "CALCULUS" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Function: f(x) = a x³ + b x² + c x + d", color = TextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            InputConstantField("ca", "coeff a", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("cb", "coeff b", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("cc", "coeff c", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("cd", "coeff d", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                        Divider(color = Color(0x2200E6FF), thickness = 1.dp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputConstantField("x0", "deriv point x₀", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("limA", "lower limit a", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("limB", "upper limit b", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                    }
                }
                "MOTION" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Kinematic variable state parameters", color = TextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputConstantField("u", "initial u (m/s)", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("a", "acceleration (m/s²)", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputConstantField("t", "duration t (s)", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("s", "displacement s (m)", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                    }
                }
                "CAPACITANCE" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Parallel plate physics configuration", color = TextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputConstantField("area", "Area A (m²)", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("dist", "Separation d (m)", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputConstantField("k", "Dielectric K", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("volts", "Potential V (Volts)", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                    }
                }
                "CURRENT ELECTRICITY" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Ohm's & wire constants (empty variables will solve)", color = GhostGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputConstantField("v", "Voltage V (V)", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("i", "Current I (A)", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("r", "Resistance R (Ω)", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                        Text("Specific structural parameters", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputConstantField("rho", "Resistivity ρ", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("length", "Length L (m)", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("area", "Area A (m²)", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                    }
                }
                "MOLE CONCEPT" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Substance selector & inputs", color = TextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        // Dropdown for Chemicals
                        val chemicals = listOf("H2O", "CO2", "NaCl", "C6H12O6", "O2")
                        var chemicalExpanded by remember { mutableStateOf(false) }

                        val selectedComp = viewModel.inputs["compound"] ?: "CO2"

                        Box(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = { chemicalExpanded = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                                    .background(Color(0x3300E6FF))
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Compound: $selectedComp", color = TextWhite, fontWeight = FontWeight.Bold)
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = NeonCyan)
                                }
                            }
                            DropdownMenu(
                                expanded = chemicalExpanded,
                                onDismissRequest = { chemicalExpanded = false },
                                modifier = Modifier.background(CyberSlate)
                            ) {
                                chemicals.forEach { chem ->
                                    DropdownMenuItem(
                                        text = { Text(chem, color = TextWhite) },
                                        onClick = {
                                            viewModel.updateInput("compound", chem)
                                            chemicalExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputConstantField("mass", "Input mass (grams)", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("moles", "Input moles (mol)", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                    }
                }
                "ORGANIC CHEMISTRY" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Skeletal carbon configuration space", color = TextWhite, fontSize = 11.sp)

                        // 1. Double state for Family selection
                        val groupNames = listOf("Alkanes", "Alkenes", "Alkynes", "Alcohols", "Carboxylic Acids")
                        var groupExpanded by remember { mutableStateOf(false) }
                        val activeFamily = viewModel.inputs["family"] ?: "Alkanes"

                        Box(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = { groupExpanded = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                                    .background(Color(0x22161920))
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Organic Family: $activeFamily", color = TextWhite, fontWeight = FontWeight.Bold)
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = NeonCyan)
                                }
                            }
                            DropdownMenu(
                                expanded = groupExpanded,
                                onDismissRequest = { groupExpanded = false },
                                modifier = Modifier.background(CyberSlate)
                            ) {
                                groupNames.forEach { fam ->
                                    DropdownMenuItem(
                                        text = { Text(fam, color = TextWhite) },
                                        onClick = {
                                            viewModel.updateInput("family", fam)
                                            groupExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // 2. Clickable slider / count for carbon size
                        val carbons = viewModel.inputs["carbons"]?.toIntOrNull() ?: 3
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text("Carbon Atoms ($carbons):", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Button(
                                onClick = { if (carbons > 1) viewModel.updateInput("carbons", (carbons - 1).toString()) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FF007F)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("-", color = HyperPink, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            }
                            Button(
                                onClick = { if (carbons < 6) viewModel.updateInput("carbons", (carbons + 1).toString()) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300E6FF)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("+", color = NeonCyan, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            }
                        }
                    }
                }
                "THERMODYNAMICS" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Active Energy Values", color = TextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputConstantField("dh", "ΔH Enthalpy (kJ/mol)", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("ds", "ΔS Entropy (J/mol.K)", viewModel, activeFieldKey, Modifier.weight(1f))
                            InputConstantField("temp", "Temp T (Kelvin)", viewModel, activeFieldKey, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatrixCellField(
    key: String,
    viewModel: CalculatorViewModel,
    activeKey: String?,
    modifier: Modifier = Modifier
) {
    val value = viewModel.inputs[key] ?: ""
    val isFocused = key == activeKey

    OutlinedTextField(
        value = value,
        onValueChange = { viewModel.updateInput(key, it) },
        textStyle = TextStyle(color = if (isFocused) NeonCyan else TextWhite, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = if (isFocused) NeonCyan else Color(0x22FFFFFF),
            focusedContainerColor = Color(0x1A00E6FF)
        ),
        modifier = modifier
            .testTag("matrix_${key}")
            .onFocusChanged { focusState ->
                if (focusState.isFocused && activeKey != key) {
                    viewModel.setActiveField(key)
                }
            }
    )
}

@Composable
fun InputConstantField(
    key: String,
    label: String,
    viewModel: CalculatorViewModel,
    activeKey: String?,
    modifier: Modifier = Modifier
) {
    val value = viewModel.inputs[key] ?: ""
    val isFocused = key == activeKey

    OutlinedTextField(
        value = value,
        onValueChange = { viewModel.updateInput(key, it) },
        label = { Text(label, fontSize = 10.sp, color = if (isFocused) NeonCyan else GhostGray) },
        textStyle = TextStyle(color = TextWhite, fontFamily = FontFamily.Monospace),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = if (isFocused) NeonCyan else Color(0x33FFFFFF),
            focusedLabelColor = NeonCyan
        ),
        modifier = modifier
            .testTag("input_${key}")
            .onFocusChanged { focusState ->
                if (focusState.isFocused && activeKey != key) {
                    viewModel.setActiveField(key)
                }
            }
    )
}

// ----------------------------------------------------
// CARD COMPONENT: IN-APP SCIENTIFIC helper KEYBOARD
// ----------------------------------------------------
@Composable
fun ScientificHelperKeyboard(viewModel: CalculatorViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1219)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x33FF007F), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GridOn,
                    contentDescription = "Keypad",
                    tint = HyperPink,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "QUANTUM HELPER MULTI-KEYPAD",
                    color = HyperPink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Keyboard grid structure mapping
            val keypadGrid = listOf(
                listOf("7", "8", "9", "/", "π"),
                listOf("4", "5", "6", "*", "e"),
                listOf("1", "2", "3", "-", "ε₀"),
                listOf("0", ".", "C", "+", "Δ"),
                listOf("R", "NA", "^2", "^3", "√")
            )

            keypadGrid.forEach { rowKeys ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rowKeys.forEach { keySymbol ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (keySymbol) {
                                        "C" -> Color(0x33FF007F)
                                        "+", "-", "*", "/" -> Color(0x2200E6FF)
                                        "π", "e", "ε₀", "R", "NA", "Δ", "^2", "^3", "√" -> Color(0x22FFFFD7)
                                        else -> Color(0x11FFFFFF)
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = when (keySymbol) {
                                        "C" -> HyperPink.copy(alpha = 0.5f)
                                        else -> Color.White.copy(alpha = 0.15f)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    when (keySymbol) {
                                        "C" -> {
                                            viewModel.activeFieldKey.value?.let { activeKey ->
                                                viewModel.updateInput(activeKey, "")
                                            }
                                        }
                                        "π" -> viewModel.appendFromKeyboard("3.14159")
                                        "e" -> viewModel.appendFromKeyboard("2.71828")
                                        "ε₀" -> viewModel.appendFromKeyboard("8.854e-12")
                                        "R" -> viewModel.appendFromKeyboard("8.314")
                                        "NA" -> viewModel.appendFromKeyboard("6.022e23")
                                        else -> viewModel.appendFromKeyboard(keySymbol)
                                    }
                                }
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Text(
                                text = keySymbol,
                                color = when (keySymbol) {
                                    "C" -> HyperPink
                                    "+", "-", "*", "/" -> NeonCyan
                                    "π", "e", "ε₀", "R", "NA", "Δ", "^2", "^3", "√" -> CosmicGold
                                    else -> TextWhite
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Backspace custom button only on the double index of zero
                    if (rowKeys == keypadGrid[3]) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x228A99AD))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .clickable { viewModel.backspaceKeyboard() }
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = "Backspace",
                                tint = TextWhite,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// CARD COMPONENT: STEP STATE LOCAL SOLUTION ANSWERS
// ----------------------------------------------------
@Composable
fun SolutionAnswerCard(
    calcResult: CalculationResult?,
    context: android.content.Context,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkGlass),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Content
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Solution Engine",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HYBRID LOCAL SOLUTION ENGINE",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Copy single click actions
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy to clipboard",
                    tint = NeonCyan,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                            val rawText = calcResult?.steps ?: "No derivation steps."
                            clipboardManager.setText(AnnotatedString(rawText))
                            Toast
                                .makeText(context, "Step-by-step solution copied!", Toast.LENGTH_SHORT)
                                .show()
                        }
                )
            }

            // Answer Summary Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1219))
                    .border(1.dp, Color(0x3300E6FF), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "COMPUTED ANSWER RESULT",
                        color = GhostGray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = calcResult?.finalAnswer ?: "PENDING ENTRANCE",
                        color = NeonGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Show calculated derivation steps nicely formatted
            Text(
                text = "SYSTEM DERIVATION STEPS & SUBSTITUTIONS:",
                color = GhostGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .background(Color(0x33060709))
                    .border(0.7.dp, Color(0x228A99AD), RoundedCornerShape(4.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Text(
                    text = calcResult?.steps ?: "Configure the parameters to compute step outputs.",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

// ----------------------------------------------------
// CARD COMPONENT: QUANTUM TUTOR AI LESSON
// ----------------------------------------------------
@Composable
fun QuantumTutorConsultCard(
    viewModel: CalculatorViewModel,
    isMistakeChecker: Boolean,
    isLoading: Boolean,
    aiText: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkGlass),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI Tutor",
                        tint = HyperPink,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "QUANTUMTUTOR AI BLOCK",
                        color = HyperPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Mistake Checker switch module
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.toggleMistakeChecker(!isMistakeChecker) }
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "MISTAKE SCAN",
                        color = if (isMistakeChecker) HyperPink else GhostGray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Checkbox(
                        checked = isMistakeChecker,
                        onCheckedChange = { viewModel.toggleMistakeChecker(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = HyperPink,
                            checkmarkColor = TextWhite
                        ),
                        modifier = Modifier
                            .scale(0.7f)
                            .testTag("mistake_checker_toggle")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Engage Button
            Button(
                onClick = { viewModel.askQuantumTutor() },
                colors = ButtonDefaults.buttonColors(containerColor = HyperPink),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("engage_tutor_button"),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Engage UI",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isMistakeChecker) "ENGAGE MISTAKE SCANNER" else "ENGAGE TUTOR CONTEXT",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Response Box Text Area
            if (aiText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = Color(0x33FF007F), thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = HyperPink)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                        .background(Color(0x33060709))
                        .border(1.dp, Color(0x1AFF007F), RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    val annotatedAiText = parseMarkdownToAnnotatedString(
                        text = aiText,
                        primaryColor = NeonCyan,
                        headerColor = HyperPink
                    )
                    Text(
                        text = annotatedAiText,
                        color = TextWhite,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// CUSTOM UTILITIES: STYLED MARKDOWN PARSER FOR COMPOSE
// ----------------------------------------------------
fun parseMarkdownToAnnotatedString(
    text: String,
    primaryColor: Color,
    headerColor: Color
): AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")
        lines.forEach { line ->
            when {
                line.startsWith("## ") -> {
                    withStyle(SpanStyle(color = headerColor, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)) {
                        append(line.substring(3).trim().uppercase())
                    }
                    append("\n")
                }
                line.startsWith("### ") -> {
                    withStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                        append(line.substring(4).trim())
                    }
                    append("\n")
                }
                line.startsWith("- ") -> {
                    withStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.ExtraBold)) {
                        append(" ✦ ")
                    }
                    append(line.substring(2).trim())
                    append("\n")
                }
                line.startsWith("* ") -> {
                    withStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.ExtraBold)) {
                        append(" ✦ ")
                    }
                    append(line.substring(2).trim())
                    append("\n")
                }
                else -> {
                    // Quick inline bold check represented by **text** -> styling the elements carefully
                    var index = 0
                    val segment = line
                    while (index < segment.length) {
                        val boldStart = segment.indexOf("**", index)
                        if (boldStart != -1) {
                            append(segment.substring(index, boldStart))
                            val boldEnd = segment.indexOf("**", boldStart + 2)
                            if (boldEnd != -1) {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = CosmicGold)) {
                                    append(segment.substring(boldStart + 2, boldEnd))
                                }
                                index = boldEnd + 2
                            } else {
                                append("**")
                                index = boldStart + 2
                            }
                        } else {
                            append(segment.substring(index))
                            break
                        }
                    }
                    append("\n")
                }
            }
        }
    }
}
