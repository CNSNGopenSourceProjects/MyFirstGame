package br.com.conseng.myfirstgame

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * Handle the background image on the game, continuously scrolling through our view.
 * @param [backgroundImage] The game background image.
 * @constructor Load the background image to be used in the game.
 */
class BackgroundImage(private val backgroundImage: Bitmap) {
    /**
     * Current scrolling position of the background image on X axis.
     */
    private var xc: Int = 0
    /**
     * Current scrolling position of the background image on Y axis.
     */
    private var yc: Int = 0

    /**
     * Save the displacement to be used on the background image scrolling in x axis.
     * =0, means no scrolling.
     */
    private var dxc: Int = GAME_MOVING_SPEED

//    /**
//     * Define the displacement to be used to scroll the background image in x axis.
//     * @param[dxc] The new displacement in x axis.
//     */
//    fun setVector(dxc: Int) {
//        this.dxc = dxc
//    }

    /**
     * Scroll the background image by the displacement defined by [setVector].
     * If the background image goes out of screen, reset the position in order to give a
     * continuous movement effect.
     */
    fun update() {
        xc += dxc
        if (xc < -GAME_SURFACE_WIDTH) {
            xc = 0
        }
    }

    /**
     * Update the backgroung image position on screen.
     * If the background image goes out of screen, draw the image twice to avoid any black and
     * to give a continuous loop effet.
     * @param [canvas] Screen view of the game.
     */
    fun draw(canvas: Canvas) {
        canvas.drawBitmap(backgroundImage, xc.toFloat(), yc.toFloat(), null)
        if (xc < 0) {
            canvas.drawBitmap(backgroundImage, (xc + GAME_SURFACE_WIDTH).toFloat(), yc.toFloat(), null)
        }
    }

    /**
     * Identifies this class to help on debug.
     * @return The current [x,y] coordinate and the dx displacement.
     */
    override fun toString(): String {
        return "[x,y]=[$xc,$yc] - displacement:$dxc"
    }
}