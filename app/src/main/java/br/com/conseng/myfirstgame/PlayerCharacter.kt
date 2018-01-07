package br.com.conseng.myfirstgame

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * Container for the player character logic.
 * @constructor Creates the player character logic loading the frames images and the character size.
 * @param [spriteSheet] Bitmap with all character frames.
 * @param [w] Individual character width in pixels.
 * @param [h] Individual character height in pixels.
 * @param [numberOfFrames] Number of frames.
 * @param [delay] Character animation delay.  Default=10.
 * @throws [IllegalArgumentException] If [numberOfFrames], [w], [h] or [delay] is negative or zero.
 */
class PlayerCharacter(private val spriteSheet: Bitmap, private val w: Int, private val h: Int, private val numberOfFrames: Int, private val delay: Int = 10) :
        GameObj() {
    /**
     * TODO: o que faz isso?
     */
    private var dya: Double = 0.0

    /**
     * Reset the DYA.
     */
    fun resetDYA() {
        dya = 0.0
    }

    /**
     * Defines if the player is active or not.
     */
    var playing: Boolean = false

    /**
     * Character score.
     */
    var score: Int = 0
        private set

    /**
     * Reset the score.
     */
    fun resetScore() {
        score = 0
    }

    /**
     * Save the player sprite animation characteristics.
     */
    private var ac: AnimationClass

    /**
     * Saves the initial time for score logic.
     */
    private var startTime: Long = 0

    /**
     * The player will jump when 'true'.
     */
    var up: Boolean = false

    /**
     * Initialize the character parameters.
     */
    init {
        // Validate the parameters
        if (w < 1) throw IllegalArgumentException("The character width must be higher than zero: w=%s".format(w))
        if (h < 1) throw IllegalArgumentException("The character height must be higher than zero: h=%s".format(h))
        if (numberOfFrames < 1) throw IllegalArgumentException("The number of frames must be higher than zero: numberOfFrames=%d".format(numberOfFrames))

        // Set the initial position of the character and define no moviment on y axis.
        xc = 100
        yc = GAME_SURFACE_HEIGHT / 2
        dyc = 0

        // Saves the character 2D size.
        objHeight = h
        objWidth = w

        // Initialize the character sprite animation.
        ac = AnimationClass(spriteSheet, w, h, numberOfFrames)
        ac.delay = delay

        startTime = System.nanoTime()
    }

    /**
     * Create a loop of events that assign a score to the player and keep the player between the
     * upper and lower bounds.
     */
    fun update() {
        val elapsed = (System.nanoTime() - startTime) / 1000000
        if (elapsed > 100) {
            score++
            startTime = System.nanoTime()
        }
        ac.update()
    }

    /**
     * Render the player character using the current animation getBitmap.
     * @param [canvas] The Canvas to which the View is rendered.
     * @see [https://developer.android.com/reference/android/view/SurfaceView.html#draw(android.graphics.Canvas)]
     * @since The superclass MUST be called.
     */
    fun draw(canvas: Canvas?) {
        canvas!!.drawBitmap(ac.getBitmap, xc.toFloat(), yc.toFloat(), null)
    }

    /**
     * Identifies this class to help on debug.
     * @return Informs the sprite animation status.
     */
    override fun toString(): String {
        return "score=$score - delay=$delay - numberOfFrames:$numberOfFrames - ${super.toString()}"
    }
}
