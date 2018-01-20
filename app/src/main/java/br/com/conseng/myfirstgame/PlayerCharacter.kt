package br.com.conseng.myfirstgame

/**************************************************************************************************
 * Histírico da implementação:
 * 20180107     F.Camargo       Acrescentado um jogador que anda sembre na mesma posição.
 * 20180107     F.Camargo       Acréscimo de obstáculos e prêmios.  Jogador pula quando tela tocada.
 *                              Removida a variável dya e colocado limite vertical de movimentação.
 **************************************************************************************************/

import android.graphics.Canvas

/**
 * Container for the player character logic.
 * @constructor Creates the player character logic loading the frames images and the character size.
 * @param [ac] The player sprite animation characteristics.
 * @param [delay] Character animation delay.  Default=10.
 * @throws [IllegalArgumentException] If [delay] is negative or zero.
 */
class PlayerCharacter(private val ac: AnimationClass, private val delay: Int = 10) :
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
     * Initialize the player character displacement.
     */
    fun resetDYC() {
        dyc = 0
    }

    /**
     * Saves the initial time for score logic.
     */
    private var startTime: Long =  System.currentTimeMillis()

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
        if (delay < 1) throw IllegalArgumentException("The delay must be higher than zero: delay=%d".format(delay))
        // Set the initial position of the character and define no movement on y axis.
        xc = getInitialX
        yc = getInitialY
        dyc = 0
        // Initialize the character sprite animation.
        ac.delay = delay
        objWidth = ac.frameWidth
        objHeight = ac.frameHeight
    }

    /**
     * Create a loop of events that assign a score to the player and keep the player between the
     * upper and lower bounds.
     */
    fun update() {
        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed > 100) {
            score++
            startTime = System.currentTimeMillis()
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
     */
    fun draw(canvas: Canvas?) {
        try {
            canvas!!.drawBitmap(ac.getBitmap, floatXc, floatYc, null)
        } catch (e: Exception) {
            println("ERROR WHILE DRAWING THE PLAYER: ${e.message}")
        }
    }

    /**
     * Identifies this class to help on debug.
     * @return Informs the sprite animation status.
     */
    override fun toString(): String {
        return "playing=${playing} - score=$score - delay=$delay - ${super.toString()}"
    }
}
