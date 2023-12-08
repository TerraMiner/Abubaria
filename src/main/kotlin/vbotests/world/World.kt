package vbotests.world

import d2t.terra.abubaria.io.LagDebugger
import org.joml.Matrix4f
import org.joml.Vector3f
import vbotests.io.Window
import vbotests.render.Camera
import vbotests.render.Shader
import vbotests.util.loopWhile

class World(
    private val width: Int = 1024,
    private val height: Int = 1024,
    private val scale: Float = 16f
) {
    val tiles: Array<Byte?> = arrayOfNulls(width * height)
    private val world = Matrix4f().setTranslation(Vector3f(0f))
        .scale(scale)

    val negativeScaledWidth = -width * scale
    val negativeScaledHeight = -height * scale

    fun render(shader: Shader, camera: Camera, window: Window) {
        val viewX = ((window.width + scale * 2) / scale).toInt()
        val viewY = ((window.height + scale * 2) / scale).toInt()

        val posX = ((camera.position.x) / scale).toInt()
        val posY = ((camera.position.y) / scale).toInt()

        loopWhile(0, viewX) { x ->
            val worldX = x - posX

            loopWhile(0,viewY) {y ->
                val worldY = y - posY

                (getTile(worldX, worldY) ?: Tile.NullTile).also {
                    TileRenderer.renderTile(it, worldX, worldY, shader, world, camera)
                }
            }
        }
    }

    fun correctCamera(camera: Camera, window: Window) {
        val pos = camera.position

        pos.x = pos.x.coerceIn(negativeScaledWidth + window.width, 0f)
        pos.y = pos.y.coerceIn(negativeScaledHeight + window.height, 0f)
    }

    fun setTile(tile: Tile, x: Int, y: Int) {
        tiles[x + y * width] = tile.id
    }

    fun getTile(x: Int, y: Int): Tile? {
        return try {
            val tile = tiles[x + y * width]?.toInt() ?: run {
                Tile.tiles.size
            }
            Tile.tiles[tile]
        } catch (e: ArrayIndexOutOfBoundsException) {
            null
        }
    }

    fun generate() {
        for (x in 0..<width) {
            for (y in 0..<height) {
                setTile(Tile.tiles[1]!!, x, y)
            }
        }
    }

}