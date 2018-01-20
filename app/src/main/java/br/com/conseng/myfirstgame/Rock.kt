package br.com.conseng.myfirstgame

/**************************************************************************************************
 * Histírico da implementação:
 * 20180108     F.Camargo       Acréscimo deste obstáculos: a rocha.  Acrescentada opção para auto
 *                              geração de rochas, sempre que esta ultrapassa a borda esquerda da tela.
 **************************************************************************************************/

import android.graphics.Canvas
import java.security.InvalidParameterException
import java.util.*
import kotlin.math.min

/**
 * Container for the rock obstacle logic.
 * @constructor Creates the rock obstacle logic loading the frames images and the character size.
 * @param [ac] The player sprite animation characteristics.
 * @param [scoreWeight] Define the obstacle score.
 * @param [autoPlay] If 'true', hurls a new rock when the rock reach the left side of the screen.
 * @param [delay] Character animation delay.  Default=10.
 * @throws [IllegalArgumentException] If [delay] is negative or zero.
 */
class Rock(private val ac: AnimationClass, private val scoreWeight: Int,
           val autoPlay: Boolean = true, private val delay: Int = 100) :
        GameObj() {

    /**
     * Goes 'true' when the rock goes out of the screen.
     */
    var out: Boolean = false
        private set

    /**
     * Necessary to randomize the rock behaviour.
     */
    private var rnd = Random()

    /**
     * Saves the rock speed.
     */
    private var speed: Int = 7

    /**
     * Informs the time this object has been created (ns).
     */
    private var startTime: Long = System.currentTimeMillis()
        private set

    /**
     * Informs how long the rock is on the screen (ms).
     */
    var rockElapsed: Long = 0
        get() = System.currentTimeMillis() - startTime
        private set

    /**
     * Initialize the character parameters, hurling the rock with random position and speed.
     */
    init {
        // Validate the parameters
        if (delay < 1) throw IllegalArgumentException("The delay must be higher than zero: delay=%d".format(delay))
        // Initialize the rock sprite animation.
        ac.delay = delay
        objWidth = ac.frameWidth
        objHeight = ac.frameHeight
        hurlsRock()
    }

    /**
     * Provides the initial rock position on X-axis.
     */
    private fun getInitialX(): Int = GAME_SURFACE_WIDTH //- objWidth

    /**
     * Provides the initial rock position on Y-axis.
     */
    private fun getInitialY(): Int = min(GAME_SURFACE_HEIGHT - objHeight, (rnd.nextDouble() * GAME_SURFACE_HEIGHT.toDouble()).toInt())

    /**
     * Provides the initial rock displacement on X-axis. Higher score = faster rock!
     */
    private fun getInitialDisplacementX(): Int {
        speed = min(35, (7 + (rnd.nextDouble() * scoreWeight.toDouble() / 30.0).toInt()))
        return -speed
    }

    /**
     * Provides the initial rock displacement on Y-axis.
     */
    private fun getInitialDisplacementY(): Int = min(20, (rnd.nextDouble() * 40.toDouble()).toInt() - 20)

    /**
     * Move the rock right to left.
     */
    private fun updateX() {
        this.xc += dxc
        this.out = xc <= -objWidth
    }

    /**
     * Move the rock on diagonal.
     * Reverse the direction when hit the boundary.
     * @param [boundaryHeight] Consider the boundary height on the rock bouncing.
     * @see [updateX] If a new rock started on right side, do not execute this method.
     */
    private fun updateY(boundaryHeight: Int) {
        if ((boundaryHeight < 0) or (boundaryHeight > (GAME_SURFACE_HEIGHT / 3)))
            throw InvalidParameterException("The boundary offset is out of the range: %d".format(boundaryHeight))

        val next = yc + dyc
        when {
            next > GAME_SURFACE_HEIGHT - objHeight - boundaryHeight -> {
                this.yc = GAME_SURFACE_HEIGHT - objHeight - boundaryHeight
                this.dyc = -dyc
            }
            next < boundaryHeight -> {
                this.yc = boundaryHeight
                this.dyc = -dyc
            }
            else -> this.yc = next
        }
    }

    /**
     * Moves the rock that left the screen to start again on screen right side.
     * Position (y-axis) and speed (x-axis) will be random.
     */
    private fun hurlsRock() {
        this.xc = getInitialX()
        this.yc = getInitialY()
        this.dxc = getInitialDisplacementX()
        this.dyc = getInitialDisplacementY()
        this.out = false
    }

    /**
     * Move the rock up and down inside the screen boundaries.
     * Move the rock right to left.
     * @param [boundaryHeight] Consider the boundary height on the rock bouncing.
     */
    fun update(boundaryHeight: Int) {
        updateX()
        if (autoPlay and out)               // Is on auto play mode?
            hurlsRock()                     // YES, move the rock to the right side-ending
        else
            updateY(boundaryHeight)         // NO, update the Y-axis position
        ac.update()
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
        return "scoreWeight=$scoreWeight - speed=$speed - numberOfFrames=${ac.numberOfFrames} - ${super.toString()}"
    }
}
