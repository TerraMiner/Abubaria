package vbotests.world

import org.joml.Matrix4f
import vbotests.render.Model
import vbotests.world.TileRenderer.getTexture
import java.lang.IllegalArgumentException

class Tile(texturePath: String) {

    companion object {
        var tileIds: Byte = 0
        val tiles = arrayOfNulls<Tile>(16)
        val NullTile get() = tiles[0]!!
        init {
            tiles[0] = Tile("")
            tiles[1] = Tile("res/block/dirt.png")
            tiles[2] = Tile("res/block/stone.png")
        }
    }

    val texture = getTexture(texturePath)

    val model = Model(0f, 0f, 1f, 1f)

    val tilePos = Matrix4f()
    val target = Matrix4f()

    val id: Byte = tileIds++

    init {
        if (tiles[id.toInt()] != null) {
            throw IllegalArgumentException("Tile at: [$id] is already being used!")
        }
        tiles[id.toInt()] = this
    }
}