package vbotests.world

import d2t.terra.abubaria.io.LagDebugger
import org.joml.Matrix4f
import vbotests.render.Camera
import vbotests.render.Model
import vbotests.render.Shader
import vbotests.render.Texture
import java.nio.file.Paths
import kotlin.io.path.exists


object TileRenderer {

    val imageTextures = HashMap<String, Texture?>()

    fun getTexture(path: String) = imageTextures.getOrPut(path) {
        val res = javaClass.getClassLoader().getResource(path.replace("res/",""))
        println(res?.toURI()?.let { Paths.get(it).exists() } != true)
        if (path == "" || res?.toURI()?.let { Paths.get(it).exists() } != true) {
            null
        } else Texture(path)
    }


    fun renderTile(tile: Tile, x: Int, y: Int, shader: Shader, world: Matrix4f, cam: Camera) {
        tile.texture?.bind() ?: return

        tile.tilePos.m30(x.toFloat()).m31(y.toFloat())

        cam.getProjection().mul(world, tile.target)

        tile.target.mul(tile.tilePos)

        shader.setProjectionUniform(tile.target)

        tile.model.render()
    }
}