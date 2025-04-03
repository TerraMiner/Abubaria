package d2t.terra.abubaria.entity

import d2t.terra.abubaria.tileSize
import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.entity.type.EntityType
import d2t.terra.abubaria.geometry.box.BlockCollisionBox
import d2t.terra.abubaria.geometry.box.CollisionBox
import d2t.terra.abubaria.geometry.box.EntityCollisionBox
import d2t.terra.abubaria.geometry.isNan
import d2t.terra.abubaria.geometry.subtract
import d2t.terra.abubaria.geometry.toVector2f
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.util.none
import d2t.terra.abubaria.util.print
import d2t.terra.abubaria.world.block.BlockFace
import org.joml.Vector2f
import kotlin.math.ceil

abstract class PhysicalEntity(type: EntityType, location: Location) : Entity(type, location) {

    var collisionBox = EntityCollisionBox(this)

    var climbHeight = tileSizeF
    var jumpHeight = .155f
    var speed = 0.15f
    var gravity = 0.03f
    var maxHorSpeed = 0.8f
    var maxVerSpeed = 2.2f

    var groundFriction = 0.85f
    var airFriction = 0.98f
    var hasAI = true
    var canCollideWithBlocks = true

    var tryJump = false

    var movement = Vector2f()

    init {
        hasCollision = false
    }

    override fun teleport(
        x: Float,
        y: Float,
    ) {
        super.teleport(x, y)
        bindHitBox()
    }

    /**
     * Задаёт вектор ускорения сущности
     */
    open fun movement(x: Float = movement.x, y: Float = movement.y) {
        movement.x = x
        movement.y = y
    }

    open fun movement(vec: Vector2f) {
        movement(vec.x, vec.y)
    }

    override fun tick() {
        if (!hasAI) return
        calculatePhysics()
    }

    /**
     * Считает физику всего энтити.
     * Тут важно соблюдать порядок выполнения действий.
     */
    open fun calculatePhysics() {
        limitSpeed()
        tickJump()
        tickFall()
        applyFriction()
        checkCollision()
        applyMovement()
    }

    /**
     *Просчитывает следующую позицию куда будет двигаться энтити
     * @return Достигнута ли цель.
     */
    open fun walkTo(target: Location, whenStop: Float): Boolean {
        val stopped = location.distance(target).let { (it <= whenStop) }
        if (stopped) return true

        val nextStep = location.toVector2f().subtract(target.toVector2f()).normalize().mul(-speed).run {
            if (isNan) Vector2f() else this
        }

        val y = movement.y

        oldLocation.direction = if (nextStep.x > 0) Direction.RIGHT
        else if (nextStep.x < 0) Direction.LEFT
        else oldLocation.direction

        nextStep.apply { movement(x, y) }

        return false
    }

    /**
     * Применяет трение.
     * Не учитывает модификаторы трения у отдельных типов блоков.
     */
    open fun applyFriction() {
        val mod = if (isOnGround) groundFriction else airFriction
        val movementBound = 0.0001f
        movement.x *= if (movement.x !in -movementBound..movementBound) mod else .0f
    }

    /**
     * Устанавливает локацию в позицию колизион-бокса
     */
    open fun applyMovement() {
        location.direction = oldLocation.direction
        collisionBox.move(movement)
        location.set(collisionBox)
        updatePosition(location.x,location.y)
    }

    open fun tickFall() {
        val mod = if (hasGravity) gravity else .0f
        movement.y += mod
    }

    open fun tickJump() {
        if (isOnGround && tryJump) {
            movement.y -= jumpHeight
        }
        tryJump = false
    }

    /**
     * Ограничение скорости вектора в допустимые рамки.
     */
    open fun limitSpeed() {
        if (movement.x > maxHorSpeed) movement.x = maxHorSpeed
        if (movement.x < -maxHorSpeed) movement.x = -maxHorSpeed
        if (movement.y > maxVerSpeed) movement.y = maxVerSpeed
        if (movement.y < -maxVerSpeed) movement.y = -maxVerSpeed
    }

    /**
     * Обработка взбирания и коллизий.
     */
    open fun checkCollision() {
        if (canCollideWithBlocks) {
            val futureBox = collisionBox.clone().expandX(movement.x)
            val forClimb = futureBox.getCollidingBlocks(location.world)
            tryClimb(futureBox, forClimb)
            collideXY()
        }
    }

    /**
     * Обработка прыжка и всевозможных коллизий.
     */
    open fun collideXY() {
        val expandedBox = collisionBox.clone().expand(movement)
        val hitBoxes = expandedBox.getCollidingBlocks(location.world)
        tryJump(expandedBox, hitBoxes)
        val collider = collisionBox.clone()
        collideY(collider,hitBoxes)
        collideX(collider,hitBoxes)
    }

    /**
     * Обработка прыжка и всевозможных столкновений.
     * @return список сторон блоков с которыми произошла коллизия
     */
    open fun collidersXY(): List<BlockFace> {
        val expandedBox = collisionBox.clone().expand(movement)
        val hitBoxes = expandedBox.getCollidingBlocks(location.world)
        tryJump(expandedBox, hitBoxes)
        val collider = collisionBox.clone()
        return listOfNotNull(
            collideY(collider,hitBoxes),
            collideX(collider,hitBoxes)
        )
    }

    /**
     * Обработка коллизии по оси X.
     */
    open fun collideX(collider: CollisionBox, colliders: List<CollisionBox>): BlockFace? {
        if (movement.x == .0f) return null
        val deltaBefore = movement.x
        movement.x = location.world.border.collideX(collider, movement.x)
        colliders.forEach { movement.x = it.collideX(collider, movement.x) }
        collider.move(movement.x, .0f)
        return if (deltaBefore != movement.x) {
            if (deltaBefore < 0) BlockFace.RIGHT else BlockFace.LEFT
        } else null
    }

    /**
     * Обработка коллизии по оси Y.
     */
    open fun collideY(collider: CollisionBox, colliders: List<CollisionBox>): BlockFace? {
        val deltaBefore = movement.y
        movement.y = location.world.border.collideY(collider, movement.y)
        colliders.forEach { movement.y = it.collideY(collider, movement.y) }
        collider.move(.0f, movement.y)
        isOnGround = deltaBefore > 0 && movement.y == .0f
        return if (deltaBefore != movement.y) {
            if (deltaBefore < 0) BlockFace.BOTTOM else BlockFace.TOP
        } else null
    }

    /**
     * Просчитывает когда сущность должна прыгнуть.
     */
    open fun tryJump(futureBox: CollisionBox, intersectsHitBoxes: List<CollisionBox>) {
        if (!isOnGround) return

        if (intersectsHitBoxes.filterIsInstance<BlockCollisionBox>().any { box ->
                futureBox.intersects(box) && (box.y - collisionBox.maxY).run { this > tileSize && this <= tileSize * 6 }
                        && none(ceil(futureBox.sizeY).toInt() / tileSize) {
                    box.relativeBox(BlockFace.TOP, it)?.let { futureBox.move(y = 1.0f).intersects(it) } == true
                }
            }) {
            tryJump = true
        }
    }

    /**
     * Обработка взбирания на блоки.
     */
    open fun tryClimb(box: CollisionBox, intersectsHitBoxes: List<CollisionBox>) {
        if (!isOnGround) return

        val boxes = intersectsHitBoxes.filter { box.intersects(it) }

        if (boxes.isEmpty()) return

        val other = boxes.minBy { (it.y - box.maxY) } as? BlockCollisionBox ?: return
        val dist = other.y - box.maxY

        if (dist.let { it > 0 || it < -climbHeight }) return

        val futureBox = box.clone().diff(y = dist)

        if (futureBox.outtersectsY(location.world.border)) return

        val afterClimbBoxes = futureBox.getCollidingBlocks(location.world)

        if (afterClimbBoxes.any { it.intersects(futureBox) }) return

        collisionBox.teleport(futureBox)
        movement.y = .0f
    }

    /**
     * Привязать хитбокс к сущности.
     */
    open fun bindHitBox() {
        collisionBox.teleport(location)
    }

    override fun spawn() {
        if (isRemoved) return
        location.world.addEntity(this)
    }

    override fun remove() {
        if (isRemoved) return
        location.world.removeEntity(this)
    }
}