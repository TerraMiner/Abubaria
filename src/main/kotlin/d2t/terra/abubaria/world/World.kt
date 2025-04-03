package d2t.terra.abubaria.world

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
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
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.io.graphics.render.UI_DEBUG_LAYER
import d2t.terra.abubaria.io.graphics.render.WORLD_DEBUG_LAYER
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.util.getIndex
import d2t.terra.abubaria.util.loopIndicy
import d2t.terra.abubaria.util.loopWhile
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.block.BlockInChunkPosition
import d2t.terra.abubaria.world.block.Position
import d2t.terra.abubaria.world.material.Material
import java.util.concurrent.ConcurrentHashMap

class World {
    val worldChunkWidth = 16
    val worldChunkHeight = 16

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

    fun generateWorldLight() {
        chunkMap.forEach { chunk ->
            chunk.applyForBlocks { x, y ->
                val block = GamePanel.world.getBlockAt(x, y) ?: return@applyForBlocks
                block.initLightMap()
            }
        }
    }

    fun draw() {
        val minVisibleChunkX = Camera.getWorldChunkX(0f).coerceAtLeast(0)
        val minVisibleChunkY = Camera.getWorldChunkY(0f).coerceAtLeast(0)

        val maxVisibleChunkX = Camera.getWorldChunkX(Window.width.toFloat()).coerceAtMost(worldChunkWidth - 1)
        val maxVisibleChunkY = Camera.getWorldChunkY(Window.height.toFloat()).coerceAtMost(worldChunkHeight - 1)

        val worldMinX = (minVisibleChunkX shl chunkShiftBits shl blockShiftBits).toFloat()
        val worldMinY = (minVisibleChunkY shl chunkShiftBits shl blockShiftBits).toFloat()
        val worldMaxX = ((maxVisibleChunkX + 1) shl chunkShiftBits shl blockShiftBits).toFloat()
        val worldMaxY = ((maxVisibleChunkY + 1) shl chunkShiftBits shl blockShiftBits).toFloat()

        loopIndicy(minVisibleChunkX, maxVisibleChunkX) { chunkX ->
            loopIndicy(minVisibleChunkY, maxVisibleChunkY) { chunkY ->
                if (Client.debugMode) {
                    val chunkWorldX = (chunkX shl chunkShiftBits shl blockShiftBits).toFloat()
                    val chunkWorldY = (chunkY shl chunkShiftBits shl blockShiftBits).toFloat()

                    Renderer.renderText("$chunkX, $chunkY", chunkWorldX, chunkWorldY, 16,
                        zIndex = WORLD_DEBUG_LAYER + .005f,
                        dim = RenderDimension.WORLD,
                        textVerPosition = TextVerPosition.CENTER,
                        ignoreCamera = false)

                    Renderer.renderLine(
                        chunkWorldX, worldMinY,
                        chunkWorldX, worldMaxY,
                        2f, Color.YELLOW,
                        WORLD_DEBUG_LAYER,
                        RenderDimension.WORLD,
                        false
                    )

                    Renderer.renderLine(
                        worldMinX, chunkWorldY,
                        worldMaxX, chunkWorldY,
                        2f, Color.YELLOW,
                        WORLD_DEBUG_LAYER,
                        RenderDimension.WORLD,
                        false
                    )

                    if (chunkX == maxVisibleChunkX) {
                        val rightEdge = chunkWorldX + (1 shl chunkShiftBits shl blockShiftBits).toFloat()
                        Renderer.renderLine(
                            rightEdge, worldMinY,
                            rightEdge, worldMaxY,
                            2f, Color.YELLOW,
                            WORLD_DEBUG_LAYER,
                            RenderDimension.WORLD,
                            false
                        )
                    }

                    if (chunkY == maxVisibleChunkY) {
                        val bottomEdge = chunkWorldY + (1 shl chunkShiftBits shl blockShiftBits).toFloat()
                        Renderer.renderLine(
                            worldMinX, bottomEdge,
                            worldMaxX, bottomEdge,
                            2f, Color.YELLOW,
                            WORLD_DEBUG_LAYER,
                            RenderDimension.WORLD,
                            false
                        )
                    }

                    repeat(7) {
                        val offset = (it + 1) * tileSizeF

                        Renderer.renderLine(
                            chunkWorldX + offset, worldMinY,
                            chunkWorldX + offset, worldMaxY,
                            1f, Color.GRAY,
                            WORLD_DEBUG_LAYER,
                            RenderDimension.WORLD,
                            false
                        )

                        Renderer.renderLine(
                            worldMinX, chunkWorldY + offset,
                            worldMaxX, chunkWorldY + offset,
                            1f, Color.GRAY,
                            WORLD_DEBUG_LAYER,
                            RenderDimension.WORLD,
                            false
                        )
                    }
                }

                val chunk = getChunk(chunkX, chunkY) ?: return@loopIndicy
                chunk.drawBlocks()
                chunk.drawEntities()
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