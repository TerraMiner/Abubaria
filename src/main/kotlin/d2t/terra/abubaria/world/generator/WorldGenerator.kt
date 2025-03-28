package d2t.terra.abubaria.world.generator

import d2t.terra.abubaria.chunkSize
import d2t.terra.abubaria.tileSize
import d2t.terra.abubaria.util.getCoords
import d2t.terra.abubaria.util.loopIndicy
import d2t.terra.abubaria.util.loopWhile
import d2t.terra.abubaria.world.Chunk
import d2t.terra.abubaria.world.World
import d2t.terra.abubaria.world.block.Position
import d2t.terra.abubaria.world.material.Material
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.random.nextInt

class WorldGenerator(private val world: World) {

    private val chunksX = world.worldChunkWidth
    private val chunksY = world.worldChunkHeight

    private val startX = 0
    private val startY = 0

    private val worldHeight = world.height / tileSize
    private val groundLevel = worldHeight / 5
    private val groundHeight = 10

    // Параметры пещер
    private val caveFrequency = 0.025
    private val caveSize = 0.55
    private val caveThreshold = 0.42
    private val caveDensityMultiplier = 1.3
    private val caveConnectionChance = 0.25

    fun generateWorld() {
        loopWhile(0, chunksX * chunksY) { index ->
            val position = getCoords(index, chunksX, chunksY)
            val chunk = Chunk(position.x, position.y).apply { initBlocks() }
            world.chunkMap[index] = chunk
            if (position.y == 0) {
                fillChunkWithAir(chunk)
            } else {
                fillChunkWithTerrain(chunk)
            }
        }

        // Генерируем пещеры после основного терейна
//        generateCaves()
    }

    private fun fillChunkWithAir(chunk: Chunk) {
        loopWhile(0, chunkSize) { x ->
            loopWhile(0, chunkSize) { y ->
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
                loopIndicy(-4, 4) { dy ->
                    if (dy >= 0) {
                        val chance = runCatching { Random.Default.nextInt(0..stoneLevel - dy) }.getOrElse { 0 }
                        if (chance == 0 && currentBlock.type !== Material.STONE
                        ) world.setBlock(Material.STONE, x, y)
                    } else {
                        val chance = runCatching { Random.Default.nextInt(0..stoneLevel + dy) }.getOrElse { 0 }
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
    }

    private fun generateCaves() {
        // Генерируем пещеры во всем мире
        val worldWidth = chunksX * chunkSize
        val worldDepth = chunksY * chunkSize

        // Начинаем генерацию с уровня земли и ниже
        for (x in 0 until worldWidth) {
            for (y in groundLevel until worldDepth) {
                // Проверка, можно ли сгенерировать пещеру в этом месте
                val block = world.getBlockAt(x, y) ?: continue

                // Пещеры только в земле и камне
                if (block.type !== Material.DIRT && block.type !== Material.STONE) {
                    continue
                }

                // Используем несколько слоев 2D шума для естественных пещер
                // Смещаем координаты для создания разнообразных паттернов
                val mainCaveNoise = caveNoiseValue(x * caveFrequency, y * caveFrequency)
                val secondaryCaveNoise = caveNoiseValue((x + 500) * caveFrequency * 2, (y + 500) * caveFrequency * 2)

                // Вертикальная плотность - глубже больше пещер
                val depthDensity = (y.toFloat() / worldDepth) * caveDensityMultiplier

                // Объединение нескольких шумов для создания комплексной структуры
                val combinedNoise = (mainCaveNoise + secondaryCaveNoise * 0.5) / 1.5

                // Главные пещеры
                if (combinedNoise > caveThreshold + depthDensity * 0.1) {
                    world.setBlock(Material.AIR, x, y)
                }

                // Случайные соединители между пещерами
                else if (Random.Default.nextFloat() < caveConnectionChance &&
                    isNearCave(x, y) &&
                    combinedNoise > caveThreshold - 0.1
                ) {
                    world.setBlock(Material.AIR, x, y)
                }
            }
        }

        // Добавляем большие пещеры/туннели в стиле Terraria
        generateLargeCaves()

        // Дополнительная обработка - добавляем сталактиты/сталагмиты и корректируем края
        refineCaves()
    }

    private fun generateLargeCaves() {
        val worldWidth = chunksX * chunkSize
        val worldDepth = chunksY * chunkSize

        // Количество больших пещерных систем
        val largeSystemCount = (worldWidth / 150).coerceAtLeast(3)

        repeat(largeSystemCount) {
            // Выбираем стартовую точку для большой пещеры
            val startX = Random.Default.nextInt(20, worldWidth - 20)
            val startY = Random.Default.nextInt(groundLevel + 20, worldDepth - 20)

            // Длина системы
            val pathLength = Random.Default.nextInt(40, 100)

            var currentX = startX.toDouble()
            var currentY = startY.toDouble()

            // Направление движения
            var dirX = Random.Default.nextDouble(-1.0, 1.0)
            var dirY = Random.Default.nextDouble(-1.0, 1.0)
            val dirLen = sqrt(dirX * dirX + dirY * dirY)
            dirX /= dirLen
            dirY /= dirLen

            // Генерируем путь
            repeat(pathLength) { step ->
                // Плавно меняем направление
                if (step % 10 == 0) {
                    dirX += Random.Default.nextDouble(-0.3, 0.3)
                    dirY += Random.Default.nextDouble(-0.2, 0.2)
                    val newDirLen = sqrt(dirX * dirX + dirY * dirY)
                    dirX /= newDirLen
                    dirY /= newDirLen
                }

                // Диаметр тоннеля меняется
                val tunnelRadius = 3 + (sin(step / 10.0) + 1) * 2

                // Рисуем тоннель как круг
                for (dx in -tunnelRadius.toInt()..tunnelRadius.toInt()) {
                    for (dy in -tunnelRadius.toInt()..tunnelRadius.toInt()) {
                        // Проверяем, что точка внутри окружности
                        if (dx * dx + dy * dy <= tunnelRadius * tunnelRadius) {
                            val blockX = (currentX + dx).toInt()
                            val blockY = (currentY + dy).toInt()

                            // Проверяем границы
                            if (blockX in 0 until worldWidth && blockY in groundLevel until worldDepth) {
                                val block = world.getBlockAt(blockX, blockY) ?: continue
                                if (block.type === Material.DIRT || block.type === Material.STONE) {
                                    world.setBlock(Material.AIR, blockX, blockY)
                                }
                            }
                        }
                    }
                }

                // Двигаемся дальше
                currentX += dirX * 2
                currentY += dirY * 2

                // Следим за границами мира
                if (currentX < 10 || currentX > worldWidth - 10 ||
                    currentY < groundLevel + 10 || currentY > worldDepth - 10
                ) {
                    return@repeat
                }
            }
        }
    }

    private fun isNearCave(x: Int, y: Int): Boolean {
        // Проверка на наличие воздуха поблизости для соединения пещер
        for (dx in -3..3) {
            for (dy in -3..3) {
                if (dx * dx + dy * dy <= 9) { // В радиусе 3 блоков
                    val block = world.getBlockAt(x + dx, y + dy) ?: continue
                    if (block.type === Material.AIR) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun refineCaves() {
        val worldWidth = chunksX * chunkSize
        val worldDepth = chunksY * chunkSize

        // Временный список блоков для изменения, чтобы избежать проблем при итерации
        val blockUpdates = mutableListOf<Pair<Int, Int>>()

        for (x in 0 until worldWidth) {
            for (y in groundLevel until worldDepth) {
                val block = world.getBlockAt(x, y) ?: continue

                if (block.type === Material.AIR) {
                    // Проверка на "потолок" пещеры для сталактитов
                    if (world.getBlockAt(x, y - 1)?.type !== Material.AIR &&
                        Random.Default.nextFloat() < 0.05
                    ) {
                        blockUpdates.add(Pair(x, y))
                    }

                    // Проверка на "пол" пещеры для сталагмитов
                    if (world.getBlockAt(x, y + 1)?.type !== Material.AIR &&
                        Random.Default.nextFloat() < 0.03
                    ) {
                        blockUpdates.add(Pair(x, y))
                    }
                }
            }
        }

        // Применяем все изменения после обхода
        for ((x, y) in blockUpdates) {
            // Добавление сталактитов и других деталей
            if (Random.Default.nextFloat() < 0.3) {
                world.setBlock(Material.STONE, x, y)
            }
        }
    }

    val seed = Random.nextLong(100000000000L, 999999999999L)
    private fun getHeight(x: Int, y: Int): Int {
        val noise = (noiseValue((startX + x) / 40.0, (startY + y) / 40.0) + 1) * 0.5
        return (groundLevel + noise * 10).toInt()
    }

    private fun noiseValue(x: Double, y: Double): Float {
        return SimplexNoise.noise2(seed, x, y)
    }

    private fun caveNoiseValue(x: Double, y: Double): Float {
        // Используем ваш существующий 2D шум, но с другим сидом для пещер
        return SimplexNoise.noise2(seed + 42069, x, y)
    }
}