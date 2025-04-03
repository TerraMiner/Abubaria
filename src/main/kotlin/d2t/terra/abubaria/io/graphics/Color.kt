package d2t.terra.abubaria.io.graphics

import kotlin.math.abs

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
         val BLACK = Color(0x000000FF.toInt())            //&0
         val DARK_BLUE = Color(0x0000AAFF.toInt())        //&1
         val DARK_GREEN = Color(0x00AA00FF.toInt())       //&2
         val DARK_AQUA = Color(0x00AAAAFF.toInt())        //&3
         val DARK_RED = Color(0xAA0000FF.toInt())         //&4
         val DARK_PURPLE = Color(0xAA00AAFF.toInt())      //&5
         val GOLD = Color(0xFFAA00FF.toInt())             //&6
         val GRAY = Color(0xAAAAAAFF.toInt())             //&7
         val DARK_GRAY = Color(0x555555FF.toInt())        //&8
         val BLUE = Color(0x5555FFFF.toInt())             //&9
         val GREEN = Color(0x55FF55FF.toInt())            //&a
         val AQUA = Color(0x55FFFFFF.toInt())             //&b
         val RED = Color(0xFF5555FF.toInt())              //&c
         val LIGHT_PURPLE = Color(0xFF55FFFF.toInt())     //&d
         val YELLOW = Color(0xFFFF55FF.toInt())           //&e
         val WHITE = Color(0xFFFFFFFF.toInt())            //&f

        const val MAX_GRADIENT_STEPS = 360

        fun gradientRainbow(step: Int, maxStep: Int = MAX_GRADIENT_STEPS): Color {
            val hue = (step.toFloat() / maxStep) * 360.0f

            val x = 1.0f * (1 - abs((hue / 60) % 2 - 1))

            return if (hue < 60) Color(1f, x, 0F)
            else if (hue < 120) Color(x, 1f, 0f)
            else if (hue < 180) Color(0f, 1f, x)
            else if (hue < 240) Color(0f, x, 1f)
            else if (hue < 300) Color(x, 0f, 1f)
            else Color(1f, 0f, x)
        }
    }
}
