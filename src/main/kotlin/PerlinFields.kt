import org.openrndr.application
import org.openrndr.draw.ColorFormat
import org.openrndr.draw.colorBuffer
import org.openrndr.extra.noise.Random
import org.openrndr.math.Vector3
import java.nio.ByteBuffer

fun main() = application {
    configure {
        width = 600
        height = 600
//        fullscreen = Fullscreen.CURRENT_DISPLAY_MODE
    }
    program {
        val cb = colorBuffer(width, height, format = ColorFormat.RGB)
        val buffer =
            ByteBuffer.allocateDirect(cb.width * cb.height * cb.format.componentCount * cb.type.componentSize)
        extend {
            buffer.clear()
            for (y in 0 until cb.height) {
                for (x in 0 until cb.width) {
                    for (c in 0 until cb.format.componentCount) {
                        val scale = 0.005
                        val value = Random.perlin(
                            Vector3(
                                x.toDouble() * scale,
                                y.toDouble() * scale,
                                (c + 1) * 0.2 * seconds
                            ), type = Random.Noise.QUINTIC) * 255 // Для цвета
//                        value = sin(0.2 * sin(seconds) * value) * 255
                        buffer.put(value.toInt().toByte())
                    }
                }
            }
            buffer.rewind()
            cb.write(buffer)
            drawer.image(cb)
        }
        extend(FPSDisplay())
    }
}