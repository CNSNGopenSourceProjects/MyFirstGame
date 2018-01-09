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
 **************************************************************************************************/

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import java.util.*

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
     * Container for the player character frames.
     */
    private val playerFrames = SpriteFrames(resources, R.drawable.player_run, 1, 3)

    /**
     * Container for the rock frames to be shared by various rocks.
     */
    private val rockFrames = SpriteFrames(resources, R.drawable.rock, 3, 1)

    /**
     * Container for the explosion.
     */
    private val explosionFrames = SpriteFrames(resources, R.drawable.explosion, 5, 5)

    /**
     * Start the game.
     */
    init {
        // Set callback to the surfaceholder to track events
        holder.addCallback(this)
        mainThread = MainGameThread(holder, this)

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
            } catch (e: InterruptedException) {
                println("ERROR WHILE TRYING TO END GAME")
                e.printStackTrace()
            }
        }
    }

    /**
     * Container for the player character object.
     */
    private var playerCharacter: PlayerCharacter? = null

    /**
     * Saves all rocks created in the game.
     */
    private var rocks = ArrayList<Rock>()

    /**
     * Container for the explosion object.
     */
    private var blast: Explosion? = null

    /**
     * Load the background image and define the game scroll displacement, before start the game.
     * @param [holder] The SurfaceHolder whose surface is being created.
     * @see [https://developer.android.com/reference/android/view/SurfaceHolder.Callback.html#surfaceCreated(android.view.SurfaceHolder)]
     */
    override fun surfaceCreated(holder: SurfaceHolder?) {
//        bgImg = BackgroundImage(BitmapFactory.decodeResource(resources, R.drawable.background_image))
        // Load the player character in the game
        val ac1 = AnimationClass(playerFrames)
        playerCharacter = PlayerCharacter(ac1)
        // Add the first rock in the game
        addNewRock()
        // Add the explosion to end the game
        val ac2 = AnimationClass(explosionFrames)
        blast = Explosion(ac2)

//        // We can safely start the game loop
//        mainThread!!.running = true
//        mainThread!!.start()
    }

    /**
     * Start the game loop.
     */
    private fun startGame() {
        mainThread!!.running = true
        mainThread!!.start()
    }

    /**
     * While touching the screen (ACTION_DOWN) the player character must go up.
     * Finishing the touch, the player character must go down..
     * @param [event] The MotionEvent object containing full information about the event.
     * @return While the listener is consuming the event returns 'true', otherwise 'false'.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {                            // Touching the screen
                if (!mainThread!!.running) {                        // Is the game running?
                    startGame()                                     // NO, start the game
                    playerCharacter!!.playing = true
                } else {                                            // YES, jump the player character
                    playerCharacter!!.up = true
                }
                return true
            }
            MotionEvent.ACTION_UP -> {                              // Stop touching the screen
                playerCharacter!!.up = false                        // The player must go down
                return true
            }
            else -> {                                           // Any other event will be handled by super
                return super.onTouchEvent(event)
            }
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
            bgImg.draw(canvas)
            playerCharacter!!.draw(canvas)
            for (rock in rocks) {
                rock.draw(canvas)
            }
            if (explosion) blast!!.draw(canvas)
            canvas.restoreToCount(savedState)
        }
    }

    /**
     * Maximum number of rocks that will be in the game.
     */
    private val maximumNumberOfRocks = 10

    /**
     * Minimum interval to add a new rock to the game.
     */
    private val minimumRockInterval = 4000

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
     * Check the rock collision with the player character using the algorithm of the Bounding Box Collision.
     * @param [rock] The rock position on the screen.
     * @param [player] The player character position on the screen.
     * @return 'true´ if collided.
     */
    private fun collision(rock: Rock, player: PlayerCharacter): Boolean
            = Rect.intersects(rock.getRectangle(), player.getRectangle())

    /**
     * Must shown the explosion since the player hit a rock.
     */
    private var explosion = false

    /**
     * Update the background image drawing.
     */
    fun update() {
        if (playerCharacter!!.playing) {
            bgImg.update()
            playerCharacter!!.update()
            // Handle multiple rocks
            for (i in rocks.indices) {          // Stop the game on collision
                rocks[i].update()
                if (collision(rocks[i], playerCharacter!!)) {
                    playerCharacter!!.playing = false
                    explosion = true
                    blast!!.doExplosion(rocks[i].xc, rocks[i].yc)
                    rocks.removeAt(i)               // Remove the rock that hit the player
                    Toast.makeText(context, "Game over: you hit a rock!", Toast.LENGTH_LONG).show()
                    break
                }
            }
            if (!explosion and
                    (rocks.isEmpty() or
                            (rocks[rocks.size - 1].rockElapsed > minimumRockInterval))) {
                addNewRock()            // Add new rocks on secific intervals
            }
        } else if(explosion) {
            blast!!.update()
        }
    }

    /**
     * Identifies this class to help on debug.
     * @return The current [x,y] coordinate and the dx displacement.
     */
    override fun toString(): String {
//        val backgroung = if (null == bgImg) "NONE" else "LOADED"
        val thread = if (null == mainThread) "NONE" else if (mainThread!!.running) "RUNNING" else "STOPPED"
        return "rocks=${rocks.size} - status:$thread"
    }
}