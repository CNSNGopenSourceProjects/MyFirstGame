package br.com.conseng.myfirstgame

/**************************************************************************************************
 * Histírico da implementação:
 * 20180106     F.Camargo       Implementada a carga do fundo e seu deslocamento.
 * 20180107     F.Camargo       Acrescentado um jogador que anda sembre na mesma posição.
 * 20180107     F.Camargo       Acréscimo de obstáculos e prêmios.  Jogador pula quando tela tocada.
 * 20180108     F.Camargo       Acréscimo da lógica de interferência para os obstáculos.
 *                              Prevê multiplas rochas sendo lançadas no jogo.
 *                              Evita loop infinito no surfaceDestroyed().
 *                              Implementa a lógica de colisão.
 * 20180115     F.Camargo       Acrescenta texto na tela.
 * 20180119     F.Camargo       Sincroniza com a implementação do livro, acrescentando as bordas.
 *                              A lógica teve que ser revista para ficar compatível com o livro.
 **************************************************************************************************/

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*
import kotlin.math.max


/**
 * Largura da superfície do jogo.
 */
const val GAME_SURFACE_WIDTH = 1920
/**
 * Altura da superfície do jogo.
 */
const val GAME_SURFACE_HEIGHT = 1080
/**
 * Default displacement for background scrooling.
 */
const val GAME_MOVING_SPEED = -5

/**
 * Implements the game logic.
 * @param [context] Address the screen where the game is running.
 * @constructor Controls the game logic.
 */
class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    /**
     * Saves the game thread.
     */
    private var mainThread: MainGameThread? = null

    /**
     * Saves the game background image managing object.
     */
    private var bgImg: BackgroundImage = BackgroundImage(BitmapFactory.decodeResource(resources, R.drawable.background_image))

    /**
     * Saves the 3 frames to animate the player character frames.
     */
    private val playerFrames = SpriteFrames(resources, R.drawable.player_run, 1, 3)

    /**
     * Container for the player character object.
     */
    private var playerCharacter: PlayerCharacter? = null

    /**
     * Saves the 3 frames to animate all rocks.
     */
    private val rockFrames = SpriteFrames(resources, R.drawable.rock, 3, 1)

    /**
     * Saves all rocks created in the game.
     */
    private val rocks = ArrayList<Rock>()

    /**
     * Saves the 25 frames to animate the explosion.
     */
    private val explosionFrames = SpriteFrames(resources, R.drawable.explosion, 5, 5)

    /**
     * Container for the explosion object.
     */
    private var explosionEffect: ExplosionEffect? = null

    /**
     * Saves the upper boundary created in the game.
     */
    private val upperBoundary = ArrayList<UpperBoundary>()

    /**
     * Adjust the upper boundary height when a segment leave the screen and a new one must be created.
     */
    private var upBound = true

    /**
     * Saves the lower boundary created in the game.
     */
    private val lowerBoundary = ArrayList<LowerBoundary>()

    /**
     * Adjust the lower boundary height when a segment leave the screen and a new one must be created.
     */
    private var lowBound = true

    /**
     * Minimum height used on upper and lower boundaries.
     */
    private var minBoundaryHeight = 0

    /**
     * Maximum height used on upper and lower boundaries.
     */
    private var maxBoundaryHeight = 0

    /**
     * Saves the highest score in the game.
     */
    private var bestScore = 0

    /**
     * Parameter used on computation of the random height of the boundaries.
     */
    private var progressDenom = 20

    /**
     * Saves the timestamp when the game was over.
     */
    private var startReset: Long = 0

    /**
     * Flag indicating when the user request to play again (true).
     */
    private var newGameCreated = false
    /**
     * Flag indicating a new game must be started (true) by user request.
     */
    private var reset = false
    /**
     * Flag indicating the game was running (true).
     */
    private var started = false

    /**
     * Start the game.
     */
    init {
        // Set callback to the surfaceholder to track events
        holder.addCallback(this)

        // Make gamePanel focusable so it can handle events
        isFocusable = true
    }

    /**
     * This is called immediately after any structural changes (format or size) have been made
     * to the surface. You should at this point update the imagery in the surface.
     * @param [holder] The SurfaceHolder whose surface has changed.
     * @param [format] The new PixelFormat of the surface.
     * @param [width] The new width of the surface.
     * @param [height] The new height of the surface.
     * @see [https://developer.android.com/reference/android/view/SurfaceHolder.Callback.html#surfaceDestroyed(android.view.SurfaceHolder)]
     */
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        // Nothing to do, at this moment.
    }

    /**
     * Waitng for the game thread to stop before destroy the surface.
     * @param [holder] The SurfaceHolder whose surface is being destroyed.
     * @see [https://developer.android.com/reference/android/view/SurfaceHolder.Callback.html#surfaceDestroyed(android.view.SurfaceHolder)]
     **/
    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        var retry = true
        var counter = 1000

        while (retry and (counter > 0)) {
            counter--                   // Avoid infinite loop
            try {
                mainThread!!.running = false
                mainThread!!.join()
                retry = false
                mainThread = null
            } catch (e: InterruptedException) {
                println("ERROR WHILE TRYING TO END GAME")
                e.printStackTrace()
            }
        }
    }

    /**
     * Load the background image and define the game scroll displacement, before start the game.
     * @param [holder] The SurfaceHolder whose surface is being created.
     * @see [https://developer.android.com/reference/android/view/SurfaceHolder.Callback.html#surfaceCreated(android.view.SurfaceHolder)]
     */
    override fun surfaceCreated(holder: SurfaceHolder?) {
        // Load the player character in the game
        val ac1 = AnimationClass(playerFrames)
        playerCharacter = PlayerCharacter(ac1)
        // Add the explosion to end the game
        val ac2 = AnimationClass(explosionFrames)
        explosionEffect = ExplosionEffect(ac2)
        // Start the game thread
        mainThread = MainGameThread(holder!!, this)
        mainThread!!.running = true
        mainThread!!.start()
    }

    /**
     * While touching the screen (ACTION_DOWN) the player character must go up.
     * Finishing the touch, the player character must go down..
     * @param [event] The MotionEvent object containing full information about the event.
     * @return While the listener is consuming the event returns 'true', otherwise 'false'.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {                    // Touching the screen
                if (playerCharacter!!.playing) {                // Is playing?
                    playerCharacter!!.up = true                 // YES, move the player up
                    if (!started) started = true
                    reset = false
                } else if (newGameCreated and reset) {          // Game starting?
                    playerCharacter!!.playing = true            // YES, run
                    playerCharacter!!.up = true
                }
                return true
            }
            MotionEvent.ACTION_UP -> {                      // Stop touching the screen
                playerCharacter!!.up = false                    // The player must go down
                return true
            }
            else -> {                                       // Any other event will be handled by super
                return super.onTouchEvent(event)
            }
        }
    }

    /**
     * Update the background image drawing.
     */
    fun update() {
        if (playerCharacter!!.playing) {
            // Stop playing if the game boundaries have not been set
            if (lowerBoundary.isEmpty() or upperBoundary.isEmpty()) {
                playerCharacter!!.playing = false
                return
            }
            // Update the game scenarious
            bgImg.update()
            playerCharacter!!.update()
            // Update the game boundaries
            updateUpperBound()
            updateLowerBound()
            // Update the boundaries limit
            maxBoundaryHeight = 30 + (playerCharacter!!.score / progressDenom)
            if (maxBoundaryHeight > (GAME_SURFACE_HEIGHT / 4)) maxBoundaryHeight = GAME_SURFACE_HEIGHT / 4
            minBoundaryHeight = 5 + (playerCharacter!!.score / progressDenom)
            if (minBoundaryHeight > (GAME_SURFACE_HEIGHT / 8)) minBoundaryHeight = GAME_SURFACE_HEIGHT / 8
            // On collision with the boundaries, the game will stop
            for (i in lowerBoundary.indices) {
                if (collision(lowerBoundary[i], playerCharacter!!)) {
                    playerCharacter!!.playing = false
                    return
                }
            }
            for (i in upperBoundary.indices) {
                if (collision(upperBoundary[i], playerCharacter!!)) {
                    playerCharacter!!.playing = false
                    return
                }
            }
            // Spawn rocks on screen.  On collision with any rock, the game will stop
            for (i in rocks.indices) {
                rocks[i].update(minBoundaryHeight)
                if (collision(rocks[i], playerCharacter!!)) {
                    playerCharacter!!.playing = false
                    rocks.removeAt(i)               // Remove the rock that hit the player
                    return
                }
            }
            if (playerCharacter!!.playing and
                    (rocks.isEmpty() or
                            (rocks[rocks.size - 1].rockElapsed > minimumRockInterval))) {
                addNewRock()            // Add new rocks at specific intervals
            }
        } else {                            // The game is over
            playerCharacter!!.resetDYC()
            if (!reset) {                           // The game just ended?
                reset = true                        // YES, shows the explosion on player character
                newGameCreated = false
                startReset = System.nanoTime()
                explosionEffect!!.doExplosion(playerCharacter!!.xc, playerCharacter!!.yc)
            }
            explosionEffect!!.update()
            val elapsed = (System.nanoTime() - startReset) / 1000000
            if (!newGameCreated and (elapsed > 10000)) {        // Restart the game after 10 seconds
                newGame()
            }
        }
    }

    /**
     * Check the a collision between two game objects using the algorithm of the Bounding Box Collision.
     * @param [a] The position of one object on the screen.
     * @param [b] The position of another object on the screen.
     * @return 'true´ if collided.
     */
    private fun collision(a: GameObj, b: GameObj): Boolean = Rect.intersects(a.getRectangle(), b.getRectangle())

    /**
     * Maximum number of rocks that will be in the game.
     */
    private val maximumNumberOfRocks = 5

    /**
     * Minimum interval to add a new rock to the game.
     */
    private val minimumRockInterval = 5000

    /**
     * Add a new rock in the game if the number of active rocks is not higher than [maximumNumberOfRocks].
     */
    private fun addNewRock() {
        if (rocks.size < maximumNumberOfRocks) {
            // Load the rock obstacle in the game
            val ac = AnimationClass(rockFrames)
            rocks.add(Rock(ac, 100))
        }
    }

    /**
     * Render the surfice, scalling the backgroud image to the game surface area.
     * @param [canvas] The Canvas to which the View is rendered.
     * @see [https://developer.android.com/reference/android/view/SurfaceView.html#draw(android.graphics.Canvas)]
     * @since The superclass MUST be called.
     */
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)      // IT IS A MUST - I ADDED IT
        val scaleFactorX: Float = width.toFloat() / GAME_SURFACE_WIDTH.toFloat()
        val scaleFactorY: Float = height.toFloat() / GAME_SURFACE_HEIGHT.toFloat()

        if (null != canvas) {
            val savedState = canvas.save()
            canvas.scale(scaleFactorX, scaleFactorY)
            // Draw the background and the player character
            bgImg.draw(canvas)
            playerCharacter!!.draw(canvas)
            // Draw all rocks
            for (rock in rocks) {
                rock.draw(canvas)
            }
            // Draw the upper boundary
            for (ub in upperBoundary) {
                ub.draw(canvas)
            }
            // Draw the lower boundary
            for (lb in lowerBoundary) {
                lb.draw(canvas)
            }
            // Draw the explosion
            if (started) explosionEffect!!.draw(canvas)
            // Write the game score on the screen
            drawText(canvas)
            // Update the screen
            canvas.restoreToCount(savedState)
        }

    }

    /**
     * Necessary to randomize the boundary height.
     */
    private var rnd = Random()

    /**
     * Default width of each boundary segment.
     */
    private val defaultBoundaryWidth = 20

    /**
     * Load the upper boundary array to restrict the player character moving area.
     * Increase the boundary height considering the modulus 50 of the score.
     */
    private fun updateUpperBound() {
        if (upperBoundary.isEmpty() or                          // Create the first segment
                (playerCharacter!!.score % 50 == 0)) {        // Add a new segment periodically, based on score
            val x = if (upperBoundary.isEmpty()) 0 else upperBoundary[upperBoundary.size - 1].xc + defaultBoundaryWidth
            addNewUpperBoundary(x, 0)
        }

        for (i in upperBoundary.indices) {
            upperBoundary[i].update()
            if (upperBoundary[i].xc < -defaultBoundaryWidth) {
                // Remove the segment out of the screen
                upperBoundary.removeAt(i)
                // Append a new segment at the end of the boundary
                var h = 0
                var x = 0
                when {
                    upperBoundary.isEmpty() -> {
                        upBound = !upBound
                    }
                    upperBoundary[upperBoundary.size - 1].objHeight >= maxBoundaryHeight -> {
                        upBound = false
                        h = upperBoundary[upperBoundary.size - 1].objHeight - 1
                        x = upperBoundary[upperBoundary.size - 1].xc + defaultBoundaryWidth
                    }
                    lowerBoundary[upperBoundary.size - 1].objHeight <= minBoundaryHeight -> {
                        upBound = true
                        h = upperBoundary[upperBoundary.size - 1].objHeight + 1
                        x = upperBoundary[upperBoundary.size - 1].xc + defaultBoundaryWidth
                    }
                    else -> {
                        h = if (upBound) {
                            lowerBoundary[upperBoundary.size - 1].objHeight + 1
                        } else {
                            upperBoundary[upperBoundary.size - 1].objHeight - 1
                        }
                        x = upperBoundary[upperBoundary.size - 1].xc + defaultBoundaryWidth
                    }
                }
                addNewUpperBoundary(x, h)
            }
        }
    }

    /**
     * Add a new upper boundary segment into the array.
     * @param [x] The segment position on X-axis.
     * @param [height] The segment height. If zero, compute a random height.
     */
    private fun addNewUpperBoundary(x: Int, height: Int = 0) {
        val h = if (height > 0) height else max(minBoundaryHeight, (rnd.nextDouble() * maxBoundaryHeight.toDouble() + 1).toInt())
        val boundaryFrames = SpriteFrames(resources, R.drawable.ground, 1, 1, h, defaultBoundaryWidth)
        val ac = AnimationClass(boundaryFrames)
        val newBoundary = UpperBoundary(ac, x, 0)
        upperBoundary.add(newBoundary)
    }

    /**
     * Load the lower boundary array to restrict the player character moving area.
     * Increase the boundary height considering the modulus 40 of the score.
     */
    private fun updateLowerBound() {
        if (lowerBoundary.isEmpty() or                          // Create the first segment
                (playerCharacter!!.score % 40 == 0)) {          // Add a new segment periodically, based on score
            val x = if (lowerBoundary.isEmpty()) 0 else lowerBoundary[lowerBoundary.size - 1].xc + defaultBoundaryWidth
            addNewLowerBoundary(x, 0)
        }

        for (i in lowerBoundary.indices) {
            lowerBoundary[i].update()
            if (lowerBoundary[i].xc < -defaultBoundaryWidth) {
                // Remove the segment out of the screen
                lowerBoundary.removeAt(i)
                // Append a new segment at the end of the boundary
                var h = 0
                var x = 0
                when {
                    lowerBoundary.isEmpty() -> {
                        lowBound = !lowBound
                    }
                    lowerBoundary[lowerBoundary.size - 1].objHeight >= maxBoundaryHeight -> {
                        lowBound = false
                        h = lowerBoundary[lowerBoundary.size - 1].objHeight - 1
                        x = lowerBoundary[upperBoundary.size - 1].xc + defaultBoundaryWidth
                    }
                    lowerBoundary[lowerBoundary.size - 1].objHeight <= minBoundaryHeight -> {
                        lowBound = true
                        h = lowerBoundary[lowerBoundary.size - 1].objHeight + 1
                        x = lowerBoundary[upperBoundary.size - 1].xc + defaultBoundaryWidth
                    }
                    else -> {
                        h = if (lowBound) {
                            lowerBoundary[lowerBoundary.size - 1].objHeight + 1
                        } else {
                            lowerBoundary[lowerBoundary.size - 1].objHeight - 1
                        }
                        x = lowerBoundary[upperBoundary.size - 1].xc + defaultBoundaryWidth
                    }
                }
                addNewLowerBoundary(x, h)
            }
        }
    }

    /**
     * Add a new lower boundary segment into the array.
     * @param [x] The segment position on X-axis.
     * @param [height] The segment height. If zero, compute a random height.
     */
    private fun addNewLowerBoundary(x: Int, height: Int = 0) {
        val h = if (height > 0) height else max(minBoundaryHeight, (rnd.nextDouble() * maxBoundaryHeight.toDouble() + 1).toInt())
        val boundaryFrames = SpriteFrames(resources, R.drawable.ground, 1, 1, h, defaultBoundaryWidth)
        val ac = AnimationClass(boundaryFrames)
        val newBoundary = LowerBoundary(ac, x, GAME_SURFACE_HEIGHT - h)
        lowerBoundary.add(newBoundary)
    }

    /**
     * Start a new game.
     * Update the best score before clear the players score for the new run.
     */
    private fun newGame() {
        // Purge all arrays for the new round
        rocks.clear()
        lowerBoundary.clear()
        upperBoundary.clear()
        minBoundaryHeight = 5
        maxBoundaryHeight = 30
        // Saves the best score before clear the score for a new round.
        if (playerCharacter!!.score > bestScore) {
            bestScore = playerCharacter!!.score
        }
        playerCharacter!!.resetScore()
        playerCharacter!!.resetDYC()
        playerCharacter!!.yc = GAME_SURFACE_HEIGHT / 2
        // Initialize the boundaries
        var x = 0
        var h = minBoundaryHeight
        while (x < (GAME_SURFACE_WIDTH) + defaultBoundaryWidth) {
            addNewUpperBoundary(x, h)
            addNewLowerBoundary(x, h)
            x += defaultBoundaryWidth
            h = 0
        }
        // Start the new round
        newGameCreated = true
    }

    /**
     * Write the score and game instructions on screen.
     * @param [canvas] The Canvas to which the View is rendered.
     */
    private fun drawText(canvas: Canvas) {
        val p = Paint()
        p.color = Color.BLACK
        p.textSize = 30f
        p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        // Write the current score
        canvas.drawText("DISTANCE: %d".format(3 * playerCharacter!!.score),
                10.toFloat(), (GAME_SURFACE_HEIGHT - 10).toFloat(), p)
        canvas.drawText("BEST: %d".format(bestScore),
                (GAME_SURFACE_WIDTH - 215).toFloat(), (GAME_SURFACE_HEIGHT - 10).toFloat(), p)
        // Show the game tutorial on the screen if the game is not running
        if (!playerCharacter!!.playing and newGameCreated and reset) {
            val p1 = Paint()
            //p1.color = Color.BLACK
            p1.textSize = 40f
            p1.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("TAP TO START ON SCREEN",
                    (GAME_SURFACE_WIDTH / 2 - 50).toFloat(), (GAME_SURFACE_HEIGHT / 2).toFloat(), p1)
            canvas.drawText("KEEP PRESSED TO GO UP",
                    (GAME_SURFACE_WIDTH / 2 - 50).toFloat(), (GAME_SURFACE_HEIGHT / 2 + 20).toFloat(), p1)
            canvas.drawText("RELEASE TO GO DOWN",
                    (GAME_SURFACE_WIDTH / 2 - 50).toFloat(), (GAME_SURFACE_HEIGHT / 2 + 40).toFloat(), p1)
        }
    }

    /**
     * Identifies this class to help on debug.
     * @return The number of rocks and boundary segments.
     */
    override fun toString(): String {
        val thread = if (null == mainThread) "NONE" else if (mainThread!!.running) "RUNNING" else "STOPPED"
        return "boundary=${lowerBoundary.size} - rocks=${rocks.size} - status:$thread"
    }
}