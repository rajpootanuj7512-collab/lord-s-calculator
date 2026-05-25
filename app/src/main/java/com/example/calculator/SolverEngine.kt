package com.example.calculator

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.roundToInt

data class CalculationResult(
    val finalAnswer: String,
    val steps: String,
    val graphType: String,
    val graphParams: Map<String, Any> = emptyMap()
)

object SolverEngine {

    /**
     * Main entry point for local calculations.
     */
    fun calculate(
        subject: String,
        chapter: String,
        operation: String,
        inputs: Map<String, String>
    ): CalculationResult {
        return try {
            when (subject.uppercase()) {
                "MATHEMATICS", "MATH" -> solveMath(chapter, operation, inputs)
                "PHYSICS" -> solvePhysics(chapter, operation, inputs)
                "CHEMISTRY" -> solveChemistry(chapter, operation, inputs)
                else -> CalculationResult(
                    "Select a valid subject.",
                    "No subject selection.",
                    "NONE"
                )
            }
        } catch (e: Exception) {
            CalculationResult(
                "Error",
                "Failed to compute: ${e.localizedMessage ?: "Invalid numerical inputs. Please ensure all inputs are correctly filled."}",
                "NONE"
            )
        }
    }

    private fun solveMath(chapter: String, operation: String, inputs: Map<String, String>): CalculationResult {
        return when (chapter.uppercase()) {
            "MATRIX" -> solveMatrix(operation, inputs)
            "ALGEBRA" -> solveAlgebra(operation, inputs)
            "CALCULUS" -> solveCalculus(operation, inputs)
            else -> CalculationResult("Unknown Chapter", "Select a valid chapter.", "NONE")
        }
    }

    private fun solvePhysics(chapter: String, operation: String, inputs: Map<String, String>): CalculationResult {
        return when (chapter.uppercase()) {
            "MOTION" -> solveMotion(operation, inputs)
            "CAPACITANCE" -> solveCapacitance(operation, inputs)
            "CURRENT ELECTRICITY" -> solveElectricity(operation, inputs)
            else -> CalculationResult("Unknown Chapter", "Select a valid chapter.", "NONE")
        }
    }

    private fun solveChemistry(chapter: String, operation: String, inputs: Map<String, String>): CalculationResult {
        return when (chapter.uppercase()) {
            "MOLE CONCEPT" -> solveMoles(operation, inputs)
            "ORGANIC CHEMISTRY" -> solveOrganic(operation, inputs)
            "THERMODYNAMICS" -> solveThermodynamics(operation, inputs)
            else -> CalculationResult("Unknown Chapter", "Select a valid chapter.", "NONE")
        }
    }

    // ==========================================
    // MATHEMATICS: MATRIX SOLVER (Supports 2x2)
    // ==========================================
    private fun solveMatrix(operation: String, inputs: Map<String, String>): CalculationResult {
        // Read 2x2 Matrix A
        val a11 = inputs["a11"]?.toDoubleOrNull() ?: 1.0
        val a12 = inputs["a12"]?.toDoubleOrNull() ?: 0.0
        val a21 = inputs["a21"]?.toDoubleOrNull() ?: 0.0
        val a22 = inputs["a22"]?.toDoubleOrNull() ?: 1.0

        // Read 2x2 Matrix B
        val b11 = inputs["b11"]?.toDoubleOrNull() ?: 1.0
        val b12 = inputs["b12"]?.toDoubleOrNull() ?: 0.0
        val b21 = inputs["b21"]?.toDoubleOrNull() ?: 0.0
        val b22 = inputs["b22"]?.toDoubleOrNull() ?: 1.0

        val matrixAStr = "A = [[$a11, $a12], [$a21, $a22]]"
        val matrixBStr = "B = [[$b11, $b12], [$b21, $b22]]"

        val detA = (a11 * a22) - (a12 * a21)

        return when (operation.uppercase()) {
            "DETERMINANT" -> {
                val stepStr = """
                    Subject: Mathematics -> Chapter: Matrix -> Operation: Determinant
                    
                    Matrix A under evaluation:
                    [ $a11   $a12 ]
                    [ $a21   $a22 ]
                    
                    FORMULA FOR 2x2 DETERMINANT:
                    det(A) = |A| = (a11 * a22) - (a12 * a21)
                    
                    SUBSTITUTION STEPS:
                    det(A) = ($a11 * $a22) - ($a12 * $a21)
                    det(A) = ${a11 * a22} - ${a12 * a21}
                    det(A) = $detA
                    
                    FINAL ANSWER:
                    |A| = $detA
                """.trimIndent()
                CalculationResult(
                    detA.toString(),
                    stepStr,
                    "MATRIX",
                    mapOf("a11" to a11, "a12" to a12, "a21" to a21, "a22" to a22)
                )
            }
            "INVERSE" -> {
                if (detA == 0.0) {
                    return CalculationResult(
                        "Undefined (Singular Matrix)",
                        "Matrix A is singular because det(A) = 0. An inverse matrix only exists for non-singular matrices where the determinant is non-zero.",
                        "MATRIX",
                        mapOf("a11" to a11, "a12" to a12, "a21" to a21, "a22" to a22)
                    )
                }

                val i11 = a22 / detA
                val i12 = -a12 / detA
                val i21 = -a21 / detA
                val i22 = a11 / detA

                val stepStr = """
                    Subject: Mathematics -> Chapter: Matrix -> Operation: Inverse
                    
                    Matrix A:
                    [ $a11   $a12 ]
                    [ $a21   $a22 ]
                    
                    FORMULA FOR 2x2 INVERSE MATRIX:
                    A⁻¹ = (1 / det(A)) * Adj(A)
                    where Adj(A) = [ a22   -a12 ]
                                   [ -a21   a11 ]
                    
                    1. CALCULATING DETERMINANT:
                    det(A) = ($a11 * $a22) - ($a12 * $a21) = $detA
                    
                    2. FORMING ADJUGATE MATRIX Adj(A):
                    Adj(A) = [ $a22   ${-a12} ]
                             [ ${-a21}   $a11 ]
                    
                    3. MULTIPLY BY (1 / det):
                    A⁻¹ = (1 / $detA) * [ $a22   ${-a12} ]
                                       [ ${-a21}   $a11 ]
                    
                    Elements calculated:
                    inv_11 = $a22 / $detA = $i11
                    inv_12 = ${-a12} / $detA = $i12
                    inv_21 = ${-a21} / $detA = $i21
                    inv_22 = $a11 / $detA = $i22
                    
                    FINAL ANSWER:
                    A⁻¹ = 
                    [ $i11   $i12 ]
                    [ $i21   $i22 ]
                """.trimIndent()

                CalculationResult(
                    "[[${i11.round(3)}, ${i12.round(3)}], [${i21.round(3)}, ${i22.round(3)}]]",
                    stepStr,
                    "MATRIX",
                    mapOf("a11" to i11, "a12" to i12, "a21" to i21, "a22" to i22, "isInverse" to true)
                )
            }
            "MULTIPLICATION" -> {
                val c11 = (a11 * b11) + (a12 * b21)
                val c12 = (a11 * b12) + (a12 * b22)
                val c21 = (a21 * b11) + (a22 * b21)
                val c22 = (a21 * b12) + (a22 * b22)

                val stepStr = """
                    Subject: Mathematics -> Chapter: Matrix -> Operation: Multiplication
                    
                    Matrix A:
                    [ $a11   $a12 ]
                    [ $a21   $a22 ]
                    
                    Matrix B:
                    [ $b11   $b12 ]
                    [ $b21   $b22 ]
                    
                    FORMULA FOR MATRIX PRODUCT C = A * B:
                    c11 = a11*b11 + a12*b21
                    c12 = a11*b12 + a12*b22
                    c21 = a21*b11 + a22*b21
                    c22 = a21*b12 + a22*b22
                    
                    SUBSTITUTION STEPS:
                    c11 = ($a11 * $b11) + ($a12 * $b21) = ${a11 * b11} + ${a12 * b21} = $c11
                    c12 = ($a11 * $b12) + ($a12 * $b22) = ${a11 * b12} + ${a12 * b22} = $c12
                    c21 = ($a21 * $b11) + ($a22 * $b21) = ${a21 * b11} + ${a22 * b21} = $c21
                    c22 = ($a21 * $b12) + ($a22 * $b22) = ${a21 * b12} + ${a22 * b22} = $c22
                    
                    FINAL ANSWER:
                    A * B = 
                    [ $c11   $c12 ]
                    [ $c21   $c22 ]
                """.trimIndent()

                CalculationResult(
                    "[[$c11, $c12], [$c21, $c22]]",
                    stepStr,
                    "MATRIX",
                    mapOf("a11" to c11, "a12" to c12, "a21" to c21, "a22" to c22)
                )
            }
            "EIGENVALUES" -> {
                // Find eigenvalues of Matrix A.
                // Characteristic Eq: det(A - lambda*I) = 0
                // det([a11-L, a12; a21, a22-L]) = (a11 - L)(a22 - L) - a12*a21 = 0
                // L^2 - (a11 + a22)L + (a11*a22 - a12*a21) = 0
                // L^2 - Trace(A)*L + det(A) = 0
                val trace = a11 + a22
                val det = detA

                // Solving L^2 - trace*L + det = 0 using quadratic formula
                // D = trace^2 - 4*det
                val disc = (trace * trace) - (4 * det)

                val answer: String
                val stepStr: String

                if (disc >= 0) {
                    val l1 = (trace + sqrt(disc)) / 2.0
                    val l2 = (trace - sqrt(disc)) / 2.0
                    answer = "λ₁ = ${l1.round(3)}, λ₂ = ${l2.round(3)}"
                    stepStr = """
                        Subject: Mathematics -> Chapter: Matrix -> Operation: Eigenvalues
                        
                        Matrix A:
                        [ $a11   $a12 ]
                        [ $a21   $a22 ]
                        
                        FORMULA FOR CHARACTERISTIC EQUATION:
                        det(A - λI) = 0  =>  λ² - Tr(A)λ + det(A) = 0
                        where Tr(A) (Trace) = a11 + a22
                        and det(A) = a11 * a22 - a12 * a21
                        
                        SUBSTITUTION & EXTRACTION:
                        Tr(A) = $a11 + $a22 = $trace
                        det(A) = $det
                        
                        Characteristic polynomial:
                        λ² - $trace λ + ($det) = 0
                        
                        SOLVING USING QUADRATIC FORMULA FOR λ:
                        Discriminant (D) = Tr(A)² - 4*det(A)
                        D = ($trace)² - 4*($det) = ${trace * trace} - ${4 * det} = $disc
                        
                        Roots:
                        λ = (Tr(A) ± √D) / 2
                        λ₁ = ($trace + √$disc) / 2 = $l1
                        λ₂ = ($trace - √$disc) / 2 = $l2
                        
                        FINAL ANSWER:
                        Eigenvalues:
                        λ₁ = $l1
                        λ₂ = $l2
                    """.trimIndent()
                } else {
                    // Complex eigenvalues
                    val realPart = trace / 2.0
                    val imPart = sqrt(-disc) / 2.0
                    answer = "λ = ${realPart.round(3)} ± ${imPart.round(3)}i"
                    stepStr = """
                        Subject: Mathematics -> Chapter: Matrix -> Operation: Eigenvalues (Complex Roots)
                        
                        Matrix A:
                        [ $a11   $a12 ]
                        [ $a21   $a22 ]
                        
                        FORMULA FOR CHARACTERISTIC EQUATION:
                        λ² - Tr(A)λ + det(A) = 0
                        Tr(A) = $trace
                        det(A) = $det
                        
                        Discriminant (D) = $trace² - 4*($det) = $disc
                        Since D < 0, roots are imaginary (complex conjugates).
                        
                        λ = (Tr(A) ± i√|D|) / 2
                        λ = ($trace ± i√${-disc}) / 2
                        λ = $realPart ± $imPart i
                        
                        FINAL ANSWER:
                        Eigenvalues:
                        λ = $realPart ± $imPart i
                    """.trimIndent()
                }

                CalculationResult(
                    answer,
                    stepStr,
                    "MATRIX",
                    mapOf("a11" to a11, "a12" to a12, "a21" to a21, "a22" to a22)
                )
            }
            else -> CalculationResult("Select Operation", "No math operation.", "NONE")
        }
    }

    // ==========================================
    // MATHEMATICS: ALGEBRA (QUADRATIC SOLVER)
    // ==========================================
    private fun solveAlgebra(operation: String, inputs: Map<String, String>): CalculationResult {
        val a = inputs["a"]?.toDoubleOrNull() ?: 1.0
        val b = inputs["b"]?.toDoubleOrNull() ?: -5.0
        val c = inputs["c"]?.toDoubleOrNull() ?: 6.0

        if (a == 0.0) {
            return CalculationResult(
                "Invalid (a cannot be zero)",
                "To be a quadratic equation, the coefficient 'a' of x² must be non-zero. Otherwise it becomes a linear equation bx + c = 0.",
                "NONE"
            )
        }

        val disc = b * b - 4 * a * c

        return when (operation.uppercase()) {
            "ROOTS" -> {
                val ans: String
                val stepStr: String
                if (disc > 0) {
                    val r1 = (-b + sqrt(disc)) / (2 * a)
                    val r2 = (-b - sqrt(disc)) / (2 * a)
                    ans = "x₁ = ${r1.round(3)}, x₂ = ${r2.round(3)}"
                    stepStr = """
                        Subject: Mathematics -> Chapter: Algebra -> Operation: Real Roots
                        
                        Equation: $a x² + ($b) x + ($c) = 0
                        
                        FORMULA (Quadratic Formula):
                        x = (-b ± √(b² - 4ac)) / 2a
                        
                        SUBSTITUTION & CALCULATIONS:
                        1. Compute Discriminant D = b² - 4ac:
                           D = ($b)² - 4*($a)*($c)
                           D = ${b * b} - ${4 * a * c}
                           D = $disc
                        
                        2. Since D > 0, we have two distinct real roots:
                           x₁ = (-($b) + √$disc) / (2 * $a)
                           x₁ = (${-b} + ${sqrt(disc)}) / ${2 * a}
                           x₁ = $r1
                           
                           x₂ = (-($b) - √$disc) / (2 * $a)
                           x₂ = (${-b} - ${sqrt(disc)}) / ${2 * a}
                           x₂ = $r2
                        
                        FINAL ANSWER:
                        Roots of the equation are:
                        x₁ = $r1
                        x₂ = $r2
                    """.trimIndent()
                } else if (disc == 0.0) {
                    val r = -b / (2 * a)
                    ans = "x₁ = x₂ = ${r.round(3)}"
                    stepStr = """
                        Subject: Mathematics -> Chapter: Algebra -> Operation: Equal Real Roots
                        
                        Equation: $a x² + ($b) x + ($c) = 0
                        
                        FORMULA:
                        x = -b / 2a
                        
                        SUBSTITUTION:
                        1. Discriminant D = ($b)² - 4*($a)*($c) = 0
                        2. Since D = 0, the equation has one repeating real root:
                           x = -($b) / (2 * $a)
                           x = ${-b} / ${2 * a}
                           x = $r
                        
                        FINAL ANSWER:
                        Double Root:
                        x = $r
                    """.trimIndent()
                } else {
                    val real = -b / (2 * a)
                    val imag = sqrt(-disc) / (2 * a)
                    ans = "x = ${real.round(3)} ± ${imag.round(3)}i"
                    stepStr = """
                        Subject: Mathematics -> Chapter: Algebra -> Operation: Complex Roots
                        
                        Equation: $a x² + ($b) x + ($c) = 0
                        
                        FORMULA:
                        x = (-b ± i√|D|) / 2a
                        
                        SUBSTITUTION:
                        1. Discriminant D = ($b)² - 4*($a)*($c) = $disc
                        2. Since D < 0, we have complex conjugate roots:
                           Real part = -($b) / (2 * $a) = $real
                           Imaginary part = √${-disc} / (2 * $a) = $imag
                           
                        FINAL ANSWER:
                        Roots of the equation are complex:
                        x = $real ± $imag i
                    """.trimIndent()
                }

                CalculationResult(
                    ans,
                    stepStr,
                    "QUADRATIC",
                    mapOf("a" to a, "b" to b, "c" to c)
                )
            }
            "VERTEX" -> {
                val vx = -b / (2 * a)
                val vy = -disc / (4 * a)
                val shapeDirection = if (a > 0) "upwards (Minimum point)" else "downwards (Maximum point)"

                val stepStr = """
                    Subject: Mathematics -> Chapter: Algebra -> Operation: Vertex Coordinates
                    
                    Equation: y = $a x² + ($b) x + ($c)
                    
                    FORMULA FOR VERTEX (V_h, V_k):
                    h = -b / 2a
                    k = -D / 4a = f(h)
                    where D (Discriminant) = b² - 4ac = $disc
                    
                    SUBSTITUTION & CALCULATIONS:
                    h = -($b) / (2 * $a) = $vx
                    k = -($disc) / (4 * $a) = $vy
                    
                    Since 'a' ($a) ${if (a > 0) "> 0" else "< 0"}, the parabola opens $shapeDirection.
                    
                    FINAL ANSWER:
                    Vertex is at Coord (h, k) = ($vx, $vy)
                """.trimIndent()

                CalculationResult(
                    "($vx, $vy)",
                    stepStr,
                    "QUADRATIC",
                    mapOf("a" to a, "b" to b, "c" to c, "vx" to vx, "vy" to vy)
                )
            }
            "DISCRIMINANT" -> {
                val descrip = when {
                    disc > 0 -> "Positive (Two distinct real roots, intersects x-axis twice)"
                    disc == 0.0 -> "Zero (One repeating real root, tangent to x-axis)"
                    else -> "Negative (Two complex solutions, no x-axis crossings)"
                }

                val stepStr = """
                    Subject: Mathematics -> Chapter: Algebra -> Operation: Discriminant Analysis
                    
                    Equation: $a x² + ($b) x + ($c) = 0
                    
                    FORMULA:
                    D = b² - 4ac
                    
                    SUBSTITUTION:
                    D = ($b)² - 4*($a)*($c)
                    D = ${b * b} - ${4 * a * c}
                    D = $disc
                    
                    PROPERTIES EVALUATED:
                    Status: $descrip
                    
                    FINAL ANSWER:
                    D = $disc
                """.trimIndent()

                CalculationResult(
                    disc.toString(),
                    stepStr,
                    "QUADRATIC",
                    mapOf("a" to a, "b" to b, "c" to c)
                )
            }
            else -> CalculationResult("Select Operation", "No operation applied.", "NONE")
        }
    }

    // ==========================================
    // MATHEMATICS: CALCULUS (POLYNOMIAL: ax³ + bx² + cx + d)
    // ==========================================
    private fun solveCalculus(operation: String, inputs: Map<String, String>): CalculationResult {
        val a = inputs["ca"]?.toDoubleOrNull() ?: 1.0 // Coefficient of x³
        val b = inputs["cb"]?.toDoubleOrNull() ?: -3.0 // Coefficient of x²
        val c = inputs["cc"]?.toDoubleOrNull() ?: 2.0 // Coefficient of x
        val d = inputs["cd"]?.toDoubleOrNull() ?: 1.0 // Constant

        val x0 = inputs["x0"]?.toDoubleOrNull() ?: 2.0 // point for derivative
        val limA = inputs["limA"]?.toDoubleOrNull() ?: 0.0 // lower limit
        val limB = inputs["limB"]?.toDoubleOrNull() ?: 3.0 // upper limit

        val expression = "${a}x³ + (${b})x² + (${c})x + (${d})"

        return when (operation.uppercase()) {
            "DERIVATIVE" -> {
                val derA = 3 * a
                val derB = 2 * b
                val derC = c
                val derExpression = "${derA}x² + (${derB})x + (${derC})"

                // Evaluate at x0: f'(x0) = derA * x0^2 + derB * x0 + derC
                val valAtX0 = derA * x0.pow(2) + derB * x0 + derC

                val stepStr = """
                    Subject: Mathematics -> Chapter: Calculus -> Operation: Differentiation
                    
                    Original Function f(x) = $expression
                    Target Evaluation Point x = $x0
                    
                    RULES USED (Power Rule):
                    d/dx [ xⁿ ] = n * xⁿ⁻¹
                    d/dx [ C * f(x) ] = C * f'(x)
                    d/dx [ constant ] = 0
                    
                    STEP-BY-STEP DIFFERENTIATION:
                    f'(x) = d/dx [ $a x³ ] + d/dx [ $b x² ] + d/dx [ $c x ] + d/dx [ $d ]
                    f'(x) = ($a * 3)x² + ($b * 2)x + ($c * 1) + 0
                    f'(x) = $derExpression
                    
                    SUBSTITUTING x = $x0 INTO DERIVATIVE FUNCTION f'(x):
                    f'($x0) = $derA*($x0)² + ($derB)*($x0) + ($derC)
                    f'($x0) = $derA*(${x0.pow(2)}) + ${derB * x0} + ($derC)
                    f'($x0) = ${derA * x0.pow(2)} + ${derB * x0} + ($derC)
                    f'($x0) = $valAtX0
                    
                    FINAL ANSWER:
                    Derivative function: f'(x) = $derExpression
                    Value at x = $x0 is f'($x0) = $valAtX0
                """.trimIndent()

                CalculationResult(
                    "f'(x) = $derExpression\nf'($x0) = $valAtX0",
                    stepStr,
                    "CALCULUS",
                    mapOf("ca" to a, "cb" to b, "cc" to c, "cd" to d, "deriv" to true, "x0" to x0)
                )
            }
            "DEFINITE INTEGRAL" -> {
                // Antiderivative F(x) = (a/4)x^4 + (b/3)x^3 + (c/2)x^2 + d*x
                val antA = a / 4.0
                val antB = b / 3.0
                val antC = c / 2.0
                val antD = d

                val evaluateF = { x: Double ->
                    antA * x.pow(4) + antB * x.pow(3) + antC * x.pow(2) + antD * x
                }

                val fB = evaluateF(limB)
                val fA = evaluateF(limA)
                val integralResult = fB - fA

                val antiderivExp = "(${antA.round(3)})x⁴ + (${antB.round(3)})x³ + (${antC.round(3)})x² + (${antD.round(3)})x"

                val stepStr = """
                    Subject: Mathematics -> Chapter: Calculus -> Operation: Definite Integration
                    
                    Function to integrate f(x) = $expression
                    Limits: Lower a = $limA, Upper b = $limB
                    
                    RULES USED:
                    ∫ xⁿ dx = (xⁿ⁺¹) / (n+1)
                    ∫ f(x) dx from a to b = F(b) - F(a)  [Fundamental Theorem of Calculus]
                    
                    1. COMPUTE ANTIDERIVATIVE F(x):
                       F(x) = ∫ ($a x³ + $b x² + $c x + $d) dx
                       F(x) = ($a / 4)x⁴ + ($b / 3)x³ + ($c / 2)x² + ($d)x + C
                       F(x) = $antiderivExp
                       
                    2. EVALUATING AT UPPER LIMIT b = $limB:
                       F($limB) = ${antA.round(3)}*($limB)⁴ + ${antB.round(3)}*($limB)³ + ${antC.round(3)}*($limB)² + $antD*($limB)
                       F($limB) = ${evaluateF(limB)}
                       
                    3. EVALUATING AT LOWER LIMIT a = $limA:
                       F($limA) = ${antA.round(3)}*($limA)⁴ + ${antB.round(3)}*($limA)³ + ${antC.round(3)}*($limA)² + $antD*($limA)
                       F($limA) = ${evaluateF(limA)}
                       
                    4. APPLYING F(b) - F(a):
                       Integral = F($limB) - F($limA)
                       Integral = ${evaluateF(limB)} - ${evaluateF(limA)}
                       Integral = $integralResult
                       
                    FINAL ANSWER:
                    ∫ f(x) dx = $integralResult
                """.trimIndent()

                CalculationResult(
                    integralResult.toString(),
                    stepStr,
                    "CALCULUS",
                    mapOf("ca" to a, "cb" to b, "cc" to c, "cd" to d, "integral" to true, "limA" to limA, "limB" to limB)
                )
            }
            else -> CalculationResult("Select Operation", "No operation applied.", "NONE")
        }
    }

    // ==========================================
    // PHYSICS: MOTION (KINEMATICS)
    // ==========================================
    private fun solveMotion(operation: String, inputs: Map<String, String>): CalculationResult {
        val u = inputs["u"]?.toDoubleOrNull() ?: 0.0 // initial speed
        val a = inputs["a"]?.toDoubleOrNull() ?: 9.8 // acceleration
        val t = inputs["t"]?.toDoubleOrNull() ?: 5.0 // time
        val s = inputs["s"]?.toDoubleOrNull() ?: 0.0 // displacement (optional depending on oper)

        return when (operation.uppercase()) {
            "FINAL_VELOCITY" -> { // Find v given u, a, t
                val v = u + a * t
                val stepStr = """
                    Subject: Physics -> Chapter: Motion -> Operation: Solve Final Velocity
                    
                    GIVEN VARIABLES:
                    Initial Velocity (u) = $u m/s
                    Acceleration (a) = $a m/s²
                    Time Duration (t) = $t s
                    
                    EQUATION OF MOTION:
                    v = u + a*t
                    
                    SUBSTITUTION & CALCULATION:
                    v = $u + ($a * $t)
                    v = $u + ${a * t}
                    v = $v m/s
                    
                    FINAL ANSWER:
                    Final Velocity (v) = $v m/s
                """.trimIndent()

                CalculationResult(
                    "$v m/s",
                    stepStr,
                    "MOTION",
                    mapOf("u" to u, "a" to a, "t" to t, "v" to v, "mode" to "v")
                )
            }
            "DISPLACEMENT" -> { // Find s given u, a, t
                val disp = u * t + 0.5 * a * t.pow(2)
                val stepStr = """
                    Subject: Physics -> Chapter: Motion -> Operation: Solve Displacement
                    
                    GIVEN VARIABLES:
                    Initial Velocity (u) = $u m/s
                    Acceleration (a) = $a m/s²
                    Time Duration (t) = $t s
                    
                    EQUATION OF MOTION:
                    s = u*t + (1/2)*a*t²
                    
                    SUBSTITUTION & CALCULATION:
                    s = ($u * $t) + 0.5 * $a * ($t)²
                    s = ${u * t} + 0.5 * $a * ${t.pow(2)}
                    s = ${u * t} + ${0.5 * a * t.pow(2)}
                    s = $disp m
                    
                    FINAL ANSWER:
                    Displacement (s) = $disp m
                """.trimIndent()

                CalculationResult(
                    "$disp m",
                    stepStr,
                    "MOTION",
                    mapOf("u" to u, "a" to a, "t" to t, "s" to disp, "mode" to "s")
                )
            }
            "VELOCITY_SQUARED" -> { // Find v given u, a, s
                val v2 = u.pow(2) + 2 * a * s
                val v = if (v2 >= 0) sqrt(v2) else Double.NaN

                val stepStr = """
                    Subject: Physics -> Chapter: Motion -> Operation: Solve Velocity through Displacement
                    
                    GIVEN VARIABLES:
                    Initial Velocity (u) = $u m/s
                    Acceleration (a) = $a m/s²
                    Displacement (s) = $s m
                    
                    EQUATION OF MOTION:
                    v² = u² + 2*a*s
                    
                    SUBSTITUTION & CALCULATION:
                    v² = ($u)² + 2 * $a * $s
                    v² = ${u.pow(2)} + ${2 * a * s}
                    v² = $v2
                    v = √$v2 = $v m/s
                    
                    FINAL ANSWER:
                    Final Velocity (v) = $v m/s
                """.trimIndent()

                CalculationResult(
                    "$v m/s",
                    stepStr,
                    "MOTION",
                    mapOf("u" to u, "a" to a, "s" to s, "v" to v, "mode" to "v2")
                )
            }
            else -> CalculationResult("Select Operation", "No operation applied.", "NONE")
        }
    }

    // ==========================================
    // PHYSICS: CAPACITANCE
    // ==========================================
    private val EPSILON_0 = 8.854e-12 // Permittivity of free space

    private fun solveCapacitance(operation: String, inputs: Map<String, String>): CalculationResult {
        val area = inputs["area"]?.toDoubleOrNull() ?: 0.01 // in m² (default 100 cm²)
        val dist = inputs["dist"]?.toDoubleOrNull() ?: 1e-3 // in m (default 1 mm)
        val k = inputs["k"]?.toDoubleOrNull() ?: 1.0 // relative permittivity (default air = 1)
        val voltage = inputs["volts"]?.toDoubleOrNull() ?: 10.0 // Applied voltage (V)

        if (dist == 0.0) {
            return CalculationResult("Undefined (Distance cannot be zero)", "Distance between parallel plates cannot be zero.", "NONE")
        }

        val cap = (k * EPSILON_0 * area) / dist // capacitance in farads

        return when (operation.uppercase()) {
            "CAPACITANCE" -> {
                val pfc = cap * 1e12 // in picofarad

                val stepStr = """
                    Subject: Physics -> Chapter: Capacitance -> Operation: Parallel Plate Capacitance
                    
                    GIVEN VARIABLES:
                    Plate Area (A) = $area m²
                    Plate Distance (d) = $dist m
                    Dielectric Constant (K or ε_r) = $k
                    Permittivity of Free Space (ε_0) = 8.854 x 10⁻¹² F/m
                    
                    FORMULA FOR PARALLEL PLATE CAPACITOR:
                    C = (K * ε_0 * A) / d
                    
                    SUBSTITUTION & CALCULATION:
                    C = ($k * 8.854x10⁻¹² * $area) / $dist
                    C = ${(k * EPSILON_0 * area)} / $dist
                    C = $cap F
                    C = $pfc pF
                    
                    FINAL ANSWER:
                    Capacitance (C) = $cap Farads ($pfc pF)
                """.trimIndent()

                CalculationResult(
                    "${cap.toSciString()} F (${pfc.round(2)} pF)",
                    stepStr,
                    "CAPACITANCE",
                    mapOf("area" to area, "dist" to dist, "k" to k, "volts" to voltage, "cap" to cap)
                )
            }
            "ENERGY_STORED" -> {
                val energy = 0.5 * cap * voltage.pow(2)

                val stepStr = """
                    Subject: Physics -> Chapter: Capacitance -> Operation: Energy Stored
                    
                    GIVEN VARIABLES:
                    Calculated Capacitance (C) = $cap F
                    Applied Voltage (V) = $voltage V
                    
                    FORMULA:
                    U = (1/2) * C * V²
                    
                    SUBSTITUTION & CALCULATION:
                    U = 0.5 * $cap * ($voltage)²
                    U = 0.5 * $cap * ${voltage.pow(2)}
                    U = $energy J
                    
                    FINAL ANSWER:
                    Energy Stored (U) = $energy Joules
                """.trimIndent()

                CalculationResult(
                    "${energy.toSciString()} J",
                    stepStr,
                    "CAPACITANCE",
                    mapOf("area" to area, "dist" to dist, "k" to k, "volts" to voltage, "cap" to cap, "energy" to energy)
                )
            }
            "ELECTRIC_FIELD" -> {
                val eField = voltage / dist

                val stepStr = """
                    Subject: Physics -> Chapter: Capacitance -> Operation: Electric Field Stiffness
                    
                    GIVEN VARIABLES:
                    Applied Voltage (V) = $voltage V
                    Plate Separation Distance (d) = $dist m
                    
                    FORMULA:
                    E = V / d
                    
                    SUBSTITUTION & CALCULATION:
                    E = $voltage / $dist
                    E = $eField V/m (or N/C)
                    
                    FINAL ANSWER:
                    Electric Field Intensity (E) = $eField V/m
                """.trimIndent()

                CalculationResult(
                    "$eField V/m",
                    stepStr,
                    "CAPACITANCE",
                    mapOf("area" to area, "dist" to dist, "k" to k, "volts" to voltage, "cap" to cap)
                )
            }
            else -> CalculationResult("Select Operation", "No operation applied.", "NONE")
        }
    }

    // ==========================================
    // PHYSICS: CURRENT ELECTRICITY
    // ==========================================
    private fun solveElectricity(operation: String, inputs: Map<String, String>): CalculationResult {
        return when (operation.uppercase()) {
            "OHM_LAW" -> {
                // Takes two fields and solves the third: V, I, R
                val v = inputs["v"]?.toDoubleOrNull()
                val i = inputs["i"]?.toDoubleOrNull()
                val r = inputs["r"]?.toDoubleOrNull()

                val ans: String
                val stepStr: String

                if (v != null && i != null) {
                    val computedR = v / i
                    ans = "Resistance (R) = $computedR Ω"
                    stepStr = """
                        Subject: Physics -> Chapter: Current Electricity -> Operation: Solve Ohm's Law
                        GIVEN: Voltage (V) = $v Volts, Current (I) = $i Amps
                        FORMULA: R = V / I
                        CALCULATION: R = $v / $i = $computedR Ω
                        FINAL ANSWER: R = $computedR Ω
                    """.trimIndent()
                } else if (v != null && r != null) {
                    val computedI = v / r
                    ans = "Current (I) = $computedI A"
                    stepStr = """
                        Subject: Physics -> Chapter: Current Electricity -> Operation: Solve Ohm's Law
                        GIVEN: Voltage (V) = $v Volts, Resistance (R) = $r Ohms
                        FORMULA: I = V / R
                        CALCULATION: I = $v / $r = $computedI A
                        FINAL ANSWER: I = $computedI A
                    """.trimIndent()
                } else if (i != null && r != null) {
                    val computedV = i * r
                    ans = "Voltage (V) = $computedV V"
                    stepStr = """
                        Subject: Physics -> Chapter: Current Electricity -> Operation: Solve Ohm's Law
                        GIVEN: Current (I) = $i Amps, Resistance (R) = $r Ohms
                        FORMULA: V = I * R
                        CALCULATION: V = $i * $r = $computedV V
                        FINAL ANSWER: V = $computedV V
                    """.trimIndent()
                } else {
                    ans = "Input any two variables (V, I, or R)"
                    stepStr = "Please fill at least two parameters representing Ohm's variables V, I, R."
                }

                CalculationResult(
                    ans,
                    stepStr,
                    "OHM_LAW",
                    mapOf("v" to (v ?: 0.0), "i" to (i ?: 0.0), "r" to (r ?: 0.0))
                )
            }
            "RESISTIVITY" -> {
                // R = rho * L / A
                val rho = inputs["rho"]?.toDoubleOrNull() ?: 1.68e-8 // resistivity of copper
                val length = inputs["length"]?.toDoubleOrNull() ?: 10.0 // 10 meters
                val area = inputs["area"]?.toDoubleOrNull() ?: 1e-6 // cross-sectional area

                val res = (rho * length) / area

                val stepStr = """
                    Subject: Physics -> Chapter: Current Electricity -> Operation: Resistivity Formula
                    
                    GIVEN VARIABLES:
                    Material Resistivity (ρ) = $rho Ω·m
                    Wire Length (L) = $length m
                    Cross-Sectional Area (A) = $area m²
                    
                    FORMULA:
                    R = ρ * (L / A)
                    
                    SUBSTITUTION & CALCULATION:
                    R = $rho * ($length / $area)
                    R = $rho * ${length / area}
                    R = $res Ω
                    
                    FINAL ANSWER:
                    Resistance (R) = $res Ω
                """.trimIndent()

                CalculationResult(
                    "$res Ω",
                    stepStr,
                    "RESISTIVITY",
                    mapOf("rho" to rho, "length" to length, "area" to area, "res" to res)
                )
            }
            else -> CalculationResult("Select Operation", "No operation applied.", "NONE")
        }
    }

    // ==========================================
    // CHEMISTRY: MOLE CONCEPT
    // ==========================================
    private fun solveMoles(operation: String, inputs: Map<String, String>): CalculationResult {
        val compound = inputs["compound"] ?: "H2O"
        val mass = inputs["mass"]?.toDoubleOrNull() ?: 18.0
        val inputMoles = inputs["moles"]?.toDoubleOrNull()

        // Get Molecular Weight of compound
        val molarMass = when (compound.trim().uppercase()) {
            "H2O" -> 18.015
            "CO2" -> 44.009
            "NACL" -> 58.44
            "C6H12O6" -> 180.156
            "O2" -> 31.998
            else -> 18.015
        }

        return when (operation.uppercase()) {
            "MASS_TO_MOLES" -> {
                val moles = mass / molarMass
                val molVolume = moles * 22.4 // L at STP
                val particles = moles * 6.022e23 // Avogadro's number

                val stepStr = """
                    Subject: Chemistry -> Chapter: Mole Concept -> Operation: Mass to Moles
                    
                    GIVEN VALUES:
                    Selected Chemical Compound: $compound
                    Molar Mass (M) of $compound = $molarMass g/mol
                    Input Mass of Substance (m) = $mass g
                    
                    FORMULAS USED:
                    1. Moles (n) = Mass (m) / Molar Mass (M)
                    2. Volume of Gas @ STP = n * 22.4 Liters (if ideal gas)
                    3. Particle Count (N) = n * N_A (where N_A = 6.022 x 10²³ particles/mol)
                    
                    CALCULATION:
                    n = $mass / $molarMass = $moles mol
                    STP Gas Volume = $moles * 22.4 = $molVolume Liters
                    Particles Count = $moles * 6.022x10²³ = $particles
                    
                    FINAL ANSWER:
                    Moles = $moles mol
                    STP Ideal Gas Volume = $molVolume L
                    Particles = ${particles.toSciString()} atoms/molecules
                """.trimIndent()

                CalculationResult(
                    "${moles.round(4)} mol",
                    stepStr,
                    "MOLES",
                    mapOf("compound" to compound, "mass" to mass, "moles" to moles, "molarMass" to molarMass)
                )
            }
            "MOLES_TO_MASS" -> {
                val activeMoles = inputMoles ?: 1.0
                val activeMass = activeMoles * molarMass
                val molVolume = activeMoles * 22.4
                val particles = activeMoles * 6.022e23

                val stepStr = """
                    Subject: Chemistry -> Chapter: Mole Concept -> Operation: Moles to Mass
                    
                    GIVEN VALUES:
                    Selected Compound: $compound
                    Molar Mass of $compound = $molarMass g/mol
                    Moles of Substance (n) = $activeMoles mol
                    
                    FORMULAS:
                    1. Mass (m) = Moles (n) * Molar Mass (M)
                    2. STP Volume = n * 22.4 L
                    3. Particle Count = n * 6.022 x 10²³
                    
                    CALCULATION:
                    m = $activeMoles * $molarMass = $activeMass g
                    Volume at STP = $activeMoles * 22.4 = $molVolume L
                    Particles Count = $activeMoles * 6.022x10²³ = $particles
                    
                    FINAL ANSWER:
                    Mass = $activeMass grams
                    STP Volume = $molVolume L
                    Particles = ${particles.toSciString()}
                """.trimIndent()

                CalculationResult(
                    "${activeMass.round(3)} g",
                    stepStr,
                    "MOLES",
                    mapOf("compound" to compound, "mass" to activeMass, "moles" to activeMoles, "molarMass" to molarMass)
                )
            }
            else -> CalculationResult("Select Operation", "No operation applied.", "NONE")
        }
    }

    // ==========================================
    // CHEMISTRY: ORGANIC CHEMISTRY (Carbon chains)
    // ==========================================
    private fun solveOrganic(operation: String, inputs: Map<String, String>): CalculationResult {
        val carbons = inputs["carbons"]?.toIntOrNull() ?: 2
        val family = inputs["family"] ?: "Alkanes"

        // Derive structural name and formula
        // Alkanes: C_n H_{2n+2}
        // Alkenes: C_n H_{2n}
        // Alkynes: C_n H_{2n-2}
        // Alcohols: C_n H_{2n+1}OH
        // Carboxylic Acids: C_{n-1}H_{2n-1}COOH (Total Carbons = n)

        val prefix = when (carbons) {
            1 -> "Meth"
            2 -> "Eth"
            3 -> "Prop"
            4 -> "But"
            5 -> "Pent"
            6 -> "Hex"
            else -> "Eth"
        }

        val iupacName: String
        val molecularFormula: String
        val detailDesc: String

        when (family.uppercase()) {
            "ALKANES" -> {
                iupacName = prefix.lowercase() + "ane"
                molecularFormula = "C${carbons}H${carbons * 2 + 2}"
                detailDesc = "A saturated hydrocarbon with single covalent bonds between carbon atoms."
            }
            "ALKENES" -> {
                if (carbons < 2) {
                    return CalculationResult(
                        "Invalid Carbons",
                        "Alkenes require at least 2 Carbon atoms to form a double bond (C=C). Please set Carbon atoms to 2 or more.",
                        "ORGANIC_CHAIN"
                    )
                }
                iupacName = prefix.lowercase() + "ene"
                molecularFormula = "C${carbons}H${carbons * 2}"
                detailDesc = "An unsaturated hydrocarbon containing a reactive double bond (C=C)."
            }
            "ALKYNES" -> {
                if (carbons < 2) {
                    return CalculationResult(
                        "Invalid Carbons",
                        "Alkynes require at least 2 Carbon atoms to form a triple bond (C≡C). Please set Carbon atoms to 2 or more.",
                        "ORGANIC_CHAIN"
                    )
                }
                iupacName = prefix.lowercase() + "yne"
                molecularFormula = "C${carbons}H${carbons * 2 - 2}"
                detailDesc = "An unsaturated hydrocarbon containing a reactive triple covalent bond (C≡C)."
            }
            "ALCOHOLS" -> {
                iupacName = if (carbons == 1) "methanol" else prefix.lowercase() + "anol"
                molecularFormula = "C${carbons}H${carbons * 2 + 1}OH"
                detailDesc = "An organic molecule containing a polar, hydro-active functional Hydroxyl (-OH) group."
            }
            "CARBOXYLIC ACIDS" -> {
                iupacName = if (carbons == 1) "methanoic acid" else prefix.lowercase() + "anoic acid"
                // e.g. Ethanoic acid: CH3COOH, C2 H4 O2
                val hCount = carbons * 2
                molecularFormula = "C${carbons}H${hCount}O2 (or C${carbons - 1}H${carbons * 2 - 1}COOH)"
                detailDesc = "An organic acid containing the terminal polar Carboxyl (-COOH) functional group."
            }
            else -> {
                iupacName = prefix.lowercase() + "ane"
                molecularFormula = "C${carbons}H${carbons * 2 + 2}"
                detailDesc = "Saturated chain."
            }
        }

        return when (operation.uppercase()) {
            "IUPAC_NAME" -> {
                val stepStr = """
                    Subject: Chemistry -> Chapter: Organic Chemistry -> Operation: IUPAC Nomenclature
                    
                    GIVEN CRITERIA:
                    Carbons Count: $carbons
                    Organic Family subclass: $family
                    Prefix based on chain size: $prefix
                    
                    NOMENCLATURE DEDUCTION:
                    1. Prefix derived: $carbons carbons = $prefix
                    2. Suffix derived: $family -> mapped structural name component.
                    
                    DETERMINED CHARACTERISTICS:
                    IUPAC Name: $iupacName
                    Molecular Formula: $molecularFormula
                    General Classification: $detailDesc
                    
                    FINAL ANSWER:
                    IUPAC Name: $iupacName
                    Formula: $molecularFormula
                """.trimIndent()

                CalculationResult(
                    "Name: $iupacName\nFormula: $molecularFormula",
                    stepStr,
                    "ORGANIC_CHAIN",
                    mapOf("carbons" to carbons, "family" to family, "name" to iupacName, "formula" to molecularFormula)
                )
            }
            "COMBUSTION" -> {
                // Combustion reaction of hydrocarbon or organic derivative
                // C_x H_y + O2 -> CO2 + H2O
                // Let's draft a balanced combustion equation for Alkanes, Alkenes, Alkynes
                val x = carbons
                val y = when (family.uppercase()) {
                    "ALKANES" -> carbons * 2 + 2
                    "ALKENES" -> carbons * 2
                    "ALKYNES" -> carbons * 2 - 2
                    "ALCOHOLS" -> carbons * 2 + 2 // C_n H_2n+1 OH has 2n+2 hydrogens
                    "CARBOXYLIC ACIDS" -> carbons * 2 // C_n H_2n O2
                    else -> carbons * 2 + 2
                }

                // Balance standard: CxHy + (x + y/4) O2 -> x CO2 + (y/2) H2O
                // Organic with Oxygen like Alcohol: CxHyO + ... ->
                // Alcohol C_x H_{y+2} O:
                // Let's simplify and balance hydro-carbons CxHy precisely.
                // We'll write out the balanced equation text with nice integer ratios!
                val doubleO2Coeff = 2 * x + y / 2.0 // total oxygen atoms needed on RHS
                // If we multiply everything by 2 to prevent fractions:
                // 2 CxHy + (4x + y) O2 -> (2x) CO2 + (y) H2O
                // If (4x + y)/2 is an integer, we can divide by 2:
                // 1 CxHy + (x + y/4) O2 -> x CO2 + (y/2) H2O

                val fuelCoeff: Int
                val o2Coeff: Int
                val co2Coeff: Int
                val h2oCoeff: Int

                if (y % 4 == 0) {
                    fuelCoeff = 1
                    o2Coeff = x + y / 4
                    co2Coeff = x
                    h2oCoeff = y / 2
                } else {
                    fuelCoeff = 2
                    o2Coeff = 2 * x + y / 2
                    co2Coeff = 2 * x
                    h2oCoeff = y
                }

                val fuelFormula = if (family.uppercase() == "ALCOHOLS") "C${carbons}H${carbons * 2 + 1}OH" else molecularFormula

                // Balance equation adjust for Oxygen present in fuel
                // For Alcohols: C_x H_{2x+2} O + (x + (2x+2)/4 - 1/2) O2 -> x CO2 + (x+1) H2O
                // let's print a clean unified equation for the balanced reaction.
                val reactionEq = if (family.uppercase() == "ALCOHOLS") {
                    val oNeeded = 2 * x + (x + 1) - 1 // double bonds worth of oxygen minus 1 in alcohol
                    // oNeeded / 2 is coeff of O2
                    if (oNeeded % 2 == 0) {
                        "1 $fuelFormula + ${oNeeded / 2} O₂ ➔ $x CO₂ + ${x + 1} H₂O"
                    } else {
                        "2 $fuelFormula + $oNeeded O₂ ➔ ${2 * x} CO₂ + ${2 * (x + 1)} H₂O"
                    }
                } else if (family.uppercase() == "CARBOXYLIC ACIDS") {
                    // C_x H_{2x} O2 + O2 -> x CO2 + x H2O
                    // O atoms on RHS = 2x + x = 3x. Minus 2 in acid = 3x - 2.
                    val oNeeded = 3 * x - 2
                    if (oNeeded % 2 == 0) {
                        "1 CH₃COOH + ${oNeeded / 2} O₂ ➔ $x CO₂ + $x H₂O" // generalize name
                    } else {
                        "2 $fuelFormula + $oNeeded O₂ ➔ ${2 * x} CO₂ + ${2 * x} H₂O"
                    }
                } else {
                    "$fuelCoeff $molecularFormula + $o2Coeff O₂ ➔ $co2Coeff CO₂ + $h2oCoeff H₂O"
                }

                val stepStr = """
                    Subject: Chemistry -> Chapter: Organic Chemistry -> Operation: Balancing Combustion Reaction
                    
                    GIVEN STRUCTURAL DETAILS:
                    Fuel Compound: $iupacName ($molecularFormula)
                    Type of Reaction: Complete Combustion (Oxidation reaction)
                    Generates products: Carbon Dioxide (CO₂) and Water (H₂O) as vapor.
                    
                    BALANCING STEPS:
                    1. Setup skeletal equation:
                       Reactant(s) -> Product(s)
                       $molecularFormula + O₂ ➔ CO₂ + H₂O
                    2. Balance Carbons on both sides.
                    3. Balance Hydrogen atoms on both sides.
                    4. Aggregate oxygen counts on right-hand side and adjust reactant O₂ coefficients accordingly.
                    
                    BALANCED EQUATION DETERMINED:
                    $reactionEq
                    
                    FINAL ANSWER:
                    Balanced Combustion Equation:
                    $reactionEq (+ heat released)
                """.trimIndent()

                CalculationResult(
                    reactionEq,
                    stepStr,
                    "ORGANIC_CHAIN",
                    mapOf("carbons" to carbons, "family" to family, "name" to iupacName, "combustion" to reactionEq)
                )
            }
            else -> CalculationResult("Select Operation", "No operation applied.", "NONE")
        }
    }

    // ==========================================
    // CHEMISTRY: THERMODYNAMICS
    // ==========================================
    private fun solveThermodynamics(operation: String, inputs: Map<String, String>): CalculationResult {
        val deltaH = inputs["dh"]?.toDoubleOrNull() ?: -120.0 // kJ/mol (negative is exothermic)
        val deltaS = inputs["ds"]?.toDoubleOrNull() ?: -200.0 // J/mol.K
        val t = inputs["temp"]?.toDoubleOrNull() ?: 298.15 // K (default 25 deg C)

        val dsKj = deltaS / 1000.0 // converted to kJ/mol.K
        val deltaG = deltaH - t * dsKj

        return when (operation.uppercase()) {
            "GIBBS_FREE_ENERGY" -> {
                val spontaneity = when {
                    deltaG < 0 -> "Spontaneous (Favors forward reaction energetically)"
                    deltaG > 0 -> "Non-spontaneous (Requires thermodynamic continuous input)"
                    else -> "Thermodynamic Equilibrium"
                }

                val stepStr = """
                    Subject: Chemistry -> Chapter: Thermodynamics -> Operation: Gibbs Free Energy
                    
                    GIVEN VARIABLES:
                    Enthalpy Change (ΔH) = $deltaH kJ/mol
                    Entropy Change (ΔS) = $deltaS J/(mol·K)
                    Absolute Temperature (T) = $t K (${t - 273.15} °C)
                    
                    CONVERSION:
                    Convert ΔS to kJ/(mol·K) to match ΔH's units:
                    ΔS = $deltaS / 1000 = $dsKj kJ/(mol·K)
                    
                    FORMULA FOR GIBBS ACTIVE ENERGY:
                    ΔG = ΔH - T * ΔS
                    
                    SUBSTITUTION & CALCULATIONS:
                    ΔG = $deltaH - ($t * $dsKj)
                    ΔG = $deltaH - (${t * dsKj})
                    ΔG = $deltaG kJ/mol
                    
                    STATUS FORWARD EVALUATED:
                    Spontaneity state: $spontaneity
                    
                    FINAL ANSWER:
                    ΔG = $deltaG kJ/mol ($spontaneity)
                """.trimIndent()

                CalculationResult(
                    "${deltaG.round(3)} kJ/mol",
                    stepStr,
                    "THERMO",
                    mapOf("dh" to deltaH, "ds" to deltaS, "temp" to t, "dg" to deltaG)
                )
            }
            "SPONTANEITY" -> {
                // Find crossover equilibrium temperature T_eq = dH / dS
                val crossoverDesc: String
                if (deltaS != 0.0) {
                    val tCross = deltaH / dsKj // crossover temperature in K
                    crossoverDesc = if (tCross > 0) {
                        "Equilibrium crossover occurs at: $tCross K (${tCross - 273.15} °C)"
                    } else {
                        "No physical positive thermodynamic crossover temperature exists. Spontaneity status is absolute."
                    }
                } else {
                    crossoverDesc = "Entropy change is zero. No thermodynamic temperature effect."
                }

                // Spontaneity cases
                // dH < 0, dS > 0 -> Spontaneous at all temperatures
                // dH > 0, dS < 0 -> Non-spontaneous at all temperatures
                // dH < 0, dS < 0 -> Spontaneous at low temperatures (below crossover)
                // dH > 0, dS > 0 -> Spontaneous at high temperatures (above crossover)
                val globalBehavior = when {
                    deltaH < 0 && deltaS > 0 -> "Spontaneous at ALL temperatures (Enthalpy driven & Entropy driven)."
                    deltaH > 0 && deltaS < 0 -> "Non-spontaneous at ALL temperatures (Enthalpically & Entropically opposed)."
                    deltaH < 0 && deltaS < 0 -> "Spontaneous at LOW temperatures (below crossover point), since Enthalpy is favorable but Entropy is unfavorable."
                    deltaH > 0 && deltaS > 0 -> "Spontaneous at HIGH temperatures (above crossover point), because Entropy is favorable but Enthalpy is unfavorable."
                    else -> "Depends on parameters."
                }

                val stepStr = """
                    Subject: Chemistry -> Chapter: Thermodynamics -> Operation: Spontaneity Analysis
                    
                    Enthalpy (ΔH) = $deltaH kJ/mol (${if (deltaH < 0) "Exothermic / Favorable" else "Endothermic / Unfavorable"})
                    Entropy (ΔS) = $deltaS J/mol.K (${if (deltaS > 0) "Increase in disorder / Favorable" else "Decrease in disorder / Unfavorable"})
                    
                    THERMODYNAMIC RULESET:
                    ΔG = ΔH - T*ΔS
                    Reaction is spontaneous when ΔG < 0.
                    
                    1. THERMAL SYSTEM CROSSOVER BEHAVIOR:
                       $globalBehavior
                    
                    2. CROSSOVER TEMPERATURE CRITERIA:
                       T_eq = ΔH / ΔS (equal spontaneity boundaries)
                       $crossoverDesc
                       
                    FINAL ANSWER:
                    Crossover Temperature: ${if (deltaS != 0.0) "${(deltaH / dsKj).round(1)} K" else "N/A"}
                    Rule: $globalBehavior
                """.trimIndent()

                CalculationResult(
                    globalBehavior,
                    stepStr,
                    "THERMO",
                    mapOf("dh" to deltaH, "ds" to deltaS, "temp" to t, "dg" to deltaG)
                )
            }
            else -> CalculationResult("Select Operation", "No operation applied.", "NONE")
        }
    }

    // ==========================================
    // UTILITIES
    // ==========================================
    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (this * multiplier).roundToInt() / multiplier
    }

    private fun Double.toSciString(): String {
        if (this == 0.0) return "0"
        val absVal = kotlin.math.abs(this)
        if (absVal >= 1e-3 && absVal < 1e4) {
            return this.round(5).toString()
        }
        val exponent = kotlin.math.floor(kotlin.math.log10(absVal)).toInt()
        val base = this / 10.0.pow(exponent)
        return "${base.round(3)}e$exponent"
    }
}
