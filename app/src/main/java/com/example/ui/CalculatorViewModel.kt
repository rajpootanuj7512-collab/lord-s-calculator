package com.example.ui

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.CalculationResult
import com.example.calculator.SolverEngine
import com.example.network.GeminiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CalculatorViewModel : ViewModel() {

    // Subjects and their corresponding Chapters
    val subjectMap = mapOf(
        "Mathematics" to listOf("Matrix", "Algebra", "Calculus"),
        "Physics" to listOf("Motion", "Capacitance", "Current Electricity"),
        "Chemistry" to listOf("Mole Concept", "Organic Chemistry", "Thermodynamics")
    )

    // Chapters and their corresponding Operations
    val operationMap = mapOf(
        "Matrix" to listOf("Determinant", "Inverse", "Multiplication", "Eigenvalues"),
        "Algebra" to listOf("Roots", "Vertex", "Discriminant"),
        "Calculus" to listOf("Derivative", "Definite Integral"),
        "Motion" to listOf("Final_Velocity", "Displacement", "Velocity_Squared"),
        "Capacitance" to listOf("Capacitance", "Energy_Stored", "Electric_Field"),
        "Current Electricity" to listOf("Ohm_Law", "Resistivity"),
        "Mole Concept" to listOf("Mass_To_Moles", "Moles_To_Mass"),
        "Organic Chemistry" to listOf("IUPAC_Name", "Combustion"),
        "Thermodynamics" to listOf("Gibbs_Free_Energy", "Spontaneity")
    )

    private val _selectedSubject = MutableStateFlow("Mathematics")
    val selectedSubject: StateFlow<String> = _selectedSubject

    private val _selectedChapter = MutableStateFlow("Matrix")
    val selectedChapter: StateFlow<String> = _selectedChapter

    private val _activeOperation = MutableStateFlow("Determinant")
    val activeOperation: StateFlow<String> = _activeOperation

    // Holds user inputs for dynamic fields
    val inputs = mutableStateMapOf<String, String>()

    // Currently focused text field key for scientific helper keyboard injection
    private val _activeFieldKey = MutableStateFlow<String?>(null)
    val activeFieldKey: StateFlow<String?> = _activeFieldKey

    // Local computed physics/chemistry/math step answers
    private val _calculationResult = MutableStateFlow<CalculationResult?>(null)
    val calculationResult: StateFlow<CalculationResult?> = _calculationResult

    // AI generated dynamic lesson state
    private val _aiOutput = MutableStateFlow("")
    val aiOutput: StateFlow<String> = _aiOutput

    private val _isLoadingAi = MutableStateFlow(false)
    val isLoadingAi: StateFlow<Boolean> = _isLoadingAi

    private val _isMistakeCheckerEnabled = MutableStateFlow(false)
    val isMistakeCheckerEnabled: StateFlow<Boolean> = _isMistakeCheckerEnabled

    init {
        resetAndLoadDefaultInputs()
        runLocalCalculation()
    }

    fun selectSubject(subject: String) {
        if (subjectMap.containsKey(subject)) {
            _selectedSubject.value = subject
            val firstChapter = subjectMap[subject]?.firstOrNull() ?: ""
            selectChapter(firstChapter)
        }
    }

    fun selectChapter(chapter: String) {
        _selectedChapter.value = chapter
        val firstOperation = operationMap[chapter]?.firstOrNull() ?: ""
        selectOperation(firstOperation)
        resetAndLoadDefaultInputs()
        runLocalCalculation()
    }

    fun selectOperation(operation: String) {
        _activeOperation.value = operation
        runLocalCalculation()
    }

    fun updateInput(key: String, value: String) {
        inputs[key] = value
        runLocalCalculation()
    }

    fun setActiveField(key: String?) {
        _activeFieldKey.value = key
    }

    fun toggleMistakeChecker(enabled: Boolean) {
        _isMistakeCheckerEnabled.value = enabled
    }

    /**
     * Appends a character or symbol from the Scientific Keyboard into the focused field.
     */
    fun appendFromKeyboard(symbol: String) {
        val key = _activeFieldKey.value ?: return
        val currentText = inputs[key] ?: ""
        val updatedText = currentText + symbol
        updateInput(key, updatedText)
    }

    /**
     * Backspaces the last character of the active focused text field.
     */
    fun backspaceKeyboard() {
        val key = _activeFieldKey.value ?: return
        val currentText = inputs[key] ?: ""
        if (currentText.isNotEmpty()) {
            val updatedText = currentText.dropLast(1)
            updateInput(key, updatedText)
        }
    }

    /**
     * Computes the mathematical and physical answers locally in real-time.
     */
    fun runLocalCalculation() {
        val result = SolverEngine.calculate(
            subject = _selectedSubject.value,
            chapter = _selectedChapter.value,
            operation = _activeOperation.value,
            inputs = inputs.toMap()
        )
        _calculationResult.value = result
    }

    /**
     * Triggers the AI model (Gemini) to explain or detect mistakes in the current calculation.
     */
    fun askQuantumTutor() {
        val currentLocalSteps = _calculationResult.value?.steps ?: "No steps available."
        val currentInputsMap = inputs.toMap()
        val chapterVal = _selectedChapter.value
        val subjectVal = _selectedSubject.value
        val operationVal = _activeOperation.value
        val checkMode = _isMistakeCheckerEnabled.value

        _isLoadingAi.value = true
        _aiOutput.value = "Establishing neural downlink with QuantumTutor..."

        viewModelScope.launch {
            val response = GeminiClient.tutorExplain(
                subject = subjectVal,
                chapter = chapterVal,
                operation = operationVal,
                inputs = currentInputsMap,
                localCalculationSteps = currentLocalSteps,
                isMistakeCheckMode = checkMode
            )
            _aiOutput.value = response
            _isLoadingAi.value = false
        }
    }

    /**
     * Resets input fields with suitable initial default values for each topic to minimize empty states.
     */
    fun resetAndLoadDefaultInputs() {
        inputs.clear()
        _aiOutput.value = "" // Clear tutor box on chapter shift
        when (_selectedChapter.value.uppercase()) {
            "MATRIX" -> {
                inputs["a11"] = "2"
                inputs["a12"] = "1"
                inputs["a21"] = "1"
                inputs["a22"] = "3"

                inputs["b11"] = "1"
                inputs["b12"] = "0"
                inputs["b21"] = "0"
                inputs["b22"] = "1"
                _activeFieldKey.value = "a11"
            }
            "ALGEBRA" -> {
                inputs["a"] = "1"
                inputs["b"] = "-5"
                inputs["c"] = "6"
                _activeFieldKey.value = "a"
            }
            "CALCULUS" -> {
                inputs["ca"] = "1"  // x^3
                inputs["cb"] = "-3" // x^2
                inputs["cc"] = "2"  // x
                inputs["cd"] = "1"  // constant
                inputs["x0"] = "2"
                inputs["limA"] = "0"
                inputs["limB"] = "3"
                _activeFieldKey.value = "ca"
            }
            "MOTION" -> {
                inputs["u"] = "5"   // initial velocity
                inputs["a"] = "9.8" // acc
                inputs["t"] = "4"   // time
                inputs["s"] = "0"   // displacement (if needed)
                _activeFieldKey.value = "u"
            }
            "CAPACITANCE" -> {
                inputs["area"] = "0.05" // m2
                inputs["dist"] = "0.002" // m (2mm)
                inputs["k"] = "3"       // Dielectric
                inputs["volts"] = "12"  // Voltage
                _activeFieldKey.value = "area"
            }
            "CURRENT ELECTRICITY" -> {
                inputs["v"] = "12"
                inputs["i"] = "2"
                inputs["r"] = "6"
                inputs["rho"] = "1.68e-8"
                inputs["length"] = "15"
                inputs["area"] = "1e-6"
                _activeFieldKey.value = "v"
            }
            "MOLE CONCEPT" -> {
                inputs["compound"] = "CO2"
                inputs["mass"] = "88"
                inputs["moles"] = "2"
                _activeFieldKey.value = "mass"
            }
            "ORGANIC CHEMISTRY" -> {
                inputs["carbons"] = "3"
                inputs["family"] = "Alkanes"
                _activeFieldKey.value = "carbons"
            }
            "THERMODYNAMICS" -> {
                inputs["dh"] = "-135"  // kJ/mol
                inputs["ds"] = "-180"  // J/mol.K
                inputs["temp"] = "298" // K
                _activeFieldKey.value = "dh"
            }
        }
    }
}
