package br.com.conseng.myfirstgame

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * @constructor Inicia a aplicação do jogo
 */
class FullscreenActivity : AppCompatActivity() {

//    /** ACCELEROMETER
//     * Called ONLY when the accuracy of the registered sensor has changed.  Unlike
//     * @param accuracy The new accuracy of this sensor, one of "SensorManager.SENSOR_STATUS_*"
//     */
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//    }

//    /**  ACCELEROMETER
//     * Called when there is a new sensor event.
//     * @param [event] Values from accelerometer sensor All values are in SI units (m/s^2)
//     *                values[0]: Acceleration minus Gx on the x-axis
//     *                values[1]: Acceleration minus Gy on the y-axis
//     *                values[2]: Acceleration minus Gz on the z-axis
//     * @since Note that "on changed" is somewhat of a misnomer, as this will also be called if we
//     * have a new reading from a sensor with the exact same sensor values (but a newer timestamp).
//     * @see [https://developer.android.com/reference/android/hardware/SensorEvent.html]
//     */
//    override fun onSensorChanged(event: SensorEvent?) {
////        val x: Float = event!!.values[0]
////        val y: Float = event.values[1]
////        val z: Float = event.values[2]
////        txtAccel.text = "[x,y,z]\n x=%.3f\n y=%.3f\n z=%.3f".format(x, y, z)
//    }

//    /**
//     * Called when a touch event is dispatched to a view.
//     * @param [v] The view the touch event has been dispatched to.
//     * @param [event] The MotionEvent object containing full information about the event.
//     * @return "True", if the listener has consumed the event, false otherwise.
//     */
//    fun onTouch(v: View, event: MotionEvent): Boolean {
////        val x = event.getX().toInt()
////        val y = event.getY().toInt()
////            coordenadas.text = "[x,y] x=%d y=%d".format(x, y)
//        return true
//    }

    /**
     * Set for full screen without the title.
     * On Manifest, set screen orientation to landscape.
     * @paran [savedInstanceState] If the activity is being re-initialized after previously being
     *        shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *        Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set our game to full screen mode
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Set no title on screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(GameView(this))
    }
}
