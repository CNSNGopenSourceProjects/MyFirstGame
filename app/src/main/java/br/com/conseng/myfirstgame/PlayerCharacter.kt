package br.com.conseng.myfirstgame

/**************************************************************************************************
 * Histírico da implementação:
 * 20180107     F.Camargo       Acrescentado um jogador que anda sembre na mesma posição.
 * 20180107     F.Camargo       Acréscimo de obstáculos e prêmios.  Jogador pula quando tela tocada.
 *                              Removida a variável dya e colocado limite vertical de movimentação.
 **************************************************************************************************/

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
class PlayerCharacter(private val spriteSheet: Bitmap, private val w: Int, private val h: Int,
                      private val numberOfFrames: Int, private val delay: Int = 10) :
        GameObj() {
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
     * While 'true', the player must go up to jump obstacles.
     * While 'false', the player must be walking.
     */
    var up: Boolean = false

    // Define the initial coordinates of the player character.
    private val getInitialX = 100
    private val getInitialY = GAME_SURFACE_HEIGHT / 2

    /**
     * Initialize the character parameters.
     */
    init {
        // Validate the parameters
        if (w < 1) throw IllegalArgumentException("The character width must be higher than zero: w=%s".format(w)) else objWidth = w
        if (h < 1) throw IllegalArgumentException("The character height must be higher than zero: h=%s".format(h)) else objHeight = h
        if (numberOfFrames < 1) throw IllegalArgumentException("The number of frames must be higher than zero: numberOfFrames=%d".format(numberOfFrames))

        // Initialize the character sprite animation.
        // Set the initial position of the character and define no moviment on y axis.
        xc = getInitialX
        yc = getInitialY
        dyc = 0

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

        // Control the player jumping, using the acceleration factor the boundaries.
        dyc = if (up) dyc - 2 else dyc + 2
        if (dyc >= 20) dyc = 20 else if (dyc <= -20) dyc = -20
        yc += dyc
        if (yc < 0) yc = 0 else if (yc >= (GAME_SURFACE_HEIGHT - objHeight)) yc = GAME_SURFACE_HEIGHT - objHeight
    }

    /**
     * Render the player character using the current animation getBitmap.
     * @param [canvas] The Canvas to which the View is rendered.
     * @see [https://developer.android.com/reference/android/view/SurfaceView.html#draw(android.graphics.Canvas)]
     * @since The superclass MUST be called.
     */
    fun draw(canvas: Canvas?) {
        try {
            canvas!!.drawBitmap(ac.getBitmap, xc.toFloat(), yc.toFloat(), null)
        } catch (e: Exception) {
            println("ERROR WHILE DRAWING THE PLAYER: ${e.message}")
        }
    }

    /**
     * Identifies this class to help on debug.
     * @return Informs the sprite animation status.
     */
    override fun toString(): String {
        return "score=$score - delay=$delay - numberOfFrames:$numberOfFrames - ${super.toString()}"
    }
}
