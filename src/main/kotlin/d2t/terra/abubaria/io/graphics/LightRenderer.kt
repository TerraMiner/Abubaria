package d2t.terra.abubaria.io.graphics

import d2t.terra.abubaria.io.graphics.render.Layer
import d2t.terra.abubaria.io.graphics.render.Renderer.shader
import d2t.terra.abubaria.io.graphics.shader.Shader
import d2t.terra.abubaria.io.graphics.texture.Model
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.util.Random

object LightRenderer {
    private lateinit var lightingShader: LightShader
    private var quadVAO: Int = 0
    private var quadVBO: Int = 0
    private var noiseTextureId: Int = 0
    private var startTime: Long = System.currentTimeMillis()
    
    // Новые переменные для фреймбуфера препятствий
    private var obstacleFramebuffer: Int = 0
    private var obstacleTexture: Int = 0
    private var obstacleDepthBuffer: Int = 0

    private const val MAX_LIGHTS = 20

    private val lightPositionsBuffer: FloatBuffer = BufferUtils.createFloatBuffer(MAX_LIGHTS * 3)
    private val lightFalloffsBuffer: FloatBuffer = BufferUtils.createFloatBuffer(MAX_LIGHTS)
    private val lightColorsBuffer: FloatBuffer = BufferUtils.createFloatBuffer(MAX_LIGHTS * 3)
    private val lightIntensitiesBuffer: FloatBuffer = BufferUtils.createFloatBuffer(MAX_LIGHTS)
    private val lightFlickerAmountBuffer: FloatBuffer = BufferUtils.createFloatBuffer(MAX_LIGHTS)
    private val lightColorShiftBuffer: FloatBuffer = BufferUtils.createFloatBuffer(MAX_LIGHTS)
    private val lightPenetrationMultipliersBuffer: FloatBuffer = BufferUtils.createFloatBuffer(MAX_LIGHTS)

    var ambientDarkness: Float = 1f
    var lightPenetration: Float = .5f // Значение по умолчанию (0.0 - свет не проходит, 1.0 - полностью проходит)

    class LightShader : Shader("shaders/lighting") {
        var screenSizeLoc: Int = -1
        var ambientDarknessLoc: Int = -1
        var timeLoc: Int = -1
        var noiseTextureLoc: Int = -1
        var obstacleTextureLoc: Int = -1  // Новая локация для текстуры препятствий
        var lightPenetrationLoc: Int = -1 // Новая локация для параметра проходимости
        var lightingCountLoc: Int = -1
        var lightPositionsLoc: Int = -1
        var lightFalloffsLoc: Int = -1
        var lightColorsLoc: Int = -1
        var lightIntensitiesLoc: Int = -1
        var lightFlickerAmountLoc: Int = -1
        var lightColorShiftLoc: Int = -1
        var lightLayerDepthLoc: Int = -1
        var lightPenetrationMultipliersLoc: Int = -1 // Новая локация

        override fun build() {
            loadVertexShader()
            loadFragmentShader()

            if (program <= 0) {
                println("ОШИБКА: Не удалось загрузить шейдеры освещения!")
                return
            }

            linkAndValidateProgram()

            screenSizeLoc = glGetUniformLocation(program, "screenSize")
            ambientDarknessLoc = glGetUniformLocation(program, "ambientDarkness")
            timeLoc = glGetUniformLocation(program, "time")
            noiseTextureLoc = glGetUniformLocation(program, "noiseTexture")
            obstacleTextureLoc = glGetUniformLocation(program, "obstacleTexture")  // Получаем локацию для текстуры препятствий
            lightPenetrationLoc = glGetUniformLocation(program, "lightPenetration") // Получаем локацию для параметра проходимости
            lightingCountLoc = glGetUniformLocation(program, "lightCount")
            lightPositionsLoc = glGetUniformLocation(program, "lightPositions")
            lightFalloffsLoc = glGetUniformLocation(program, "lightFalloffs")
            lightColorsLoc = glGetUniformLocation(program, "lightColors")
            lightIntensitiesLoc = glGetUniformLocation(program, "lightIntensities")
            lightFlickerAmountLoc = glGetUniformLocation(program, "lightFlickerAmount")
            lightColorShiftLoc = glGetUniformLocation(program, "lightColorShift")
            lightLayerDepthLoc = glGetUniformLocation(program, "lightDepth")
            lightPenetrationMultipliersLoc = glGetUniformLocation(program, "lightPenetrationMultipliers")
        }

        fun render(lights: List<Light>) {
            bind()
            glUniform2f(screenSizeLoc, Window.width.toFloat(), Window.height.toFloat())

            glUniform1f(ambientDarknessLoc, ambientDarkness)
            glUniform1f(lightLayerDepthLoc, -Layer.WORLD_LIGHT_LAYER.value)
            glUniform1f(lightPenetrationLoc, lightPenetration) // Устанавливаем значение проходимости света

            val currentTime = (System.currentTimeMillis() - startTime) / 1000.0f
            glUniform1f(timeLoc, currentTime)

            // Привязываем текстуру шума
            glActiveTexture(GL_TEXTURE0)
            glBindTexture(GL_TEXTURE_2D, noiseTextureId)
            glUniform1i(noiseTextureLoc, 0)
            
            // Привязываем текстуру препятствий
            glActiveTexture(GL_TEXTURE1)
            glBindTexture(GL_TEXTURE_2D, obstacleTexture)
            glUniform1i(obstacleTextureLoc, 1)

            val lightCount = minOf(lights.size, MAX_LIGHTS)
            glUniform1i(lightingCountLoc, lightCount)

            lightPositionsBuffer.clear()
            lightFalloffsBuffer.clear()
            lightColorsBuffer.clear()
            lightIntensitiesBuffer.clear()
            lightFlickerAmountBuffer.clear()
            lightColorShiftBuffer.clear()
            lightPenetrationMultipliersBuffer.clear()

            for (i in 0 until lightCount) {
                val light = lights[i]

                lightPositionsBuffer.put(light.x)
                lightPositionsBuffer.put(light.y)
                lightPositionsBuffer.put(light.radius)

                lightFalloffsBuffer.put(light.falloff)

                lightColorsBuffer.put(light.color.r)
                lightColorsBuffer.put(light.color.g)
                lightColorsBuffer.put(light.color.b)

                lightIntensitiesBuffer.put(light.intensity)
                lightFlickerAmountBuffer.put(light.flickerAmount)
                lightColorShiftBuffer.put(light.colorShift)
                lightPenetrationMultipliersBuffer.put(light.penetrationMultiplier)
            }

            lightPositionsBuffer.flip()
            lightFalloffsBuffer.flip()
            lightColorsBuffer.flip()
            lightIntensitiesBuffer.flip()
            lightFlickerAmountBuffer.flip()
            lightColorShiftBuffer.flip()
            lightPenetrationMultipliersBuffer.flip()

            glUniform3fv(lightPositionsLoc, lightPositionsBuffer)
            glUniform1fv(lightFalloffsLoc, lightFalloffsBuffer)
            glUniform3fv(lightColorsLoc, lightColorsBuffer)
            glUniform1fv(lightIntensitiesLoc, lightIntensitiesBuffer)
            glUniform1fv(lightFlickerAmountLoc, lightFlickerAmountBuffer)
            glUniform1fv(lightColorShiftLoc, lightColorShiftBuffer)
            glUniform1fv(lightPenetrationMultipliersLoc, lightPenetrationMultipliersBuffer)

            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            glEnable(GL_DEPTH_TEST)
            glDepthFunc(GL_LEQUAL)

            glBindVertexArray(quadVAO)
            glDrawArrays(GL_TRIANGLES, 0, 6)
            glBindVertexArray(0)
            
            // Отвязываем текстуры после рендеринга
            glActiveTexture(GL_TEXTURE1)
            glBindTexture(GL_TEXTURE_2D, 0)
            
            glActiveTexture(GL_TEXTURE0)
            glBindTexture(GL_TEXTURE_2D, 0)
            
            glUseProgram(0)
        }

        fun cleanup() {
            if (program > 0) {
                glDeleteProgram(program)
            }
        }
    }

    fun init() {
        lightingShader = LightShader()
        lightingShader.register()

        createQuad()
        createNoiseTexture()
        createObstacleFramebuffer()

        startTime = System.currentTimeMillis()
    }

    private fun createQuad() {
        val array = arrayOf(
            0f,0f, 0f,0f,
            0f,0f, 0f,0f,
            0f,0f, 0f,0f,
            0f,0f, 0f,0f
        ).toFloatArray()

        quadVAO = glGenVertexArrays()
        quadVBO = glGenBuffers()

        glBindVertexArray(quadVAO)

        glBindBuffer(GL_ARRAY_BUFFER, quadVBO)
        glBufferData(GL_ARRAY_BUFFER, array, GL_STATIC_DRAW)

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0)
        glEnableVertexAttribArray(0)

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4)
        glEnableVertexAttribArray(1)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    private fun createNoiseTexture() {
        val size = 256
        val noiseData = ByteBuffer.allocateDirect(size * size * 4)
        val random = Random()

        for (y in 0 until size) {
            for (x in 0 until size) {
                val value = (random.nextFloat() * 255).toInt().toByte()
                noiseData.put(value) // R
                noiseData.put(value) // G
                noiseData.put(value) // B
                noiseData.put(255.toByte()) // A
            }
        }

        noiseData.flip()

        noiseTextureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, noiseTextureId)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size, size, 0, GL_RGBA, GL_UNSIGNED_BYTE, noiseData)

        glBindTexture(GL_TEXTURE_2D, 0)

        println("Создана шумовая текстура: ID=$noiseTextureId")
    }
    
    // Создаем фреймбуфер для захвата препятствий
    private fun createObstacleFramebuffer() {
        obstacleFramebuffer = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, obstacleFramebuffer)
        
        // Создаем текстуру для хранения цветовых данных
        obstacleTexture = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, obstacleTexture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, Window.width, Window.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null as ByteBuffer?)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, obstacleTexture, 0)
        
        // Создаем буфер глубины
        obstacleDepthBuffer = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, obstacleDepthBuffer)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, Window.width, Window.height)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, obstacleDepthBuffer)
        
        // Проверяем статус фреймбуфера
        val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            println("ОШИБКА: Фреймбуфер препятствий не завершен! Статус: $status")
        } else {
            println("Фреймбуфер препятствий создан успешно: ID=$obstacleFramebuffer")
        }
        
        // Возвращаемся к стандартному фреймбуферу
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }
    
    // Метод для обновления размера фреймбуфера при изменении размера окна
    fun updateProjection() {
        shader.bind()
        shader.setProjection(Matrix4f().setOrtho2D(0f, Window.width.toFloat(), Window.height.toFloat(), 0f))
        shader.setCameraCenter(Window.centerX, Window.centerY)
        val width = Window.width
        val height = Window.height
        glBindTexture(GL_TEXTURE_2D, obstacleTexture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null as ByteBuffer?)
        
        glBindRenderbuffer(GL_RENDERBUFFER, obstacleDepthBuffer)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height)
        
        println("Фреймбуфер препятствий изменен до размера: ${width}x${height}")
    }

    fun beginObstacleCapture() {
        glBindFramebuffer(GL_FRAMEBUFFER, obstacleFramebuffer)
        val c = Window.bgColor
        glClearColor(c.r, c.g, c.b, c.a)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun endObstacleCapture() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun render(lights: List<Light>) {
        lightingShader.render(lights)
    }

    fun cleanup() {
        glDeleteVertexArrays(quadVAO)
        glDeleteBuffers(quadVBO)
        glDeleteTextures(noiseTextureId)
        glDeleteTextures(obstacleTexture)
        glDeleteRenderbuffers(obstacleDepthBuffer)
        glDeleteFramebuffers(obstacleFramebuffer)
        lightingShader.cleanup()
    }
}


data class Light(
    var x: Float,
    var y: Float,
    var radius: Float,
    var falloff: Float = 0.8f,
    var color: Color = Color.WHITE,
    var intensity: Float = 1.2f,
    var flickerAmount: Float = 0.0f,
    var colorShift: Float = 0.0f,
    var penetrationMultiplier: Float = 1.0f
) {
    companion object {
        // Обновленные вспомогательные функции для создания различных типов источников света
        fun createFireLight(x: Float, y: Float, radius: Float, penetration: Float = 1.0f): Light {
            return Light(
                x = x,
                y = y,
                radius = radius,
                falloff = 1.0f,
                color = Color(1.0f, 0.6f, 0.2f),
                intensity = 1.5f,
                flickerAmount = 0.2f,
                colorShift = 0.05f,
                penetrationMultiplier = penetration
            )
        }

        fun createTorchLight(x: Float, y: Float, penetration: Float = 1.0f): Light {
            return Light(
                x = x,
                y = y,
                radius = 120f,
                falloff = 1.2f,
                color = Color(1.0f, 0.7f, 0.3f),
                intensity = 1.3f,
                flickerAmount = 0.15f,
                penetrationMultiplier = penetration
            )
        }

        fun createMagicLight(x: Float, y: Float, color: Color, radius: Float = 150f, penetration: Float = 1.5f): Light {
            return Light(
                x = x,
                y = y,
                radius = radius,
                falloff = 0.7f,
                color = color,
                intensity = 1.4f,
                flickerAmount = 0.05f,
                colorShift = 0.1f,
                penetrationMultiplier = penetration // Магический свет может иметь повышенную проходимость
            )
        }

        fun createSunlight(x: Float, y: Float, penetration: Float = 1.2f): Light {
            return Light(
                x = x,
                y = y,
                radius = 250f,
                falloff = 0.5f,
                color = Color(1.0f, 0.95f, 0.8f),
                intensity = 1.6f,
                penetrationMultiplier = penetration
            )
        }

        fun createMoonlight(x: Float, y: Float, penetration: Float = 1.3f): Light {
            return Light(
                x = x,
                y = y,
                radius = 300f,
                falloff = 0.6f,
                color = Color(0.8f, 0.85f, 1.0f),
                intensity = 1.2f,
                flickerAmount = 0.02f,
                penetrationMultiplier = penetration
            )
        }
    }
}