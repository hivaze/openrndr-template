import org.openrndr.KEY_ESCAPE
import org.openrndr.MouseButton
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.TransformTarget
import org.openrndr.draw.isolated
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.easing.*
import org.openrndr.extra.noise.Random
import org.openrndr.extra.shadestyles.linearGradient
import org.openrndr.extra.shapes.drawers.bezierPatch
import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import java.util.*
import kotlin.system.exitProcess

var animations = mutableMapOf<Int, (ticks: Int) -> Boolean>()
val drawPositions = Stack<Vector2>()

fun main() = application {
    configure {
        width = 768
        height = 576
    }
    program {
        backgroundColor = ColorRGBa.WHITE

        mouse.buttonDown.listen {
            when (it.button) {
                MouseButton.LEFT -> {
                    drawPositions.push(it.position)
                    animations[frameCount] = { ticks -> pulsingCircle(ticks, it.position, 20.0, 200) }
                }
                MouseButton.RIGHT -> {
                    if (!drawPositions.isEmpty()) drawPositions.pop()
                }
                else -> {}
            }
        }

        keyboard.keyDown.listen {
            when (it.key) {
                KEY_ESCAPE -> exitProcess(0)
            }
        }

        extend(Screenshots())
        extend(FPSDisplay())

        extend {
            animations = animations.filter { it.value(frameCount - it.key) } as MutableMap<Int, (Int) -> Boolean>
            drawPositions.forEach {
                drawer.isolated {
                    drawer.shadeStyle = linearGradient(
                        ColorRGBa.CYAN,
                        ColorRGBa.PINK,
                        rotation = -46.0
                    )
//                    drawer.fill = ColorRGBa.BLACK
//                    drawer.rectangle(it, 20.0, 20.0)
                    drawer.text("Something", it)
                }
            }
        }
    }
}

fun Program.pulsingCircle(ticks: Int, position: Vector2?, radius: Double, duration: Int): Boolean {
    drawer.isolated {
        drawer.stroke = ColorRGBa.GRAY
        drawer.fill = ColorRGBa.TRANSPARENT
        val secondsTime = (ticks % duration) / 60.0
        val secHalfDur = duration / 60.0 / 2.0
        val easing: Double = when (secondsTime) { // in seconds
            in 0.0..secHalfDur -> easeCircInOut(secondsTime, 0.0, 1.0, secHalfDur)
            else -> easeElasticInOut(secondsTime - secHalfDur, 1.0, -1.0, secHalfDur)
        }
        drawer.circle(position!!, radius * easing)
    }
    return ticks <= duration
}
