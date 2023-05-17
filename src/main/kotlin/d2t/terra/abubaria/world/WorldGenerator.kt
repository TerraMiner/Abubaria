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
    private val caveChance = 0.01

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
            val height = getHeight(x, y)
            if (y in (height + 1) until groundLevel + groundHeight * 2) {
                world.setBlock(Material.DIRT, x, y)
            }
        }

        chunk.applyForBlocks { x, y ->
            val height = getHeight(x, y)
            val stoneLevel = height + groundHeight

            if (y >= stoneLevel) {
                world.setBlock(Material.STONE, x, y)
            }
        }

        chunk.applyForBlocks { x, y ->

            val height = getHeight(x, y)
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
            val block = world.getBlockAt(x, y) ?: return@applyForBlocks
            if (block.type === Material.DIRT
                && world.getBlockAt(x, y - 1)?.type === Material.AIR
            ) block.type = Material.GRASS
        }

        chunk.apply {
            applyForBlocks { x, y ->
                if (y >= groundLevel + groundHeight + 10) {
                    if (Random.nextDouble() < caveChance) {
                        generateCave(x, y)
                    }
                }
            }
        }
    }

    val seed = Random.nextLong(100000000000L..999999999999L)
    private fun getHeight(x: Int, y: Int): Int {
        val noise = (noiseValue((startX + x) / 40.0, (startY + y) / 40.0/*5.0*/) + 1) * 0.5
        return (groundLevel + noise * 10).toInt()
    }

    private fun noiseValue(x: Double, y: Double): Float {
        return SimplexNoise.noise2(seed, x, y)
    }

    // генерация пещеры в блоке с заданными координатами
    private fun generateCave(x: Int, y: Int) {
        val baseX = x + Random.nextInt(-5, 5)
        val baseY = y + Random.nextInt(-5, 5)
        val radius = Random.nextInt(2, 4)
        val height = Random.nextInt(25, 50)
        val scale = Random.nextDouble(0.01, 0.03)
        val noiseSampling = Random.nextDouble(0.05, 0.2)

        for (t in 0..height) {
            val theta = t.toDouble() * 4.0 * Math.PI / height
            val cosTheta = cos(theta)
            val sinTheta = sin(theta)
            val r = radius + scale * noiseValue(baseX.toDouble(), t.toDouble() * noiseSampling)
            for (phi in 0..360) {
                val angle = phi.toDouble() * 2.0 * Math.PI / 360.0
                val dx = (r * cosTheta * cos(angle)).toInt()
                val dy = (r * sinTheta * cos(angle)).toInt()
                val currentX = baseX + dx
                val currentY = baseY + dy

                world.setBlock(Material.AIR, currentX, currentY)
            }
        }
    }

    private fun setBlock(block: Block) {
        val chunk = world.getChunkAt(block.x, block.y) ?: return
        chunk.blockMap[block.x - chunk.x * chunkSize][block.y - chunk.y * chunkSize] = block
    }
}