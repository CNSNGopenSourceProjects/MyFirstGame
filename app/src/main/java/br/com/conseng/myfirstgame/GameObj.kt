package br.com.conseng.myfirstgame

/**************************************************************************************************
 * Histírico da implementação:
 * 20180107     F.Camargo       Criação da classe de base comum a todos os objetos.
 **************************************************************************************************/

import android.graphics.Rect

/**
 * Abstract class for all future game objects.
 */
abstract class GameObj {
    /**
     * Current position of the object on x-axis.
     */
    var xc: Int = 0

    /**
     * Current position of the object on y-axis.
     */
    var yc: Int = 0

    /**
     * The object displacement on x-axis.
     * Positive values, moves the object left to right on the screen.
     */
    var dxc: Int = 0

    /**
     * The object displacement on y-axis.
     * Positive values, moves the object top-down on the screen.
     */
    var dyc: Int = 0

    /**
     * Current position of the object on x-axis, but as Float.
     */
    val floatXc
        get() = xc.toFloat()

    /**
     * Current position of the object on y-axis, but as Float.
     */
    val floatYc
        get() = yc.toFloat()

    /**
     * The object width.
     */
    var objWidth: Int = 0

    /**
     * The object height.
     */
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