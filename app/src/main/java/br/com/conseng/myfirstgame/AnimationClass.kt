package br.com.conseng.myfirstgame

/**************************************************************************************************
 * Histírico da implementação:
 * 20180107     F.Camargo       Criação da classe de animação comum a todos os objetos.
 * 20180108     F.Camargo       Passa a utilizar a classe SpriteFrames como container para as mensagens.
 **************************************************************************************************/

import android.graphics.Bitmap

/**
 * Execute the sprite animation on the screen.
 * @constructor Extracts the individual frames from the collection bitmap and get ready for sprite animation.
 * @param [spriteFrames] Frames to animate the sprite.
 */
class AnimationClass(private val spriteFrames: SpriteFrames) {
    /**
     * Saves the initial time to control the getBitmap change cadence.
     */
    private var startTime: Long = 0

    /**
     * Extracts the individual frames from the collection bitmap and get ready for sprite animation.
     */
    init {
        this.startTime = System.nanoTime()
    }

    /**
     * Informa o numero de frames disponíveis para a animação do sprite.
     */
    val numberOfFrames: Int
        get() = spriteFrames.numberOfFrames

    /**
     * The height of each frame image used in the animation.
     */
    val frameHeight:Int
        get() = spriteFrames.frameHeight

    /**
     * The width of each frame image used in the animation.
     */
    val frameWidth:Int
        get() = spriteFrames.frameWidth

    /**
     * The getBitmap index current shown in the sprite.
     * Implements the work-around on the getBitmap index:
     *    Any value lower than zero, goes to last valid index.
     *    Any attempt to set a value higher than [frames] array boundary, goes to first valid index.
     * @throws [IllegalArgumentException] If the new frame index is negative or higher than sprite array size.
     */
    var frameIndex = 0
        set(value) {
            when {
                value < 0 -> {
                    throw IllegalArgumentException("The new frame index cannot be negative: %d.".format(value))
                }
                value >= numberOfFrames -> {
                    throw IllegalArgumentException("The new frame index (%d) cannot be higher than %d, the last element of the sprite getBitmap array."
                            .format(value, numberOfFrames - 1))
                }
                else -> {
                    field = value
                }
            }
        }

    /**
     * Returns the current sprite getBitmap.
     */
    val getBitmap: Bitmap
        get() = spriteFrames.frames[frameIndex]

    /**
     * Update the strite getBitmap index [frameIndex] by one.
     * @param [plusOne] Increments (if true) or decrements (if false) the getBitmap index by one.
     * @return True on index overlap:
     *        If the new index is higher than last getBitmap, goes back to the first getBitmap.
     *        If the new index is lower than first getBitmap, goes back to the last getBitmap.
     */
    private fun nextFrame(plusOne: Boolean = true): Boolean {
        var overlap = false
        val next = if (plusOne) frameIndex + 1 else frameIndex - 1

        when {
            next < 0 -> {
                frameIndex = numberOfFrames - 1
                overlap = true
            }
            next >= numberOfFrames -> {
                frameIndex = 0
                overlap = true
            }
            else -> frameIndex = next
        }
        return overlap
    }

    /**
     * Animation interval used on this sprite animation.
     * @throws [IllegalArgumentException] The delay cannot be set with a negative value.
     */
    var delay: Int = 0
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("The delay cannot be negative: value=%d".format(value))
            } else {
                field = value
            }
        }

    /**
     * Goes 'true' when all sprites frames had been displayed.
     */
    var playedOnce = false

    /**
     * Update the getBitmap to be displayed after [delay] interval.
     */
    fun update() {
        val elapsed = (System.nanoTime() - startTime) / 1000000
        if (elapsed > delay) {
            playedOnce = playedOnce or nextFrame()      // Makes it 'true' when all frames had been shown.
            this.startTime = System.nanoTime()
        }
    }

    /**
     * Identifies this class to help on debug.
     * @return Informs the number of frames and the current getBitmap.
     */
    override fun toString(): String {
        return "numberOfFrames=${numberOfFrames} - frameIndex=$frameIndex - delay=$delay"
    }
}