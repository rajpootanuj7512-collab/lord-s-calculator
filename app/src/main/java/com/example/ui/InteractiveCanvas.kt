package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun InteractiveCanvas(
    chapter: String,
    operation: String,
    params: Map<String, Any>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(CyberSlate)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            try {
                val width = size.width
                val height = size.height

                if (width > 0 && height > 0) {
                    // Main radar circle backing
                    drawCircle(
                        color = NeonCyan.copy(alpha = 0.05f),
                        radius = minOf(width, height) * 0.45f,
                        center = Offset(width / 2f, height / 2f)
                    )

                    // Sub-chapter drawings
                    when (chapter.uppercase()) {
                        "MATRIX" -> drawMatrixSpace(width, height, params)
                        "ALGEBRA" -> drawAlgebraSpace(width, height, params)
                        "CALCULUS" -> drawCalculusSpace(width, height, params)
                        "MOTION" -> drawMotionSpace(width, height, params)
                        "CAPACITANCE" -> drawCapacitanceSpace(width, height, params)
                        "CURRENT ELECTRICITY" -> drawElectricitySpace(width, height, params)
                        "MOLE CONCEPT" -> drawMoleSpace(width, height, params)
                        "ORGANIC CHEMISTRY" -> drawOrganicSpace(width, height, params)
                        "THERMODYNAMICS" -> drawThermoSpace(width, height, params)
                        else -> drawGenericSpace(width, height)
                    }
                }
            } catch (e: Throwable) {
                android.util.Log.e("InteractiveCanvas", "Canvas drawing exception caught: ", e)
            }
        }
    }
}

// 1. Draw transformed Matrix unit box / linear transform on grid
private fun DrawScope.drawMatrixSpace(width: Float, height: Float, params: Map<String, Any>) {
    val cx = width / 2f
    val cy = height / 2f
    val scale = 50f // 50 pixels per unit

    // Pull Matrix A parameters
    val m11 = (params["a11"] as? Double ?: 1.0).toFloat()
    val m12 = (params["a12"] as? Double ?: 0.0).toFloat()
    val m21 = (params["a21"] as? Double ?: 0.0).toFloat()
    val m22 = (params["a22"] as? Double ?: 1.0).toFloat()

    // Draw coordinate axis
    drawLine(NeonCyan.copy(alpha = 0.2f), Offset(0f, cy), Offset(width, cy), strokeWidth = 1f)
    drawLine(NeonCyan.copy(alpha = 0.2f), Offset(cx, 0f), Offset(cx, height), strokeWidth = 1f)

    for (i in -5..5) {
        if (i != 0) {
            drawLine(NeonCyan.copy(alpha = 0.08f), Offset(0f, cy + i * scale), Offset(width, cy + i * scale), 1f)
            drawLine(NeonCyan.copy(alpha = 0.08f), Offset(cx + i * scale, 0f), Offset(cx + i * scale, height), 1f)
        }
    }

    // Coordinates of square corners: (0,0), (1,0), (1,1), (0,1)
    // Transform coordinates: x' = m11*x + m12*y,  y' = m21*x + m22*y
    val transform = { x: Float, y: Float ->
        val xPrime = m11 * x + m12 * y
        // In Android, Y axis goes downwards, so we negate Y logic for Cartesian graph coordinates
        val yPrime = m21 * x + m22 * y
        Offset(cx + xPrime * scale, cy - yPrime * scale)
    }

    val p00 = transform(0f, 0f)
    val p10 = transform(2f, 0f)
    val p11 = transform(2f, 2f)
    val p01 = transform(0f, 2f)

    // Draw unit box transformed vector shape
    val path = Path().apply {
        moveTo(p00.x, p00.y)
        lineTo(p10.x, p10.y)
        lineTo(p11.x, p11.y)
        lineTo(p01.x, p01.y)
        close()
    }

    drawPath(path, primaryGreenOrCyan(params).copy(alpha = 0.15f))
    drawPath(path, primaryGreenOrCyan(params), style = Stroke(width = 3.dp.toPx()))

    // Draw unit vectors
    val iHat = transform(1f, 0f)
    val jHat = transform(0f, 1f)

    drawLine(HyperPink, p00, iHat, strokeWidth = 5f) // Vector i
    drawLine(CosmicGold, p00, jHat, strokeWidth = 5f) // Vector j

    drawCircle(HyperPink, 8f, iHat)
    drawCircle(CosmicGold, 8f, jHat)
}

// 2. Draw Algebra quadratic parabola
private fun DrawScope.drawAlgebraSpace(width: Float, height: Float, params: Map<String, Any>) {
    val cx = width / 2f
    val cy = height / 2f
    val scaleX = 40f
    val scaleY = 20f

    val a = (params["a"] as? Double ?: 1.0).toFloat()
    val b = (params["b"] as? Double ?: -5.0).toFloat()
    val c = (params["c"] as? Double ?: 6.0).toFloat()

    // Draw axis
    drawLine(NeonCyan.copy(alpha = 0.2f), Offset(0f, cy), Offset(width, cy), 1f)
    drawLine(NeonCyan.copy(alpha = 0.2f), Offset(cx, 0f), Offset(cx, height), 1f)

    // Drawing parabola
    val parabolaPath = Path()
    var first = true

    // Compute range
    val startX = -10f
    val endX = 10f
    val steps = 100
    val stepSize = (endX - startX) / steps

    for (i in 0..steps) {
        val x = startX + i * stepSize
        val y = a * x.pow(2) + b * x + c
        val plotX = cx + x * scaleX
        val plotY = cy - y * scaleY // invert Y

        if (plotX in 0f..width && plotY in 0f..height) {
            if (first) {
                parabolaPath.moveTo(plotX, plotY)
                first = false
            } else {
                parabolaPath.lineTo(plotX, plotY)
            }
        }
    }

    drawPath(parabolaPath, NeonCyan, style = Stroke(width = 4f))

    // Draw Vertex point if available
    val vx = -b / (2 * a)
    val vy = a * vx.pow(2) + b * vx + c
    val vplotX = cx + vx * scaleX
    val vplotY = cy - vy * scaleY

    if (vplotX in 0f..width && vplotY in 0f..height) {
        drawCircle(CosmicGold, 12f, Offset(vplotX, vplotY))
        drawCircle(DeepBlack, 5f, Offset(vplotX, vplotY))
    }

    // Draw Roots if D >= 0
    val dVal = b * b - 4 * a * c
    if (dVal >= 0) {
        val root1 = (-b + kotlin.math.sqrt(dVal.toDouble())).toFloat() / (2 * a)
        val root2 = (-b - kotlin.math.sqrt(dVal.toDouble())).toFloat() / (2 * a)

        val r1Plot = cx + root1 * scaleX
        val r2Plot = cx + root2 * scaleX

        drawCircle(HyperPink, 10f, Offset(r1Plot, cy))
        drawCircle(HyperPink, 10f, Offset(r2Plot, cy))
    }
}

// 3. Draw Calculus Polynomial Space with Derivative/Integral
private fun DrawScope.drawCalculusSpace(width: Float, height: Float, params: Map<String, Any>) {
    val cx = width / 2f
    val cy = height / 2f
    val scaleX = 45f
    val scaleY = 15f

    val ca = (params["ca"] as? Double ?: 1.0).toFloat()
    val cb = (params["cb"] as? Double ?: -3.0).toFloat()
    val cc = (params["cc"] as? Double ?: 2.0).toFloat()
    val cd = (params["cd"] as? Double ?: 1.0).toFloat()

    val f = { x: Float -> ca * x.pow(3) + cb * x.pow(2) + cc * x + cd }

    // Plot axis
    drawLine(NeonCyan.copy(alpha = 0.2f), Offset(0f, cy), Offset(width, cy), 1f)
    drawLine(NeonCyan.copy(alpha = 0.2f), Offset(cx, 0f), Offset(cx, height), 1f)

    // Shaded Definite Integral bounds if matching
    val isIntegral = params["integral"] as? Boolean ?: false
    if (isIntegral) {
        val limA = (params["limA"] as? Double ?: 0.0).toFloat()
        val limB = (params["limB"] as? Double ?: 3.0).toFloat()

        val shadedPath = Path()
        shadedPath.moveTo(cx + limA * scaleX, cy)

        val steps = 40
        val stepSize = (limB - limA) / steps
        for (i in 0..steps) {
            val x = limA + i * stepSize
            val y = f(x)
            shadedPath.lineTo(cx + x * scaleX, cy - y * scaleY)
        }
        shadedPath.lineTo(cx + limB * scaleX, cy)
        shadedPath.close()

        drawPath(shadedPath, HyperPink.copy(alpha = 0.25f))
        drawLine(HyperPink, Offset(cx + limA * scaleX, cy), Offset(cx + limA * scaleX, cy - f(limA) * scaleY), 3f)
        drawLine(HyperPink, Offset(cx + limB * scaleX, cy), Offset(cx + limB * scaleX, cy - f(limB) * scaleY), 3f)
    }

    // Draw polynomial curve
    val curvePath = Path()
    var start = true
    for (px in -100..100) {
        val x = px / 20f
        val y = f(x)
        val drawX = cx + x * scaleX
        val drawY = cy - y * scaleY

        if (drawX in 0f..width && drawY in 0f..height) {
            if (start) {
                curvePath.moveTo(drawX, drawY)
                start = false
            } else {
                curvePath.lineTo(drawX, drawY)
            }
        }
    }
    drawPath(curvePath, NeonCyan, style = Stroke(width = 4f))

    // Tangent derivative if matching
    val isDeriv = params["deriv"] as? Boolean ?: false
    if (isDeriv) {
        val x0 = (params["x0"] as? Double ?: 2.0).toFloat()
        val y0 = f(x0)

        // Slope m = f'(x) = 3ax^2 + 2bx + c
        val m = 3 * ca * x0.pow(2) + 2 * cb * x0 + cc

        // Draw tangent line: y - y0 = m * (x - x0) -> y = m * (x - x0) + y0
        val tx1 = x0 - 2f
        val ty1 = m * (tx1 - x0) + y0
        val tx2 = x0 + 2f
        val ty2 = m * (tx2 - x0) + y0

        drawLine(
            color = CosmicGold,
            start = Offset(cx + tx1 * scaleX, cy - ty1 * scaleY),
            end = Offset(cx + tx2 * scaleX, cy - ty2 * scaleY),
            strokeWidth = 3f
        )

        drawCircle(CosmicGold, 10f, Offset(cx + x0 * scaleX, cy - y0 * scaleY))
        drawCircle(DeepBlack, 4f, Offset(cx + x0 * scaleX, cy - y0 * scaleY))
    }
}

// 4. Draw Motion Kinematics trajectory
private fun DrawScope.drawMotionSpace(width: Float, height: Float, params: Map<String, Any>) {
    val cx = 100f
    val cy = height - 100f

    val u = (params["u"] as? Double ?: 0.0).toFloat()
    val a = (params["a"] as? Double ?: 9.8).toFloat()
    val totalTime = (params["t"] as? Double ?: 5.0).toFloat()

    // Draw simple runway
    drawLine(GhostGray, Offset(cx, cy), Offset(width - 50f, cy), 4f)

    // Plot trajectory displacement points
    val scaleT = (width - cx - 100f) / totalTime
    val maxDisp = u * totalTime + 0.5f * a * totalTime.pow(2)
    val scaleY = if (maxDisp > 0) (cy - 100f) / maxDisp else 1f

    val path = Path()
    path.moveTo(cx, cy)

    val steps = 50
    for (i in 0..steps) {
        val t = (i.toFloat() / steps) * totalTime
        val s = u * t + 0.5f * a * t.pow(2)
        path.lineTo(cx + t * scaleT, cy - s * scaleY)
    }

    drawPath(path, NeonCyan.copy(alpha = 0.15f))
    drawPath(path, NeonCyan, style = Stroke(width = 3f))

    // Spaceship / particle glowing indicator at final point
    val fx = cx + totalTime * scaleT
    val fy = cy - maxDisp * scaleY
    drawCircle(HyperPink, 14f, Offset(fx, fy))
    drawCircle(TextWhite, 6f, Offset(fx, fy))

    // Motion acceleration vector line
    drawLine(CosmicGold, Offset(cx, cy - 20f), Offset(cx + 80f, cy - 20f), strokeWidth = 5f)
    drawCircle(CosmicGold, 6f, Offset(cx + 80f, cy - 20f))
}

// 5. Draw Capacitance plate schematic
private fun DrawScope.drawCapacitanceSpace(width: Float, height: Float, params: Map<String, Any>) {
    val cx = width / 2f
    val cy = height / 2f

    val k = (params["k"] as? Double ?: 1.0).toFloat()
    val area = (params["area"] as? Double ?: 0.01).toFloat()
    val volts = (params["volts"] as? Double ?: 10.0).toFloat()

    val plateW = 300f
    val plateH = 20f
    val separation = 80f

    // Draw dielectric block first (backing)
    if (k > 1f) {
        val colorAlpha = minOf(0.7f, 0.1f + (k * 0.05f))
        drawRect(
            color = CosmicGold.copy(alpha = colorAlpha),
            topLeft = Offset(cx - plateW / 2f, cy - separation / 2f),
            size = Size(plateW, separation)
        )
    }

    // Top Plate (+)
    drawRect(
        color = NeonCyan,
        topLeft = Offset(cx - plateW / 2f, cy - separation / 2f - plateH),
        size = Size(plateW, plateH)
    )

    // Bottom Plate (-)
    drawRect(
        color = HyperPink,
        topLeft = Offset(cx - plateW / 2f, cy + separation / 2f),
        size = Size(plateW, plateH)
    )

    // Draw field arrows in between
    val arrows = 8
    val spacing = plateW / (arrows + 1)
    for (i in 1..arrows) {
        val arrowX = cx - plateW / 2f + i * spacing
        val startY = cy - separation / 2f
        val endY = cy + separation / 2f

        drawLine(color = NeonGreen.copy(alpha = 0.5f), start = Offset(arrowX, startY), end = Offset(arrowX, endY), strokeWidth = 2f)

        // Arrow tip pointing down (positive to negative field direction)
        drawLine(color = NeonGreen.copy(alpha = 0.5f), start = Offset(arrowX - 6f, endY - 12f), end = Offset(arrowX, endY), strokeWidth = 2f)
        drawLine(color = NeonGreen.copy(alpha = 0.5f), start = Offset(arrowX + 6f, endY - 12f), end = Offset(arrowX, endY), strokeWidth = 2f)
    }

    // Applied power terminal lines
    drawLine(TextWhite.copy(alpha = 0.4f), Offset(cx - 100f, cy - separation / 2f - plateH), Offset(cx - 100f, cy - separation / 2f - 60f), 3f)
    drawLine(TextWhite.copy(alpha = 0.4f), Offset(cx + 100f, cy + separation / 2f + plateH), Offset(cx + 100f, cy + separation / 2f + 60f), 3f)
}

// 6. Current Electricity diagram space
private fun DrawScope.drawElectricitySpace(width: Float, height: Float, params: Map<String, Any>) {
    val cx = width / 2f
    val cy = height / 2f

    // Draw a cylindrical wire resister with flowing electrons
    val wireW = 320f
    val wireH = 90f

    // Wire outer container
    drawRoundRect(
        color = NeonCyan.copy(alpha = 0.1f),
        topLeft = Offset(cx - wireW / 2f, cy - wireH / 2f),
        size = Size(wireW, wireH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(15f, 15f)
    )

    drawRoundRect(
        color = NeonCyan.copy(alpha = 0.4f),
        topLeft = Offset(cx - wireW / 2f, cy - wireH / 2f),
        size = Size(wireW, wireH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(15f, 15f),
        style = Stroke(width = 3f)
    )

    // Flowing electrons
    val electrons = listOf(
        Offset(cx - 100f, cy - 20f), Offset(cx - 50f, cy + 15f),
        Offset(cx, cy - 10f), Offset(cx + 60f, cy + 20f),
        Offset(cx + 110f, cy - 15f), Offset(cx - 130f, cy + 5f)
    )

    for (e in electrons) {
        drawCircle(NeonGreen, 10f, e)
        drawCircle(TextWhite, 4f, e)

        // electron velocity vector (pointing left for conventional positive current flowing right)
        drawLine(NeonGreen, e, Offset(e.x - 25f, e.y), strokeWidth = 3f)
        drawLine(NeonGreen, Offset(e.x - 25f, e.y), Offset(e.x - 20f, e.y - 4f), strokeWidth = 2f)
        drawLine(NeonGreen, Offset(e.x - 25f, e.y), Offset(e.x - 20f, e.y + 4f), strokeWidth = 2f)
    }

    // Lead wires
    drawLine(TextWhite, Offset(cx - wireW / 2f, cy), Offset(cx - wireW / 2f - 40f, cy), 4f)
    drawLine(TextWhite, Offset(cx + wireW / 2f, cy), Offset(cx + wireW / 2f + 40f, cy), 4f)
}

// 7. Chemical Mole Particle box
private fun DrawScope.drawMoleSpace(width: Float, height: Float, params: Map<String, Any>) {
    val cx = width / 2f
    val cy = height / 2f
    val boxSize = 220f

    // Draw container box
    drawRect(
        color = DarkGlass.copy(alpha = 0.4f),
        topLeft = Offset(cx - boxSize / 2f, cy - boxSize / 2f),
        size = Size(boxSize, boxSize)
    )

    drawRect(
        color = NeonCyan.copy(alpha = 0.5f),
        topLeft = Offset(cx - boxSize / 2f, cy - boxSize / 2f),
        size = Size(boxSize, boxSize),
        style = Stroke(width = 2.dp.toPx())
    )

    // Draw a bunch of gas atoms bouncing inside based on molecules moles or compound
    val comp = params["compound"] as? String ?: "H2O"
    val moleCount = (params["moles"] as? Double ?: 1.0)
    val particles = minOf(15, (moleCount * 5 + 3).toInt())

    val randomOffsets = listOf(
        Offset(-80f, -60f), Offset(-30f, -80f), Offset(30f, -90f), Offset(80f, -70f),
        Offset(-90f, -10f), Offset(-40f, -20f), Offset(40f, -30f), Offset(70f, -10f),
        Offset(-70f, 40f), Offset(-20f, 60f), Offset(20f, 70f), Offset(80f, 50f),
        Offset(-40f, 90f), Offset(10f, -50f), Offset(-10f, 20f)
    )

    for (i in 0 until minOf(particles, randomOffsets.size)) {
        val o = randomOffsets[i]
        val atomCenter = Offset(cx + o.x, cy + o.y)

        // Draw compound specific atom balls
        if (comp == "H2O") {
            // Draw Oxygen (red) with two small hydrogens (white)
            drawCircle(Color(0xFFFF3333), 12f, atomCenter)
            drawCircle(TextWhite, 6f, Offset(atomCenter.x - 12f, atomCenter.y + 10f))
            drawCircle(TextWhite, 6f, Offset(atomCenter.x + 12f, atomCenter.y + 10f))
        } else if (comp == "CO2") {
            // Carbon (black) flanked by two Oxygens (red)
            drawCircle(Color(0xFF333333), 10f, atomCenter)
            drawCircle(Color(0xFFFF3333), 10f, Offset(atomCenter.x - 16f, atomCenter.y))
            drawCircle(Color(0xFFFF3333), 10f, Offset(atomCenter.x + 16f, atomCenter.y))
        } else {
            // Generic glowing ball
            drawCircle(NeonCyan, 11f, atomCenter)
            drawCircle(TextWhite, 5f, atomCenter)
        }
    }
}

// 8. Chemical Organic Compound chain skeleton diagram
private fun DrawScope.drawOrganicSpace(width: Float, height: Float, params: Map<String, Any>) {
    val cx = width / 2f
    val cy = height / 2f

    val carbons = params["carbons"] as? Int ?: 2
    val family = params["family"] as? String ?: "Alkanes"

    val spacingX = 60f
    val startX = cx - ((carbons - 1) * spacingX) / 2f

    // Plot carbon backbone chains: C-C-C
    val carbonPoints = mutableListOf<Offset>()
    for (i in 0 until carbons) {
        // zigzag Y coords
        val zig = if (i % 2 == 0) -25f else 25f
        val pt = Offset(startX + i * spacingX, cy + zig)
        carbonPoints.add(pt)
    }

    // Draw bounds between carbons
    for (i in 0 until carbons - 1) {
        val p1 = carbonPoints[i]
        val p2 = carbonPoints[i + 1]

        // Double bond if Alkenes and first carbon
        val isDoubleBond = family.uppercase() == "ALKENES" && i == 0
        val isTripleBond = family.uppercase() == "ALKYNES" && i == 0

        if (isDoubleBond) {
            drawLine(NeonCyan, Offset(p1.x, p1.y - 6f), Offset(p2.x, p2.y - 6f), 5f)
            drawLine(NeonCyan, Offset(p1.x, p1.y + 6f), Offset(p2.x, p2.y + 6f), 5f)
        } else if (isTripleBond) {
            drawLine(NeonCyan, Offset(p1.x, p1.y - 10f), Offset(p2.x, p2.y - 10f), 4f)
            drawLine(NeonCyan, Offset(p1.x, p1.y), Offset(p2.x, p2.y), 4f)
            drawLine(NeonCyan, Offset(p1.x, p1.y + 10f), Offset(p2.x, p2.y + 10f), 4f)
        } else {
            drawLine(NeonCyan, p1, p2, 5f)
        }
    }

    // Draw atoms labeling centers
    for (i in 0 until carbons) {
        val p = carbonPoints[i]
        drawCircle(DeepBlack, 18f, p)
        drawCircle(TextWhite, 12f, p, style = Stroke(width = 3f))

        // Draw letter C inside
        // (Simplified drawing, just green dot inside to represent Carbon atom)
        drawCircle(NeonGreen, 5f, p)
    }

    // If organic group is Alcohol (-OH), draw OH tag at terminal carbon
    if (family.uppercase() == "ALCOHOLS" && carbons > 0) {
        val lastC = carbonPoints.last()
        val ohPt = Offset(lastC.x + spacingX * 0.9f, lastC.y)
        drawLine(NeonCyan, lastC, ohPt, 5f)
        drawCircle(HyperPink, 14f, ohPt)
    }
    // Carboxylic Acid (-COOH) group
    else if (family.uppercase() == "CARBOXYLIC ACIDS" && carbons > 0) {
        val lastC = carbonPoints.last()
        val oDoublePt = Offset(lastC.x, lastC.y - 45f)
        val ohPt = Offset(lastC.x + spacingX * 0.7f, lastC.y + 20f)

        // double bond O
        drawLine(NeonCyan, Offset(lastC.x - 5f, lastC.y), Offset(oDoublePt.x - 5f, oDoublePt.y), 4f)
        drawLine(NeonCyan, Offset(lastC.x + 5f, lastC.y), Offset(oDoublePt.x + 5f, oDoublePt.y), 4f)
        drawCircle(CosmicGold, 10f, oDoublePt) // O

        // single bond -OH
        drawLine(NeonCyan, lastC, ohPt, 4f)
        drawCircle(HyperPink, 10f, ohPt) // OH
    }
}

// 9. Thermodynamics energy levels
private fun DrawScope.drawThermoSpace(width: Float, height: Float, params: Map<String, Any>) {
    val cx = width / 2f
    val cy = height / 2f

    val dh = (params["dh"] as? Double ?: -120.0).toFloat()
    val isExothermic = dh < 0f

    val startY = cy + if (isExothermic) -40f else 40f
    val endY = cy + if (isExothermic) 40f else -40f

    val barW = 120f
    val barH = 15f

    // Reactants Energy Level Bar (Gold)
    drawRect(
        color = CosmicGold,
        topLeft = Offset(cx - 140f, startY - barH / 2f),
        size = Size(barW, barH)
    )

    // Products Energy Level Bar (Pink)
    drawRect(
        color = HyperPink,
        topLeft = Offset(cx + 20f, endY - barH / 2f),
        size = Size(barW, barH)
    )

    // Draw transition arrow
    val arrowX1 = cx - 20f
    val arrowX2 = cx + 20f
    val arrowPath = Path().apply {
        moveTo(arrowX1, startY)
        quadraticTo(cx, (startY + endY) / 2f, arrowX2, endY)
    }
    drawPath(arrowPath, NeonCyan, style = Stroke(width = 4f))

    // Arrow tip pointing to final state
    drawCircle(NeonCyan, 8f, Offset(arrowX2, endY))

    // Transition state hill
    val peakY = minOf(startY, endY) - 50f
    val reactionHillPath = Path().apply {
        moveTo(cx - 140f + barW, startY)
        quadraticTo(cx, peakY, cx + 20f, endY)
    }
    drawPath(reactionHillPath, TextWhite.copy(alpha = 0.25f), style = Stroke(width = 2f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)))
}

// Draw futuristic radar pattern if no calculations loaded
private fun DrawScope.drawGenericSpace(width: Float, height: Float) {
    val cx = width / 2f
    val cy = height / 2f

    // Crosshairs
    drawLine(NeonCyan.copy(alpha = 0.15f), Offset(0f, cy), Offset(width, cy), 1f)
    drawLine(NeonCyan.copy(alpha = 0.15f), Offset(cx, 0f), Offset(cx, height), 1f)

    // Expanding orbital circles
    drawCircle(color = NeonCyan.copy(alpha = 0.08f), radius = 100f, center = Offset(cx, cy), style = Stroke(width = 2f))
    drawCircle(color = NeonCyan.copy(alpha = 0.04f), radius = 180f, center = Offset(cx, cy), style = Stroke(width = 1f))

    // Digital scanner sweep line
    drawLine(
        color = NeonCyan.copy(alpha = 0.25f),
        start = Offset(cx, cy),
        end = Offset(cx + 120f, cy - 120f),
        strokeWidth = 3f
    )
    drawCircle(NeonCyan, 6f, Offset(cx + 120f, cy - 120f))
}

private fun primaryGreenOrCyan(params: Map<String, Any>): Color {
    val isInverse = params["isInverse"] as? Boolean ?: false
    return if (isInverse) HyperPink else NeonCyan
}
