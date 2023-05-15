package d2t.terra.abubaria.entity.particle

import d2t.terra.abubaria.entity.Entity
import java.util.concurrent.ConcurrentLinkedQueue

open class ParticleOwner : Entity() {
    open fun initParticles() {}

    var tiles = ConcurrentLinkedQueue<Particle>()
}