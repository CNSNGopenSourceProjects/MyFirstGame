package br.com.conseng.myfirstgame

/**************************************************************************************************
 * Histírico da implementação:
 * 20180106     F.Camargo       Implementada a carga do fundo e seu deslocamento.
 * 20180107     F.Camargo       Acrescentado um jogador que anda sembre na mesma posição.
 * 20180107     F.Camargo       Acréscimo de obstáculos e prêmios.  Jogador pula quando tela tocada.
 **************************************************************************************************/

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

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
    private var bgImg: BackgroundImage? = null

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
        while (retry) {
            try {
                mainThread!!.running = false
                mainThread!!.join()
            } catch (e: InterruptedException) {
                println("ERROR WHILE TRYING TO END GAME")
                e.printStackTrace()
            }
            retry = false
        }
    }

    /**
     * Container for the playeer character object.
     */
    private var playerCharacter: PlayerCharacter? = null

    /**
     * Load the background image and define the game scroll displacement, before start the game.
     * @param [holder] The SurfaceHolder whose surface is being created.
     * @see [https://developer.android.com/reference/android/view/SurfaceHolder.Callback.html#surfaceCreated(android.view.SurfaceHolder)]
     */
    override fun surfaceCreated(holder: SurfaceHolder?) {
        bgImg = BackgroundImage(BitmapFactory.decodeResource(resources, R.drawable.background_image))
        // Load the player character on game
        val d = resources.getDrawable(R.drawable.player_run)
        val w = d.intrinsicWidth
        val h = d.intrinsicHeight
        playerCharacter = PlayerCharacter(BitmapFactory.decodeResource(resources, R.drawable.player_run), w / 3, h, 3)
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

        if ((null != canvas) and (null != bgImg)) {
            val savedState = canvas!!.save()
            canvas.scale(scaleFactorX, scaleFactorY)
            bgImg!!.draw(canvas)
            playerCharacter!!.draw(canvas)
            canvas.restoreToCount(savedState)
        }
    }

    /**
     * Update the background image drawing.
     */
    fun update() {
        bgImg!!.update()
        playerCharacter!!.update()
    }

    /**
     * Identifies this class to help on debug.
     * @return The current [x,y] coordinate and the dx displacement.
     */
    override fun toString(): String {
        val backgroung = if (null == bgImg) "NONE" else "LOADED"
        val thread = if (null == mainThread) "NONE" else if (mainThread!!.running) "RUNNING" else "STOPPED"
        return "background=$backgroung - status:$thread"
    }
}