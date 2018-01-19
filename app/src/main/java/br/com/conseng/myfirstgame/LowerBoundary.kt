package br.com.conseng.myfirstgame

/**************************************************************************************************
 * Histírico da implementação:
 * 20180119     F.Camargo       Cria a borda inferior com altura variável.
 **************************************************************************************************/

import android.graphics.Canvas

/**
 * Container for one segment of the lower boundary logic.
 * This segment speed is the same speed of the background.
 * @constructor Creates the single frame image of the lower boundary.
 * @param [ac] The lower boundary sprite animation characteristics.
 * @param [x] The boundary position on X-axis
 * @param [y] The boundary position on Y-axis
 * @param [delay] Character animation delay.  Default=10.
 * @throws [IllegalArgumentException] If [x] or [y] is negative.
 * @throws [IllegalArgumentException] If [delay] is negative or zero.
 */
class LowerBoundary(private val ac: AnimationClass, private val x: Int,
                    private val y: Int, private val delay: Int = 10) :
        GameObj() {

    /**
     * Initialize the character parameters, hurling the rock with random position and speed.
     */
    init {
        // Validate the parameters
        if (x < 0) throw IllegalArgumentException("The value of X-axis must be positive: x=%d".format(x))
        if (y < 0) throw IllegalArgumentException("The value of X-axis must be positive: y=%d".format(y))
        if (delay < 1) throw IllegalArgumentException("The delay must be higher than zero: delay=%d".format(delay))
        // Set the initial position of the character and define no movement on y axis.
        this.xc = x
        this.yc = y
        this.dxc = GAME_MOVING_SPEED
        // Initialize the character sprite animation.
        ac.delay = delay
        this.objWidth = ac.frameWidth
        this.objHeight = ac.frameHeight
    }

    /**
     * Move the lower boundary with the background scenarios.
     */
    fun update() {
        this.xc += this.dxc
    }

    /**
     * Render the rock obstacle using the current animation getBitmap.
     * @param [canvas] The Canvas to which the View is rendered.
     * @see [https://developer.android.com/reference/android/view/SurfaceView.html#draw(android.graphics.Canvas)]
     */
    fun draw(canvas: Canvas?) {
        try {
            canvas!!.drawBitmap(ac.getBitmap, floatXc, floatYc, null)
        } catch (e: Exception) {
            println("ERROR WHILE DRAWING THE ROCK: ${e.message}")
        }
    }

    /**
     * Identifies this class to help on debug.
     * @return Informs the sprite animation status.
     */
    override fun toString(): String {
        return "height=${ac.frameHeight} - ${super.toString()}"
    }
}
