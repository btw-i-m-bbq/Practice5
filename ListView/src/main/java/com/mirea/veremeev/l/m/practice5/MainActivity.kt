package com.mirea.veremeev.l.m.practice5

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    lateinit var sensorManager : SensorManager
    lateinit var listCountSensor : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listCountSensor = findViewById(R.id.list_view)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        val arrayList: ArrayList<HashMap<String?, Any?>> = ArrayList()
        var sensorTypeList: HashMap<String?, Any?>
        for (i in sensors.indices) {
            sensorTypeList = HashMap()
            sensorTypeList["Name"] = sensors[i].getName()
            sensorTypeList["Value"] = sensors[i].getMaximumRange()
            arrayList.add(sensorTypeList)
        }

        val mHistory = SimpleAdapter(
            this, arrayList, android.R.layout.simple_list_item_2, arrayOf("Name", "Value"), intArrayOf(
                android.R.id.text1, android.R.id.text2
            )
        )
        listCountSensor.adapter = mHistory
    }
}