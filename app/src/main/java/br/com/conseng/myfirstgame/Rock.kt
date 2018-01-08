package br.com.conseng.myfirstgame

/**************************************************************************************************
 * Histírico da implementação:
 * 20180108     F.Camargo       Acréscimo deste obstáculos: a rocha.
 **************************************************************************************************/

import android.graphics.Canvas
import java.util.*
import kotlin.math.min

/**
 * Container for the rock obstacle logic.
 * @constructor Creates the rock obstacle logic loading the frames images and the character size.
 * @param [ac] The player sprite animation characteristics.
 * @param [scoreWeight] Define the obstacle score.
 * @param [delay] Character animation delay.  Default=10.
 * @throws [IllegalArgumentException] If [delay] is negative or zero.
 */
class Rock(private val ac: AnimationClass, private val scoreWeight: Int, private val delay: Int = 100) :
        GameObj() {
//    /**
//     * Defines if the obstacle is active or not.
//     */
//    var playing: Boolean = false

    /**
     * Necessary to randomize the rock behaviour.
     */
    private var rnd = Random()

    /**
     * Saves the rock speed.
     */
    private var speed: Int = 7

//    /**
//     * Saves the initial time for score logic.
//     */
//    private var startTime: Long = 0

    // Start coordinate of the rock obstacle.
    /**
     * Provides the initial rock position on X-axis.
     */
    private fun getInitialX(): Int = GAME_SURFACE_WIDTH //- objWidth

    /**
     * Provides the initial rock position on Y-axis.
     */
    private fun getInitialY(): Int = min(GAME_SURFACE_HEIGHT - objHeight, (rnd.nextDouble() * GAME_SURFACE_HEIGHT.toDouble()).toInt())

    /**
     * Provides the displacement on X-axis. Higher score = faster rock!
     */
    private fun getDisplacementX(): Int {
        speed = min(35, (7 + (rnd.nextDouble() * scoreWeight.toDouble() / 30.0).toInt()))
        return -speed
    }

    /**
     * Provides the displacement on Y-axis.
     */
    private fun getDisplacementY(): Int = min(20, (rnd.nextDouble() * 40.toDouble()).toInt() - 20)

    /**
     * Move the rock right to left.
     * When the rocks reached the left side, a new rock starts on right side.
     * @return 'true' if the reached the left side and a new rock started on right side .
     */
    private fun updateX(): Boolean {
        val next = xc + dxc
        val newRock: Boolean = next <= -objWidth
        if (newRock) {                      // The rock disappear on left side?
            hurlsRock()                     // YES, a new rock will start on right side
        } else {
            xc = next
        }
        return newRock
    }

    /**
     * Move the rock on diagonal.
     * Reverse the direction when hit the boundary.
     * @see [updateX] If a new rock started on right side, do not execute this method.
     */
    private fun updateY() {
        val next = yc + dyc
        when {
            next > GAME_SURFACE_HEIGHT - objHeight -> {
                yc = GAME_SURFACE_HEIGHT - objHeight
                dyc = -dyc
            }
            next < 0 -> {
                yc = 0
                dyc = -dyc
            }
            else -> yc = next
        }
    }

    /**
     * Hurls a new rock, starting on right side.
     * Position (y-axis) and speed (x-axis) will be random.
     */
    private fun hurlsRock() {
        xc = getInitialX()
        yc = getInitialY()
        dxc = getDisplacementX()
        dyc = getDisplacementY()
    }

    /**
     * Initialize the character parameters, hurling the rock with random position and speed.
     */
    init {
        hurlsRock()
        ac.delay = delay
        objWidth = ac.frameWidth
        objHeight = ac.frameHeight
//        startTime = System.nanoTime()
    }

    /**
     * Create a loop of events that assign a score to the obstacle and keep the obstacle between the
     * upper and lower bounds.
     */
    fun update() {
//        val elapsed = (System.nanoTime() - startTime) / 1000000
        if (!updateX()) updateY()           // Update the rock position
        ac.update()

//        startTime = System.nanoTime()
    }

    /**
     * Render the rock obstacle using the current animation getBitmap.
     * @param [canvas] The Canvas to which the View is rendered.
     * @see [https://developer.android.com/reference/android/view/SurfaceView.html#draw(android.graphics.Canvas)]
     * @since The superclass MUST be called.
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
        return "scoreWeight=$scoreWeight - speed=$speed - numberOfFrames=${ac.numberOfFrames} - ${super.toString()}"
    }
}
