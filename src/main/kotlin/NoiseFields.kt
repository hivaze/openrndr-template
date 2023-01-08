import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noclear.NoClear
import org.openrndr.extra.noise.Random
import org.openrndr.math.Polar

fun main() = application {
    configure { width = 900; height = 900 }
    program {
        val zoom = 0.01
        backgroundColor = ColorRGBa.WHITE
        extend(NoClear())

        extend {
            drawer.fill = ColorRGBa(0.0, 0.0, 0.0, 0.01)
            drawer.points(generateSequence(Random.point(drawer.bounds)) {
                it + Polar(
                    180 * Random.simplex(it * zoom)
                ).cartesian
            }.take(100).toList())

        }
    }
}