package br.com.conseng.myfirstgame

/**************************************************************************************************
 * Histírico da implementação:
 * 20180108     F.Camargo       Criada a explosão.
 **************************************************************************************************/

import android.graphics.Canvas

/**
 * Container for the explosion logic.
 * @constructor Creates the explosion logic loading the frames images and the character size.
 * @param [ac] The explosion sprite animation characteristics.
 * @param [delay] Character animation delay.  Default=10.
 * @throws [IllegalArgumentException] If [delay] is negative or zero.
 */
class Explosion(private val ac: AnimationClass, private val delay: Int = 10) :
        GameObj() {
    /**
     * Defines if the explosion is active or not.
     */
    var playing: Boolean = false
        private set

    /**
     * Start the sequence of explosion on a specific position.
     * @param [x] Coordenate on x-axis.
     * @param [y] Coordenate on y-axis.
     * @throws [IllegalArgumentException] Invalid coordenates.
     */
    fun doExplosion(x: Int, y: Int, delay: Int = 10) {
        if ((x < 0) or (x >= GAME_SURFACE_WIDTH))
            throw IllegalArgumentException("The coordenate of the x-axis is out of the screen=%s".format(x))
        else
            xc = x
        if ((y < 0) or (y >= GAME_SURFACE_HEIGHT))
            throw IllegalArgumentException("The coordenate of the y-axis is out of the screen=%s".format(y))
        else
            yc = y
        playing = true
        update()
    }

    /**
     * Initialize the character parameters.
     */
    init {
        ac.delay = delay
        objWidth = ac.frameWidth
        objHeight = ac.frameHeight
    }

    /**
     * Create a loop of events that assign a score to the explosion and keep the explosion between the
     * upper and lower bounds.
     */
    fun update() {
        if (playing) {              // Shows the explosion a single time
            ac.update()
            playing = !ac.playedOnce
        }
    }

    /**
     * Render the explosion using the current animation getBitmap.
     * @param [canvas] The Canvas to which the View is rendered.
     * @see [https://developer.android.com/reference/android/view/SurfaceView.html#draw(android.graphics.Canvas)]
     * @since The superclass MUST be called.
     */
    fun draw(canvas: Canvas?) {
        if (playing) {              // Shows the explosion a single time
            try {
                canvas!!.drawBitmap(ac.getBitmap, floatXc, floatYc, null)
            } catch (e: Exception) {
                println("ERROR WHILE DRAWING THE EXPLOSION: ${e.message}")
            }
        }
    }

    /**
     * Identifies this class to help on debug.
     * @return Informs the sprite animation status.
     */
    override fun toString(): String {
        return "playing=$playing - delay=$delay - numberOfFrames=${ac.numberOfFrames} - ${super.toString()}"
    }
}
