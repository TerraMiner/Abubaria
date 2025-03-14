package d2t.terra.abubaria.io.graphics

class Color(val rgba: Int) {

    constructor(r: Int, g: Int, b: Int) : this(r, g, b, 255)
    constructor(r: Int, g: Int, b: Int, a: Int) : this((r shl 24) or (g shl 16) or (b shl 8) or a)

    constructor(r: Float, g: Float, b: Float) : this(r, g, b, 1.0f)
    constructor(r: Float, g: Float, b: Float, a: Float) : this(
        ((r * 255).toInt() shl 24) or
                ((g * 255).toInt() shl 16) or
                ((b * 255).toInt() shl 8) or
                ((a * 255).toInt())
    )

    val r: Float by lazy { ((rgba shr 24) and 0xFF) / 255.0f }
    val g: Float by lazy { ((rgba shr 16) and 0xFF) / 255.0f }
    val b: Float by lazy { ((rgba shr 8) and 0xFF) / 255.0f }
    val a: Float by lazy { (rgba and 0xFF) / 255.0f }

    val ri: Int by lazy { ((rgba shr 24) and 0xFF) }
    val gi: Int by lazy { ((rgba shr 16) and 0xFF) }
    val bi: Int by lazy { ((rgba shr 8) and 0xFF) }
    val ai: Int by lazy { (rgba and 0xFF) }

    fun toInt(): Int = rgba

    companion object {
        val WHITE = Color(0xFFFFFFFF.toInt())
        val BLACK = Color(0x000000FF.toInt())
        val RED = Color(0xFF0000FF.toInt())
        val GREEN = Color(0x00FF00FF.toInt())
        val BLUE = Color(0x0000FFFF.toInt())
        val YELLOW = Color(0xFFFF00FF.toInt())
        val CYAN = Color(0x00FFFFFF.toInt())
        val MAGENTA = Color(0xFF00FFFF.toInt())

        const val MAX_GRADIENT_STEPS = 360

        fun gradientRainbow(step: Int, maxStep: Int = MAX_GRADIENT_STEPS): Color {
            val hue = (step.toFloat() / maxStep) * 360.0f
            val rgb = hsvToRgb(hue, 1.0f, 1.0f)
            return Color(rgb[0], rgb[1], rgb[2])
        }

        private fun hsvToRgb(h: Float, s: Float, v: Float): FloatArray {
            val c = v * s
            val x = c * (1 - Math.abs((h / 60) % 2 - 1))
            val m = v - c
            var r = 0.0f
            var g = 0.0f
            var b = 0.0f

            when {
                h < 60 -> {
                    r = c
                    g = x
                    b = 0.0f
                }
                h < 120 -> {
                    r = x
                    g = c
                    b = 0.0f
                }
                h < 180 -> {
                    r = 0.0f
                    g = c
                    b = x
                }
                h < 240 -> {
                    r = 0.0f
                    g = x
                    b = c
                }
                h < 300 -> {
                    r = x
                    g = 0.0f
                    b = c
                }
                else -> {
                    r = c
                    g = 0.0f
                    b = x
                }
            }
            return floatArrayOf(r + m, g + m, b + m)
        }
    }
}
