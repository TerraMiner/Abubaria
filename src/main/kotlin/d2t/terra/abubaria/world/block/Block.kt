package d2t.terra.abubaria.world.block

import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.event.BlockDestroyEvent
import d2t.terra.abubaria.event.BlockPlaceEvent
import d2t.terra.abubaria.event.EventService
import d2t.terra.abubaria.geometry.box.BlockCollisionBox
import d2t.terra.abubaria.world.material.Material
import d2t.terra.abubaria.io.graphics.texture.Model
import d2t.terra.abubaria.io.graphics.render.Layer
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer

class Block(
    private var material: Material = Material.AIR,
    val pos: Position = Position(0, 0)
) {
    var x by pos::x
    var y by pos::y

    val chunkX get() = pos.chunkX
    val chunkY get() = pos.chunkY

    var collisionBox = BlockCollisionBox(this)

    var type
        get() = material
        set(value) {
            material = value
            collisionBox = BlockCollisionBox(this)
        }

    fun destroy() {
        if (type === Material.AIR) return
        EventService.launch(BlockDestroyEvent(this))
        type = Material.AIR
    }

    fun place(type: Material) {
        if (type === Material.AIR) return
        this.type = type
        EventService.launch(BlockPlaceEvent(this))
    }


    fun relative(blockFace: BlockFace, dist: Int): Block? {

        return when (blockFace) {
            BlockFace.BOTTOM -> {
                world.getBlockAt(x, y + dist)
            }

            BlockFace.TOP -> {
                world.getBlockAt(x, y - dist)
            }

            BlockFace.LEFT -> {
                world.getBlockAt(x - dist, y)
            }

            BlockFace.RIGHT -> {
                world.getBlockAt(x + dist, y)
            }
        }
    }

    fun drawTexture() {
        val texture = type.texture ?: return
        Renderer.render(
            texture,
            Model.DEFAULT,
            x * tileSizeF,
            y * tileSizeF + type.state.offset * tileSizeF,
            tileSizeF,
            tileSizeF * type.scale,
            Layer.WORLD_BLOCKS_LAYER,
            RenderDimension.WORLD,
            ignoreZoom = false
        )
    }
}