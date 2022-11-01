package com.example.ugd89_d_10632_project1

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.*
import android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private  var mCamera:  Camera? = null
    private  var mCameraView: CameraView? = null
    private var currentCameraId: Int? = CAMERA_FACING_BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try{
            mCamera = Camera.open()
        }catch (e:Exception){
            Log.d("Error", "Failed to get camera" + e.message)
        }
        if (mCamera != null){
            mCameraView = CameraView(this, mCamera!!)
            val  camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }
        @SuppressLint("MissingInflatedId", "LocalSuppress") val imageClose =
            findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener{view: View? -> System.exit(0)}

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if(proximitySensor == null){
            Toast.makeText(this,"No Proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    lateinit var sensorStatusTv: TextView
    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0f) {
                    //sensorStatusTv.text = "<<NEAR>>"



                    if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        mCamera?.stopPreview();
                        mCamera?.release();
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

                    } else {
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                    mCamera = Camera.open(currentCameraId!!);
                    if (mCamera != null){
                        mCameraView = CameraView(this@MainActivity, mCamera!!)
                        val  camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
                        camera_view.addView(mCameraView)
                    }
                    @SuppressLint("MissingInflatedId", "LocalSuppress") val imageClose =
                        findViewById<View>(R.id.imgClose) as ImageButton
                    imageClose.setOnClickListener{view: View? -> System.exit(0)}
                }

            }

        }
    }
}