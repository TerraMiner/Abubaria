package d2t.terra.abubaria.world

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.blockChunkShiftBits
import d2t.terra.abubaria.blockIndexMask
import d2t.terra.abubaria.blockShiftBits
import d2t.terra.abubaria.chunkShiftBits
import d2t.terra.abubaria.chunkSize
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.tileSize
import d2t.terra.abubaria.geometry.box.BlockCollisionBox
import d2t.terra.abubaria.geometry.box.ColliderType
import d2t.terra.abubaria.geometry.box.CollisionBox
import d2t.terra.abubaria.geometry.position
import d2t.terra.abubaria.io.fonts.TextVerPosition
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.render.Layer
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.util.getIndex
import d2t.terra.abubaria.util.loopIndicy
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.block.BlockInChunkPosition
import d2t.terra.abubaria.world.block.Position
import d2t.terra.abubaria.world.material.Material
import java.util.concurrent.ConcurrentHashMap

class World(
    val worldChunkWidth: Int = 16,
    val worldChunkHeight: Int = 16
) {

    val width = tileSize * chunkSize * worldChunkWidth
    val height = tileSize * chunkSize * worldChunkHeight

    val spawnLocation = Location(width / 2f, tileSize * 10f, Direction.LEFT, this)

    val border =
        CollisionBox(0f, 0f, width.toFloat(), height.toFloat()).apply { colliderType = ColliderType.INSIDE }

    val chunkMap = Array(worldChunkWidth * worldChunkHeight) { Chunk() }
    val entities = ConcurrentHashMap<Int, Entity>()

    fun getChunk(x: Int, y: Int): Chunk? =
        chunkMap.getOrNull(getIndex(x, y, worldChunkWidth, worldChunkHeight))

    fun getChunk(pos: Position): Chunk? = getChunk(pos.x, pos.y)

    fun getChunkAt(x: Int, y: Int): Chunk? =
        chunkMap.getOrNull(getIndex(x shr chunkShiftBits, y shr chunkShiftBits, worldChunkWidth, worldChunkHeight))

    fun getChunkAt(pos: Position): Chunk? = getChunkAt(pos.x, pos.y)

    fun getBlockAt(x: Int, y: Int): Block? = getChunkAt(x, y)?.blockMap
        ?.getOrNull(BlockInChunkPosition.decode(x and blockIndexMask, y and blockIndexMask).value.toInt())

    fun getBlockAt(pos: Position) = getBlockAt(pos.x, pos.y)

    fun setBlock(material: Material, x: Int, y: Int) {
        getBlockAt(x, y)?.also {
            it.type = material
            it.collisionBox = BlockCollisionBox(it)
        }
    }


    fun renderBlocks() {
        val minVisibleChunkX = Camera.getWorldChunkX(0f).coerceAtLeast(0)
        val minVisibleChunkY = Camera.getWorldChunkY(0f).coerceAtLeast(0)
        val maxVisibleChunkX = Camera.getWorldChunkX(Window.width.toFloat()).coerceAtMost(worldChunkWidth - 1)
        val maxVisibleChunkY = Camera.getWorldChunkY(Window.height.toFloat()).coerceAtMost(worldChunkHeight - 1)
        loopIndicy(minVisibleChunkX, maxVisibleChunkX) { chunkX ->
            loopIndicy(minVisibleChunkY, maxVisibleChunkY) { chunkY ->
                getChunk(chunkX, chunkY)?.drawBlocks()
            }
        }
    }

    fun renderEntities() {
        val minVisibleChunkX = Camera.getWorldChunkX(0f).coerceAtLeast(0)
        val minVisibleChunkY = Camera.getWorldChunkY(0f).coerceAtLeast(0)
        val maxVisibleChunkX = Camera.getWorldChunkX(Window.width.toFloat()).coerceAtMost(worldChunkWidth - 1)
        val maxVisibleChunkY = Camera.getWorldChunkY(Window.height.toFloat()).coerceAtMost(worldChunkHeight - 1)
        loopIndicy(minVisibleChunkX, maxVisibleChunkX) { chunkX ->
            loopIndicy(minVisibleChunkY, maxVisibleChunkY) { chunkY ->
                getChunk(chunkX, chunkY)?.drawEntities()
            }
        }
    }

    fun renderGrid() {
        if (Client.showWorldGrid) {
            val minVisibleChunkX = Camera.getWorldChunkX(0f).coerceAtLeast(0)
            val minVisibleChunkY = Camera.getWorldChunkY(0f).coerceAtLeast(0)
            val maxVisibleChunkX = Camera.getWorldChunkX(Window.width.toFloat()).coerceAtMost(worldChunkWidth - 1)
            val maxVisibleChunkY = Camera.getWorldChunkY(Window.height.toFloat()).coerceAtMost(worldChunkHeight - 1)

            val wmx = (minVisibleChunkX shl blockChunkShiftBits).toFloat()
            val wmy = (minVisibleChunkY shl blockChunkShiftBits).toFloat()
            val wmxx = ((maxVisibleChunkX + 1) shl blockChunkShiftBits).toFloat()
            val wmxy = ((maxVisibleChunkY + 1) shl blockChunkShiftBits).toFloat()

            val dim = RenderDimension.WORLD
            val layer = Layer.WORLD_DEBUG_LAYER

            loopIndicy(minVisibleChunkX, maxVisibleChunkX) { x ->
                loopIndicy(minVisibleChunkY, maxVisibleChunkY) { y ->
                    val wx = (x shl blockChunkShiftBits).toFloat()
                    val wy = (y shl blockChunkShiftBits).toFloat()

                    Renderer.renderText("$x, $y", wx, wy, 16, layer, dim, verPos = TextVerPosition.CENTER, ignoreZoom = false)
                    Renderer.renderLine(wx, wmy, wx, wmxy, layer, dim, 2f, Color.YELLOW, false)
                    Renderer.renderLine(wmx, wy, wmxx, wy, layer, dim, 2f, Color.YELLOW, false)

                    if (x == maxVisibleChunkX) {
                        val redge = wx + (1 shl blockChunkShiftBits).toFloat()
                        Renderer.renderLine(redge, wmy, redge, wmxy, layer, dim, 2f, Color.YELLOW, false)
                    }

                    if (y == maxVisibleChunkY) {
                        val bottomEdge = wy + (1 shl chunkShiftBits shl blockShiftBits).toFloat()
                        Renderer.renderLine(wmx, bottomEdge, wmxx, bottomEdge, layer, dim, 2f, Color.YELLOW, false)
                    }

                    repeat(7) {
                        val offset = (it + 1) * tileSizeF
                        Renderer.renderLine(wx + offset, wmy, wx + offset, wmxy, layer, dim, 1f, Color.GRAY, false)
                        Renderer.renderLine(wmx, wy + offset, wmxx, wy + offset, layer, dim, 1f, Color.GRAY, false)
                    }
                }
            }
        }
    }

    fun addEntity(entity: Entity) {
        entities[entity.id] = entity
        getChunk(entity.location.position.chunkPosition)?.addEntity(entity)
    }

    fun removeEntity(entity: Entity) {
        entities.remove(entity.id)
        getChunk(entity.location.position.chunkPosition)?.removeEntity(entity)
    }

    fun tick() {
        entities.values.forEach(Entity::tick)
    }
}