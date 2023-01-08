import kotlinx.coroutines.delay
import org.openrndr.Extension
import org.openrndr.PresentationMode
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.launch
import org.openrndr.math.mod
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

fun Program.frameRate(frameRate: Long) {
    window.presentationMode = PresentationMode.MANUAL

    launch {
        val t = 1000.0 / frameRate // in millis
        val t0 = mod(seconds, t / 1000.0) // in seconds
        val d = (t - t0 * 1000).toLong() // in millis

        if (d > 0L) delay(d)

        window.requestDraw()
    }
}

infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
    require(start.isFinite())
    require(endInclusive.isFinite())
    require(step > 0.0) { "Step must be positive, was: $step." }
    val sequence = generateSequence(start) { previous ->
        if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
        val next = previous + step
        if (next > endInclusive) null else next
    }
    return sequence.asIterable()
}

fun Double.roundTo(numFractionDigits: Int): Double {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return (this * factor).roundToInt() / factor
}

class FPSDisplay : Extension {

    override var enabled: Boolean = true

    private var lastDrawSec: Double = 0.0
    private var lastDrawnFrames = 0
    private var framesDelta: Int = 0

    override fun setup(program: Program) {
        lastDrawSec = program.seconds
        lastDrawnFrames = program.frameCount
    }

    override fun afterDraw(drawer: Drawer, program: Program) {
        if (abs(lastDrawSec - program.seconds) >= 1.0) {
            lastDrawSec = program.seconds
            framesDelta = program.frameCount - lastDrawnFrames - 1
            lastDrawnFrames = program.frameCount
        }
        drawer.isolated {
            drawer.fill = ColorRGBa.WHITE
            drawer.rectangle(10.0, 10.0, 130.0, 20.0)
            drawer.fill = ColorRGBa.BLACK
            drawer.text("FPS: $framesDelta | ${program.deltaTime.roundTo(3)}", 15.0, 25.0)
        }
    }
}