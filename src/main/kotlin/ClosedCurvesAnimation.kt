import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.shadeStyle
import org.openrndr.extra.color.presets.WHEAT
import org.openrndr.extra.noise.Random
import org.openrndr.shape.Circle

fun main() = application {
    configure { width = 700; height = 700 }
    program {
        val c = List(20) {
            Circle(drawer.bounds.center, 100.0 + it * 10.0).contour
        }
        val style = shadeStyle {
            fragmentPreamble = """
                #define linearstep(edge0, edge1, x) clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0)
            """.trimIndent()
            fragmentTransform = """
                float t = c_contourPosition / p_length;
                float alpha = fract(t - p_time + p_offset);
                // make alpha zero below 0.7, then fade to 1.0
                alpha = linearstep(0.7, 1.0, alpha);
                // square alpha to bias the mix towards the first color
                x_stroke.rgb = mix(vec3(1.0, 0.0, 1.0), vec3(0.0, 1.0, 1.0), alpha*alpha);
                x_stroke.a = alpha;
            """.trimIndent()
        }

        extend {
            drawer.clear(ColorRGBa.BLACK.shade(0.2))
            drawer.strokeWeight = 4.0
            drawer.fill = null
            drawer.shadeStyle = style
            style.parameter("time", seconds * 0.3)
            c.forEachIndexed { i, c ->
                style.parameter("offset", Random.perlin(i * 0.303, seconds * 0.03 + i * 0.808))
                style.parameter("length", c.length)
                drawer.contour(c)
            }
        }
    }
}