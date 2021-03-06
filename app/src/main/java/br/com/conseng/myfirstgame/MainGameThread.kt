package br.com.conseng.myfirstgame

import android.graphics.Canvas
import android.view.SurfaceHolder
import java.lang.Exception
import kotlin.math.max

/**
 * Calls the game drawing logic to update the image at specific getBitmap rate.
 * @param [surfaceHolder] To manipulate the screen pixel area by cavas.
 * @param [gameView] To draw each game getBitmap.
 * @constructor Controls the game image update refresh rate.
 */
class MainGameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {
    /**
     * Enables the game execution while "True".
     */
    var running: Boolean = false

    /**
     * The game getBitmap will be updated 30 times per second.
     */
    private val framesPerSecond: Int = 30

    /**
     * Informs the game average FPS.
     */
    private var averageFPS: Double = 0.0

    /**
     * Saves the current canvas used to draw the game images.
     */
    private var canvas: Canvas? = null

    /**
     * Run the game, getBitmap by getBitmap.
     */
    override fun run() {
        // Statistics variables
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        var totalTime: Long = 0
        var frameCount = 0

        val targetTime: Long = 1000 / framesPerSecond.toLong()

        println("STARTING THE MAIN GAME LOOP...")
        while (running) {
            startTime = System.nanoTime()
            canvas = null

            // Try locking the canvas for pixel editing
            try {
                canvas = this.surfaceHolder.lockCanvas()
                if (null == canvas)
                    println("GAME-THREAD WHILE FAIL TO LOCK THE CANVAS")
                else {
                    synchronized(surfaceHolder) {
                        this.gameView.update()
                        this.gameView.draw(canvas)
                    }
                }
            } catch (e: Exception) {
                println("GAME-THREAD EXCEPTION ON UPDATE OR DRAW: ${e.message}")
                e.printStackTrace()
            } finally {
                if (null != canvas) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        println("GAME-THREAD ERROR WHILE UNLOCKING CANVAS: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }

            // One getBitmap delay - compute the missing time
            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitTime = max(5, (targetTime - timeMillis))
            try {
                sleep(waitTime)
            } catch (e: Exception) {
                println("GAME-THREAD ERROR WHILE SLEEPING: ${e.message}")
            }

            // update the game statistics
            totalTime += System.nanoTime() - startTime
            frameCount++
            if (framesPerSecond == frameCount) {
                averageFPS = 1000 / ((totalTime.toDouble() / frameCount.toDouble()) / 1000000)
                frameCount = 0
                totalTime = 0
                println("averageFPS=$averageFPS")
            }
        }
        println("THE MAIN GAME LOOP HAS BEEN ENDED")

    }

    /**
     * Identifies this class to help on debug.
     * @return The game status and the average FPS.
     */
    override fun toString(): String {
        return "running:$running - averageFPS:$averageFPS"
    }
}