package br.com.conseng.myfirstgame

/**************************************************************************************************
 * Histírico da implementação:
 * 20180108     F.Camargo       Acréscimo deste obstáculos: a rocha.
 **************************************************************************************************/

import android.graphics.Bitmap
import android.graphics.Canvas
import java.util.*
import kotlin.math.min

/**
 * Container for the rock obstacle logic.
 * @constructor Creates the rock obstacle logic loading the frames images and the character size.
 * @param [spriteSheet] Bitmap with all character frames.
 * @param [w] Individual character width in pixels.
 * @param [h] Individual character height in pixels.
 * @param [numberOfFrames] Number of frames.
 * @param [scoreWeight] Define the obstacle score.
 * @param [delay] Character animation delay.  Default=10.
 * @throws [IllegalArgumentException] If [numberOfFrames], [w], [h] or [delay] is negative or zero.
 */
class Rock(private val spriteSheet: Bitmap, private val w: Int, private val h: Int,
           private val numberOfFrames: Int, private val scoreWeight: Int, private val delay: Int = 100) :
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

    /**
     * Save the obstacle sprite animation characteristics.
     */
    private var ac: AnimationClass

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
        // Validate the parameters
        if (w < 1) throw IllegalArgumentException("The character width must be higher than zero: w=%s".format(w)) else objWidth = w
        if (h < 1) throw IllegalArgumentException("The character height must be higher than zero: h=%s".format(h)) else objHeight = h
        if (numberOfFrames < 1) throw IllegalArgumentException("The number of frames must be higher than zero: numberOfFrames=%d".format(numberOfFrames))

        hurlsRock()

        // Initialize the character sprite animation.
        ac = AnimationClass(spriteSheet, w, h, numberOfFrames, false)
        ac.delay = delay

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
            canvas!!.drawBitmap(ac.getBitmap, xc.toFloat(), yc.toFloat(), null)
        } catch (e: Exception) {
            println("ERROR WHILE DRAWING THE ROCK: ${e.message}")
        }
    }

    /**
     * Identifies this class to help on debug.
     * @return Informs the sprite animation status.
     */
    override fun toString(): String {
        return "scoreWeight=$scoreWeight - speed=$speed - numberOfFrames:$numberOfFrames - ${super.toString()}"
    }
}
