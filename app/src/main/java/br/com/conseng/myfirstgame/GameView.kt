package br.com.conseng.myfirstgame

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
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
     * Load the background image and define the game scroll displacement, before start the game.
     * @param [holder] The SurfaceHolder whose surface is being created.
     * @see [https://developer.android.com/reference/android/view/SurfaceHolder.Callback.html#surfaceCreated(android.view.SurfaceHolder)]
     */
    override fun surfaceCreated(holder: SurfaceHolder?) {
        bgImg = BackgroundImage(BitmapFactory.decodeResource(resources, R.drawable.background_image))
        bgImg!!.setVector(-5)
        // We can safely start the game loop
        mainThread!!.running = true
        mainThread!!.start()
    }

//    /**
//     * Called when a touch event is dispatched to the screen.
//     * @param [event] The MotionEvent object containing full information about the event.
//     * @return "True", if the listener has consumed the event, false otherwise.
//     */
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return super.onTouchEvent(event)
//    }

    /**
     * Render the surfice, scalling the backgroud image to the game surface area.
     * @param [canvas] The Canvas to which the View is rendered.
     * @see [https://developer.android.com/reference/android/view/SurfaceView.html#draw(android.graphics.Canvas)]
     * @since The superclass MUST be called.
     */
    override fun draw(canvas: Canvas?) {
        val scaleFactorX: Float = width.toFloat() / GAME_SURFACE_WIDTH.toFloat()
        val scaleFactorY: Float = height.toFloat() / GAME_SURFACE_HEIGHT.toFloat()
        if ((null != canvas) and (null != bgImg)) {
            val savedState = canvas!!.save()
            canvas.scale(scaleFactorX, scaleFactorY)
            bgImg!!.draw(canvas)
            canvas.restoreToCount(savedState)
        }
        super.draw(canvas)      // IT IS A MUST
    }

    /**
     * Update the background image drawing.
     */
    fun update() {
        bgImg!!.update()
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