package d2t.terra.abubaria.location

open class Location(var x: Double = .0, var y: Double = .0, var direction: Direction = Direction.LEFT) {
    constructor(x: Int, y: Int) : this(x.toDouble(),y.toDouble(),Direction.LEFT)

    val clone get() = Location(x,y,direction)

//    fun setLocation(location: Location) {
//        this.x = location.x
//        this.y = location.y
//    }
//
//    fun setLocation(x: Double, y: Double) {
//        this.x = x
//        this.y = y
//    }
//
//    fun move(x: Double, y: Double) {
//        this.x += x
//        this.y += y
//    }

}
