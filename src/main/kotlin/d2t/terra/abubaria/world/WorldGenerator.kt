package d2t.terra.abubaria.world

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.SimplexNoise
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.material.Material
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

class WorldGenerator(private val world: World) {

    // количество чанков в мире
    private val chunksX = world.worldSizeX
    private val chunksY = world.worldSizeY

    // координаты стартовой точки генерации мира
    private val startX = 0
    private val startY = 0

    // высота земли и слоя камня
    private val worldHeight = world.worldHeight / tileSize
    private val groundLevel = worldHeight / 5
    private val groundHeight = 10

    // шанс появления пещеры в каждом блоке (чем ниже, тем больше шанс)
    private val caveChance = 0.002

    fun generateWorld() {

        // инициализация всех чанков и блоков в них
        for (x in 0 until chunksX) {
            for (y in 0 until chunksY) {
                val chunk = Chunk(x, y).apply { initBlocks() }
                world.chunkMap[x][y] = chunk
                if (y == 0) {
                    fillChunkWithAir(chunk)
                } else {
                    fillChunkWithTerrain(chunk)
                }
            }
        }

    }

    private fun fillChunkWithAir(chunk: Chunk) {
        for (x in 0 until chunkSize) {
            for (y in 0 until chunkSize) {
                world.setBlock(Material.AIR, chunk.x * chunkSize + x, chunk.y * chunkSize + y)
            }
        }
    }

    private fun fillChunkWithTerrain(chunk: Chunk) {

        chunk.applyForBlocks { x, y ->
            val height = getHeight(x)
            if (y in (height + 1) until groundLevel + groundHeight * 2) {
                world.setBlock(Material.DIRT, x, y)
            }
        }

        chunk.applyForBlocks { x, y ->
            val height = getHeight(x)
            val stoneLevel = height + groundHeight

            if (y >= stoneLevel) {
                world.setBlock(Material.STONE, x, y)
            }
        }

        chunk.applyForBlocks { x, y ->

            val height = getHeight(x)
            val stoneLevel = height + groundHeight
            val currentBlock = world.getBlockAt(x, y) ?: return@applyForBlocks

            if (y in stoneLevel - 4..stoneLevel + 4) {
                for (dy in -4..4) {
                    if (dy >= 0) {
                        val chance = runCatching { Random.nextInt(0..stoneLevel - dy) }.getOrElse { 0 }
                        if (chance == 0 && currentBlock.type !== Material.STONE
                        ) world.setBlock(Material.STONE, x, y)
                    } else {
                        val chance = runCatching { Random.nextInt(0..stoneLevel + dy) }.getOrElse { 0 }
                        if (chance == 0 && currentBlock.type !== Material.DIRT
                        ) world.setBlock(Material.DIRT, x, y)
                    }
                }
            }
        }

        chunk.applyForBlocks { x, y ->
            if (y >= groundLevel + groundHeight + 10) {
                if (Random.nextDouble() < caveChance) {
                    generateCave(x, y)
                }
            }
        }

        chunk.applyForBlocks { x, y ->
            val block = world.getBlockAt(x, y) ?: return@applyForBlocks
            if (block.type === Material.DIRT
                && world.getBlockAt(x, y - 1)?.type === Material.AIR
            ) block.type = Material.GRASS
        }
    }

    // высота блока на заданной координате
    val seed = Random.nextLong(0L..999999L)
    private fun getHeight(x: Int): Int {
        val noise = (SimplexNoise.noise2(seed, (startX + x) / 40.0, 5.0) + 1) * 0.5
        return (groundLevel + noise * 10).toInt()
    }

    // генерация пещеры в блоке с заданными координатами
    private fun generateCave(x: Int, y: Int) {
        val caveLength = Random.nextInt(5, 20)
        val direction = DoubleArray(caveLength) { Random.nextDouble() * 2 * Math.PI }

        for (i in 1..caveLength) {
            val currentX = x + (i * sin(direction[i - 1])).toInt()
            val currentY = y - (i * cos(direction[i - 1])).toInt()

//            if (world.getBlockAt(currentX, currentY)?.material != Material.AIR) continue

            world.setBlock(Material.AIR, currentX, currentY)
        }
    }

    private fun setBlock(block: Block) {
        val chunk = world.getChunkAt(block.x, block.y) ?: return
        chunk.blockMap[block.x - chunk.x * chunkSize][block.y - chunk.y * chunkSize] = block
    }
}