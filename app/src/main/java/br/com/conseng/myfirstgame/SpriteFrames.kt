package br.com.conseng.myfirstgame

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**************************************************************************************************
 * Histírico da implementação:
 * 20180108     F.Camargo       Criada classe para fatiar os frames dos caracteres.
 **************************************************************************************************/

/**
 * Extract the sprite frames from the image collection for sprite animation.
 * @constructor Get the image to be sliced from resource.
 * @param [res] The resources object containing the image data.
 * @param [id] The resource id of the image data
 * @param [rows] Number of rows on the image collection.
 *  param [columns] Number of columns on the image collection.
 * @throws [IllegalArgumentException] If [rows] or [columns] is lower than 1.
 * @throws [NotFoundException] If [id] does not exist.
 */
class SpriteFrames(private val res: Resources, private val id: Int,
                   private val rows: Int, private val columns: Int) {

    /**
     * Load the sprite frames and start timing.
     */
    var frames: Array<Bitmap>
        private set

    /**
     * Informs the number os images available for the sprite animation.
     */
    var numberOfFrames :Int = 0
    private set

    /**
     * The height of each frame image used in the animation.
     */
    var frameHeight:Int = 0
        private set

    /**
     * The width of each frame image used in the animation.
     */
    var frameWidth:Int = 0
        private set

    /**
     * Get the image to be sliced from resource.
     */
    init {
        // Validate the parameters
        if (rows < 1) throw IllegalArgumentException("The number of rows must be higher than zero: w=%s".format(rows))
        if (columns < 1) throw IllegalArgumentException("The number of columns must be higher than zero: w=%s".format(columns))
        // Get the sprite collection image from resource
        val spriteCollection: Bitmap? = BitmapFactory.decodeResource(res, id)
        if (null == spriteCollection) throw Resources.NotFoundException("Invalid resource id: %d".format(id))
        // Extract the frames from the sprite collection image.
        frameWidth = spriteCollection.width / columns
        frameHeight = spriteCollection.height / rows
        numberOfFrames = rows * columns
        this.frames = Array(numberOfFrames, { i ->
            val c = i % columns
            val r = i / columns
            Bitmap.createBitmap(spriteCollection, c * frameWidth, r * frameHeight, frameWidth, frameHeight)
        })
    }

    /**
     * Identifies this class to help on debug.
     * @return Informs the sprite characteristics.
     */
    override fun toString(): String {
        return "rows=$rows - columns=$columns - frameHeight=$frameHeight - frameWidth=$frameWidth"
    }
}