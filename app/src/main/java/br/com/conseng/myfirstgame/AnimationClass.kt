package br.com.conseng.myfirstgame

import android.graphics.Bitmap

/**
 * Execute the sprite animation on the screen.
 * @constructor Extracts the individual frames from the collection bitmap and get ready for sprite animation.
 * @param [spriteSheet] Collection bitmap with all character frames.
 * @param [w] Individual character width in pixels.
 * @param [h] Individual character height in pixels.
 * @param [numberOfFrames] Number of frames.
 * @throws [IllegalArgumentException] If [numberOfFrames], [w] or [h] is negative or zero.
 */
class AnimationClass(private val spriteSheet: Bitmap, private val w: Int, private val h: Int, private val numberOfFrames: Int) {
    /**
     * Saves the initial time to control the getBitmap change cadence.
     */
    private var startTime: Long = 0

    /**
     * Load the sprite frames and start timing.
     */
    private var frames: Array<Bitmap>

    /**
     * Extracts the individual frames from the collection bitmap and get ready for sprite animation.
     */
    init {
        // Load the animation frames, extracting slice-by-slice from the bitmap, in the animation class.
        this.frames = Array(numberOfFrames, { i -> Bitmap.createBitmap(spriteSheet, i * w, 0, w, h) })
        this.startTime = System.nanoTime()
    }

    /**
     * The getBitmap index current shown in the sprite.
     * Implements the work-around on the getBitmap index:
     *    Any value lower than zero, goes to last valid index.
     *    Any attempt to set a value higher than [frames] array boundary, goes to first valid index.
     * @throws [IllegalArgumentException] If the new index is negative or higher than sprite array size.
     */
    var frameIndex = 0
        set(value) {
            when {
                value < 0 -> {
                    throw IllegalArgumentException("The new getBitmap index cannot be negative: %d.".format(value))
                }
                value >= frames.size -> {
                    throw IllegalArgumentException("The new getBitmap index (%d) cannot be higher than %d, the last element of the sprite getBitmap array.".format(value, frames.size - 1))
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
        get() = frames[frameIndex]

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
                frameIndex = frames.size - 1
                overlap = true
            }
            next >= frames.size -> {
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
        return "numberOfFrames=$numberOfFrames - frameIndex=$frameIndex - delay=$delay"
    }
}