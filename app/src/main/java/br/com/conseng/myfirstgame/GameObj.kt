package br.com.conseng.myfirstgame

import android.graphics.Rect

/**
 * Abstract class for all future game objects.
 */
abstract class GameObj {
    // Our x and y coordinates along with their displacement variables
    var xc: Int = 0
    var yc: Int = 0
    var dcx: Int = 0
    var dyc: Int = 0

    // Width and height of our objects
    var objWidth: Int = 0
    var objHeight: Int = 0

    /**
     * Returns the object 2D size.
     */
    fun getRectangle() = Rect(xc, yc, xc + objWidth, yc + objHeight)

    /**
     * Identifies this class to help on debug.
     * @return Informs the sprite position and size.
     */
    override fun toString(): String {
        return "position=[$xc,$yc] - width=$objWidth - height:$objHeight"
    }
}